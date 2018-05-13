package models

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import scalikejdbc._

case class Company(id: Long, name: String)

@ImplementedBy(classOf[DefaultCompanies])
trait Companies extends SQLSyntaxSupport[Company] {
  override def tableName: String = "companies"

  def apply(rn: ResultName[Company])(wr: WrappedResultSet): Company =
    Company(wr.long(rn.id), wr.string(rn.name))
}

@Singleton
class DefaultCompanies extends Companies