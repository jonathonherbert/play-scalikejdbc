package db

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper
import skinny.orm.SkinnyCRUDMapperWithId

case class DBRule2(ruleId: String, name: String, description: String)

object DBRule2 extends SkinnyCRUDMapperWithId[String, DBRule2] {
  override lazy val tableName = "rule"
  override lazy val primaryKeyFieldName = "ruleId"
  override lazy val defaultAlias = createAlias("r")
  override def idToRawValue(id: String) = id
  override def rawValueToId(value: Any) = value.toString
  override def extract(rs: WrappedResultSet, n: ResultName[DBRule2]): DBRule2 =
    new DBRule2(
      ruleId = rs.get(n.ruleId),
      name = rs.get(n.name),
      description = rs.get(n.description)
    )
}
