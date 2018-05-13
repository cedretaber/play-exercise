package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scalikejdbc._
import daos._
import models._

@Singleton
class JsonController @Inject()(components: ControllerComponents, UserDao: UserDao, CompanyDao: CompanyDao)
  extends AbstractController(components) {

  import JsonController._

  def list = Action { implicit req =>
    val users =
      DB.readOnly { implicit session =>
        UserDao.all()
      }
    Ok(Json.obj("users" -> users))
  }

  def create = Action(parse.json) { implicit req =>
    req.body.validate[UserForm].map { form =>
      DB.localTx { implicit session =>
        UserDao.insert(form.name, form.companyId)
      }
      Ok(Json.obj("result" -> "success"))
    }.recoverTotal { err =>
      BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(err)))
    }
  }

  def update = Action(parse.json) { implicit req =>
    req.body.validate[UserForm].map { form =>
      DB.localTx { implicit session =>
        UserDao.update(User(form.id.get, form.name, form.companyId))
      }
      Ok(Json.obj("result" -> "success"))
    }.recoverTotal { err =>
      BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(err)))
    }
  }

  def remove(id: Long) = Action { implicit req =>
    DB.localTx { implicit session =>
      UserDao.delete(id)
    }
    Ok(Json.obj("result" -> "success"))
  }
}

object JsonController {
  implicit val userWrites: Writes[User] = Json.writes[User]

  case class UserForm(id: Option[Long], name: String, companyId: Option[Long])

  implicit val userFormRead: Reads[UserForm] = Json.reads[UserForm]
}