package db

import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import java.util.UUID
import scalikejdbc.config.DBs
import com.whisk.docker.scalatest.DockerTestKit

class DBFeedbackSpec
    extends FixtureAnyFlatSpec
    with Matchers
    with AutoRollback
    with DBService
    with DBEvolutions {

  override def fixture(implicit session: DBSession) = {
    sql"insert into feedback(rule_id, email, description) values ('3968f85a-d9ce-4bd8-b67f-64633a58d1c1', 'example@test.com', 'A description')"
      .updateAndReturnGeneratedKey()
      .apply()
  }

  behavior of "DBFeedback"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = DBFeedback.find(1)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = DBFeedback.findBy(sqls"id = ${1}")
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = DBFeedback.findAll()
    allResults.size should be > (0)
  }
  it should "count all records" in { implicit session =>
    val count = DBFeedback.countAll()
    count should be > (0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = DBFeedback.findAllBy(sqls"id = ${1}")
    results.size should be > (0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = DBFeedback.countBy(sqls"id = ${1}")
    count should be > (0L)
  }
  it should "create new record" in { implicit session =>
    val created = DBFeedback.create(
      ruleId = UUID.fromString("d1e6271f-08e6-45e1-86f8-57fdb9ed80ce"),
      email = "MyString"
    )
    created should not be (null)
  }
  it should "save a record" in { implicit session =>
    val entity = DBFeedback.findAll().head
    // TODO modify something
    val modified = entity.copy(description = Some("Another description"))
    val updated = DBFeedback.save(modified)
    updated should not equal (entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = DBFeedback.findAll().head
    val deleted = DBFeedback.destroy(entity)
    deleted should be(1)
    val shouldBeNone = DBFeedback.find(1)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = DBFeedback.findAll()
    entities.foreach(e => DBFeedback.destroy(e))
    val batchInserted = DBFeedback.batchInsert(entities)
    batchInserted.size should be > (0)
  }
}
