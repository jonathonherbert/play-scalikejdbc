package db

import scalikejdbc._
import java.util.UUID

case class DBRule(ruleId: UUID, name: String, description: String, feedback: List[DBFeedback] = Nil) {

  def save()(implicit session: DBSession = DBRule.autoSession): DBRule =
    DBRule.save(this)(session)

  def destroy()(implicit session: DBSession = DBRule.autoSession): Int =
    DBRule.destroy(this)(session)
}

object DBRule extends SQLSyntaxSupport[DBRule] {

  override val tableName = "rule"

  override val columns = Seq("rule_id", "name", "description")

  def apply(r: SyntaxProvider[DBRule])(rs: WrappedResultSet): DBRule =
    apply(r.resultName)(rs)
  def apply(r: ResultName[DBRule])(rs: WrappedResultSet): DBRule = new DBRule(
    ruleId = UUID.fromString(rs.get(r.ruleId)),
    name = rs.get(r.name),
    description = rs.get(r.description)
  )

  val r = DBRule.syntax("r")
  val f = DBFeedback.syntax("f")

  override val autoSession = AutoSession

  implicit val uuidParameterBinderFactory: ParameterBinderFactory[UUID] =
    ParameterBinderFactory { value => (stmt, idx) =>
      stmt.setObject(idx, value)
    }

  def find(ruleId: UUID)(implicit
      session: DBSession = autoSession
  ): Option[DBRule] = {
    withSQL {
      select
        .from(DBRule as r)
        .where
        .eq(r.ruleId, ruleId)
    }.map(DBRule(r.resultName)).single().apply()
  }

  def findWithFeedback(ruleId: UUID)(implicit
      session: DBSession = autoSession
  ): Option[DBRule] = {
    withSQL {
      select
        .from(DBRule as r)
        .leftJoin(DBFeedback as f)
        .on(r.ruleId, f.ruleId)
        .where
        .eq(r.ruleId, ruleId)
    }.map(DBRule(r.resultName))
      .one(DBRule(r))
      .toMany(DBFeedback.opt(f))
      .map { (rule, feedbacks) => rule.copy(feedback = feedbacks.toList)}
      .single()
      .apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[DBRule] = {
    withSQL(select.from(DBRule as r)).map(DBRule(r.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(DBRule as r))
      .map(rs => rs.long(1))
      .single()
      .apply()
      .get
  }

  def findBy(
      where: SQLSyntax
  )(implicit session: DBSession = autoSession): Option[DBRule] = {
    withSQL {
      select.from(DBRule as r).where.append(where)
    }.map(DBRule(r.resultName)).single().apply()
  }

  def findAllBy(
      where: SQLSyntax
  )(implicit session: DBSession = autoSession): List[DBRule] = {
    withSQL {
      select.from(DBRule as r).where.append(where)
    }.map(DBRule(r.resultName)).list().apply()
  }

  def countBy(
      where: SQLSyntax
  )(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(DBRule as r).where.append(where)
    }.map(_.long(1)).single().apply().get
  }

  def create(name: String, description: String)(implicit
      session: DBSession = autoSession
  ) = {
    val key = withSQL {
      insert
        .into(DBRule)
        .namedValues(
          column.name -> name,
          column.description -> description
        )
    }.update().apply()
  }

  def batchInsert(
      entities: collection.Seq[DBRule]
  )(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        Symbol("ruleId") -> entity.ruleId,
        Symbol("name") -> entity.name,
        Symbol("description") -> entity.description
      )
    )
    SQL("""insert into rule(
      rule_id,
      name,
      description
    ) values (
      {ruleId},
      {name},
      {description}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(
      entity: DBRule
  )(implicit session: DBSession = autoSession): DBRule = {
    withSQL {
      update(DBRule)
        .set(
          column.name -> entity.name,
          column.description -> entity.description
        )
        .where
        .eq(column.ruleId, entity.ruleId)
    }.update().apply()
    entity
  }

  def destroy(
      entity: DBRule
  )(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete
        .from(DBRule)
        .where
        .eq(column.ruleId, entity.ruleId)
        .and
        .eq(column.name, entity.name)
        .and
        .eq(column.description, entity.description)
    }.update().apply()
  }
}
