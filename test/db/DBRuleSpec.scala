package db

import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.scalatest.AutoRollback
import org.scalatest.fixture.FlatSpec
import org.scalatest.matchers.should.Matchers
import java.util.UUID
import org.scalatest.flatspec.FixtureAnyFlatSpec
import com.whisk.docker.scalatest.DockerTestKit

class DBRuleSpec
    extends FixtureAnyFlatSpec
    with Matchers
    with AutoRollback
    with DBService
      with DBEvolutions {

  val r = DBRule.syntax("r")

  override def fixture(implicit session: DBSession) = {
    sql"insert into rule values ('3968f85a-d9ce-4bd8-b67f-64633a58d1c1', 'A name', 'A description')"
      .update()
      .apply()
    sql"insert into feedback(id, rule_id, email, description) values (1, '3968f85a-d9ce-4bd8-b67f-64633a58d1c1', 'example@test.com', 'A description')"
      .update()
      .apply()
  }

  behavior of "DBRule"

  it should "find by primary keys" in { implicit session =>
    val maybeFound =
      DBRule.find(UUID.fromString("3968f85a-d9ce-4bd8-b67f-64633a58d1c1"))
    maybeFound.isDefined shouldBe true
  }

  it should "find by primary keys with feedback" in { implicit session =>
    val maybeFound =
      DBRule.findWithFeedback(
        UUID.fromString("3968f85a-d9ce-4bd8-b67f-64633a58d1c1")
      )
    maybeFound.isDefined shouldBe true
    maybeFound.get.feedback.size should be > 0
  }

  it should "find by where clauses" in { implicit session =>
    val maybeFound = DBRule.findBy(sqls.eq(r.name, "A name"))
    maybeFound.isDefined shouldBe true
  }
  it should "find all records" in { implicit session =>
    val allResults = DBRule.findAll()
    allResults.size should be > 0
  }
  it should "count all records" in { implicit session =>
    val count = DBRule.countAll()
    count should be > 0L
  }
  it should "find all by where clauses" in { implicit session =>
    val results = DBRule.findAllBy(sqls.eq(r.name, "A name"))
    results.size should be > 0
  }
  it should "count by where clauses" in { implicit session =>
    val count = DBRule.countBy(sqls.eq(r.name, "A name"))
    count should be > 0L
  }
  it should "create new record" in { implicit session =>
    val created = DBRule.create(name = "MyString", description = "MyString")
    val count = DBRule.countBy(sqls.eq(r.name, "MyString"))
    count should be > 0L
  }
  it should "save a record" in { implicit session =>
    val entity = DBRule.findAll().head
    // TODO modify something
    val modified = entity.copy(name = "New name!")
    val updated = DBRule.save(modified)
    updated should not be entity
  }
  it should "destroy a record" in { implicit session =>
    val entity = DBRule.findAll().head
    val deleted = DBRule.destroy(entity) == 1
    deleted should equal(true)
    val shouldBeNone = DBRule.find(entity.ruleId)
    shouldBeNone.isDefined shouldBe false
  }
  it should "perform batch insert" in { implicit session =>
    val entities = DBRule.findAll()
    entities.foreach(e => DBRule.destroy(e))
    val batchInserted = DBRule.batchInsert(entities)
    batchInserted.size should be > 0
  }
}
