package db

import scalikejdbc.ConnectionPool
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Suite}
import play.api.db.{Database, Databases}

trait DBService extends DockerPostgresService with DockerTestKit with BeforeAndAfterAll with BeforeAndAfter { self: Suite =>
  var database: Database = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    database = Databases(driver, dbUrl, config = Map(
      "username" -> dbUser,
      "password" -> dbPassword
    ))

    Class.forName("org.postgresql.Driver")
    ConnectionPool.singleton(dbUrl, dbUser, dbPassword)
  }
}
