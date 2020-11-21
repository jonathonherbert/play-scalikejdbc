package db

import java.sql.DriverManager

import scala.concurrent.ExecutionContext
import scala.util.Try
import com.whisk.docker.DockerKit
import com.whisk.docker.DockerContainer
import com.whisk.docker.DockerReadyChecker
import com.whisk.docker.DockerContainerState
import com.whisk.docker.DockerCommandExecutor
import com.whisk.docker.impl.spotify.DockerKitSpotify

trait DockerPostgresService extends DockerKit with DockerKitSpotify {
  import scala.concurrent.duration._
  val postgresAdvertisedPort = 5432
  val postgresExposedPort = 44444
  val PostgresUser = "test"
  val PostgresPassword = "test"
  val dbUser = "test"
  val dbPassword = "test"
  val dbUrl = s"jdbc:postgresql://localhost:$postgresExposedPort/?autoReconnect=true&useSSL=false"
  val driver = "org.postgresql.Driver"
  val dockerImage = "postgres:10.7-alpine"

  val postgresContainer = DockerContainer(dockerImage)
    .withPorts((postgresAdvertisedPort, Some(postgresExposedPort)))
    .withEnv(
      s"POSTGRES_USER=$PostgresUser",
      s"POSTGRES_PASSWORD=$PostgresPassword"
    )
    .withReadyChecker(
      new PostgresReadyChecker(
        PostgresUser,
        PostgresPassword,
        Some(postgresExposedPort)
      ).looped(15, 1.second)
    )

  abstract override def dockerContainers: List[DockerContainer] =
    postgresContainer :: super.dockerContainers
}

class PostgresReadyChecker(
    user: String,
    password: String,
    port: Option[Int] = None
) extends DockerReadyChecker {

  override def apply(
      container: DockerContainerState
  )(implicit docker: DockerCommandExecutor, ec: ExecutionContext) =
    container
      .getPorts()
      .map(ports =>
        Try {
          Class.forName("org.postgresql.Driver")
          val url =
            s"jdbc:postgresql://${docker.host}:${port.getOrElse(ports.values.head)}/"
          Option(DriverManager.getConnection(url, user, password))
            .map(_.close)
            .isDefined
        }.getOrElse(false)
      )
}
