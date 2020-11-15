package db

import scalikejdbc._
import java.util.UUID

case class DBFeedback(
    id: Int,
    ruleId: UUID,
    email: String,
    description: Option[String] = None
) {

  def save()(implicit session: DBSession): DBFeedback =
    DBFeedback.save(this)(session)

  def destroy()(implicit session: DBSession): Int =
    DBFeedback.destroy(this)(session)

}

object DBFeedback extends SQLSyntaxSupport[DBFeedback] {

  override val tableName = "feedback"

  override val columns = Seq("id", "rule_id", "email", "description")

  val f = DBFeedback.syntax("f")

  implicit val uuidParameterBinderFactory: ParameterBinderFactory[UUID] =
    ParameterBinderFactory { value => (stmt, idx) =>
      stmt.setObject(idx, value)
    }

  def apply(f: SyntaxProvider[DBFeedback])(rs: WrappedResultSet): DBFeedback =
    apply(f.resultName)(rs)
  def apply(f: ResultName[DBFeedback])(rs: WrappedResultSet): DBFeedback =
    new DBFeedback(
      id = rs.get(f.id),
      ruleId = UUID.fromString(rs.get(f.ruleId)),
      email = rs.get(f.email),
      description = rs.get(f.description)
    )

  def opt(
      f: SyntaxProvider[DBFeedback]
  )(rs: WrappedResultSet): Option[DBFeedback] =
    rs.longOpt(f.resultName.id).map(_ => DBFeedback(f)(rs))

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[DBFeedback] = {
    sql"""select ${f.result.*} from ${DBFeedback as f} where ${f.id} = ${id}"""
      .map(DBFeedback(f.resultName))
      .single()
      .apply()
  }

  def findAll()(implicit session: DBSession): List[DBFeedback] = {
    sql"""select ${f.result.*} from ${DBFeedback as f}"""
      .map(DBFeedback(f.resultName))
      .list()
      .apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    sql"""select count(1) from ${DBFeedback.table}"""
      .map(rs => rs.long(1))
      .single()
      .apply()
      .get
  }

  def findBy(
      where: SQLSyntax
  )(implicit session: DBSession): Option[DBFeedback] = {
    sql"""select ${f.result.*} from ${DBFeedback as f} where ${where}"""
      .map(DBFeedback(f.resultName))
      .single()
      .apply()
  }

  def findAllBy(
      where: SQLSyntax
  )(implicit session: DBSession): List[DBFeedback] = {
    sql"""select ${f.result.*} from ${DBFeedback as f} where ${where}"""
      .map(DBFeedback(f.resultName))
      .list()
      .apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    sql"""select count(1) from ${DBFeedback as f} where ${where}"""
      .map(_.long(1))
      .single()
      .apply()
      .get
  }

  def create(ruleId: UUID, email: String, description: Option[String] = None)(
      implicit session: DBSession
  ): DBFeedback = {
    val generatedKey = sql"""
      insert into ${DBFeedback.table} (
        ${column.ruleId},
        ${column.email},
        ${column.description}
      ) values (
        ${ruleId},
        ${email},
        ${description}
      )
      """.updateAndReturnGeneratedKey().apply()

    DBFeedback(
      id = generatedKey.toInt,
      ruleId = ruleId,
      email = email,
      description = description
    )
  }

  def batchInsert(
      entities: collection.Seq[DBFeedback]
  )(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("ruleId") -> entity.ruleId,
        Symbol("email") -> entity.email,
        Symbol("description") -> entity.description
      )
    )
    SQL("""insert into feedback(
      rule_id,
      email,
      description
    ) values (
      {ruleId},
      {email},
      {description}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: DBFeedback)(implicit session: DBSession): DBFeedback = {
    sql"""
      update
        ${DBFeedback.table}
      set
        ${column.id} = ${entity.id},
        ${column.ruleId} = ${entity.ruleId},
        ${column.email} = ${entity.email},
        ${column.description} = ${entity.description}
      where
        ${column.id} = ${entity.id}
      """.update().apply()
    entity
  }

  def destroy(entity: DBFeedback)(implicit session: DBSession): Int = {
    sql"""delete from ${DBFeedback.table} where ${column.id} = ${entity.id}"""
      .update()
      .apply()
  }

}
