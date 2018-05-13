package daos

import models.{Companies, Company, User, Users}
import org.scalatest.{Matchers, fixture}
import scalikejdbc.config.DBs
import scalikejdbc.{DB, NamedDB, SQL}
import scalikejdbc.scalatest.AutoRollback

class UserDaoSpec extends fixture.FlatSpec with Matchers with AutoRollback {

  val dbName = 'test

  DBs.setup(dbName)

  override def db(): DB = NamedDB(dbName).toDB

  val company1 = Company(1, "company 1")
  val company2 = Company(2, "company 2")

  val user1 = User(11, "user 1", Some(company1.id))
  val user2 = User(12, "user 2", Some(company1.id))
  val user3 = User(13, "user 3", Some(company2.id))
  val user4 = User(14, "user 4", None)

  override def fixture(implicit session: FixtureParam): Unit = {
    for {
      company <- Seq(company1, company2)
    } SQL("""INSERT INTO "companies" VALUES (?, ?)""").bind(company.id, company.name).update.apply

    for {
      user <- Seq(user1, user2, user3, user4)
    } SQL("""INSERT INTO "users" VALUES (?, ?, ?)""").bind(user.id, user.name, user.companyId).update.apply
  }

  val dao = new UserDao(
    new Users {
      override def connectionPoolName: Any = dbName
    },
    new Companies {
      override def connectionPoolName: Any = dbName
    }
  )

  "all" should "return all users" in { implicit session =>
    val expected = Seq(user1, user2, user3, user4)
    dao.all should be(expected)
  }

  "allWithCompany" should "return all users with her company" in { implicit session =>
    val expected = Seq(user1 -> Some(company1), user2 -> Some(company1), user3 -> Some(company2), user4 -> None)
    dao.allWithCompany should be(expected)
  }

  "findById" should "return the user" in { implicit session =>
    dao.findById(user1.id) should be(Some(user1))
  }

  it should "return None when the id is invalid" in { implicit session =>
    dao.findById(114514L) should be(None)
  }

  "insert" should "insert the user" in { implicit session =>
    val newUser = User(42, "new user", Some(company2.id))

    val id = dao.insert(newUser.name, newUser.companyId)
    val Some(User(_, name, companyId)) = dao.findById(id)
    name should be(newUser.name)
    companyId should be(newUser.companyId)
  }

  "update" should "change the data in DB" in { implicit session =>
    val newUser = user1.copy(name = "new user 1", companyId = None)
    dao.update(newUser) should be(true)

    val Some(user) = dao.findById(user1.id)
    user should not be(user1)
  }

  "delete" should "remove the user from DB" in { implicit session =>
    dao.findById(user2.id) should be(Some(user2))
    dao.delete(user2.id) should be(true)
    dao.findById(user2.id) should be(None)
  }
}
