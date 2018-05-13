package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import javax.inject.{Inject, Singleton}
import scalikejdbc._
import daos._
import models._

@Singleton
class UserController @Inject()(components: MessagesControllerComponents, UserDao: UserDao, CompanyDao: CompanyDao)
  extends MessagesAbstractController(components) {

  import UserController._

  def list = Action { implicit req =>
    val users =
      DB.readOnly { implicit session =>
        UserDao.allWithCompany()
      }
    Ok(views.html.users.list(users))
  }

  def edit(id: Option[Long]) = Action { implicit req =>
    DB.readOnly { implicit session =>
      val form = id match {
        case None =>
          userForm
        case Some(id) =>
          val user = UserDao.findById(id).get
          userForm.fill(UserForm(Some(id), user.name, user.companyId))
      }

      val companies = CompanyDao.all()

      Ok(views.html.users.edit(form, companies))
    }
  }

  def create = Action { implicit req =>
    DB.localTx { implicit session =>
      userForm.bindFromRequest.fold(
        error => {
          BadRequest(views.html.users.edit(error, CompanyDao.all()))
        },
        form => {
          UserDao.insert(form.name, form.companyId)
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  def update = Action { implicit req =>
    DB.localTx { implicit session =>
      userForm.bindFromRequest.fold(
        error => {
          val companies = CompanyDao.all()
          BadRequest(views.html.users.edit(error, companies))
        },
        form => {
          UserDao.update(User(form.id.get, form.name, form.companyId))
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  def remove(id: Long) = Action { implicit req =>
    DB.localTx { implicit session =>
      UserDao.delete(id)
    }
    Redirect(routes.UserController.list)
  }
}

object UserController {
  case class UserForm(id: Option[Long], name: String, companyId: Option[Long])

  val userForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(longNumber)
    )(UserForm.apply)(UserForm.unapply)
  )
}