package daos

import javax.inject.{Inject, Singleton}
import models.{Companies, Company}
import scalikejdbc._

@Singleton
class CompanyDao @Inject()(Companies: Companies) {

  lazy val c = Companies.syntax("c")

  def all()(implicit session: DBSession): Seq[Company] =
    withSQL {
      selectFrom(Companies as c)
        .orderBy(c.id.asc)
    }.map(Companies(c.resultName)).list.apply
}