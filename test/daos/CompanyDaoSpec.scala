package daos

import models.{Companies, Company}
import org.scalatest.{Matchers, fixture}
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.config._
import scalikejdbc._

class CompanyDaoSpec extends fixture.FlatSpec with Matchers with AutoRollback {

  val dbName = 'test

  DBs.setup(dbName)

  override def db(): DB = NamedDB(dbName).toDB

  val company1 = Company(1, "company 1")
  val company2 = Company(2, "company 2")
  val company3 = Company(3, "company 3")

  override def fixture(implicit session: FixtureParam): Unit = {
    for {
      company <- Seq(company1, company2, company3)
    } SQL("""INSERT INTO "companies" VALUES (?, ?)""").bind(company.id, company.name).update.apply
  }

  val dao = new CompanyDao(new Companies {
    override def connectionPoolName: Any = dbName
  })

  "all" should "return all records" in { implicit session =>
    val expected = Seq(company1, company2, company3)
    dao.all() should be(expected)
  }
}
