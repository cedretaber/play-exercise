package daos

import javax.inject.{Inject, Singleton}
import models.{Companies, Company, User, Users}
import scalikejdbc._

@Singleton
class UserDao @Inject()(Users: Users, Companies: Companies) {

  lazy val u = Users.syntax("u")
  lazy val c = Companies.syntax("c")
  lazy val col = Users.column

  def all()(implicit session: DBSession): Seq[User] =
    withSQL {
      selectFrom(Users as u)
        .orderBy(u.id.asc)
    }.map(Users(u.resultName)).list.apply

  def allWithCompany()(implicit session: DBSession): Seq[(User, Option[Company])] =
    withSQL {
      selectFrom(Users as u)
        .leftJoin(Companies as c).on(u.companyId, c.id)
        .orderBy(u.id.asc)
    }.map { rs =>
      (
        Users(u.resultName)(rs),
        rs.longOpt(c.resultName.id).map(_ => Companies(c.resultName)(rs))
      )
    }.list.apply

  def findById(id: Long)(implicit session: DBSession): Option[User] =
    withSQL {
      selectFrom(Users as u)
        .where.eq(u.id, id)
    }.map(Users(u.resultName)).single.apply

  def insert(name: String, companyId: Option[Long])(implicit session: DBSession): Long =
    withSQL {
      insertInto(Users).namedValues(
        col.name -> name,
        col.companyId -> companyId
      )
    }.updateAndReturnGeneratedKey.apply

  def update(user: User)(implicit session: DBSession): Boolean =
    withSQL {
      QueryDSL.update(Users).set(
        col.name -> user.name,
        col.companyId -> user.companyId
      ).where.eq(col.id, user.id)
    }.update.apply > 0

  def delete(id: Long)(implicit session: DBSession): Boolean =
    withSQL {
      deleteFrom(Users).where.eq(Users.column.id, id)
    }.update.apply > 0
}