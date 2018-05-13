package models

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import scalikejdbc._

case class User(id: Long, name: String, companyId: Option[Long])

@ImplementedBy(classOf[DefaultUsers])
trait Users extends SQLSyntaxSupport[User] {
  override def tableName: String = "users"

  def apply(rn: ResultName[User])(wr: WrappedResultSet): User =
    User(wr.long(rn.id), wr.string(rn.name), wr.longOpt(rn.companyId))
}

@Singleton
class DefaultUsers extends Users