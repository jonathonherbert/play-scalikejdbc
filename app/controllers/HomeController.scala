package controllers

import play.api._
import play.api.mvc._
import scalikejdbc._
import db.DBRule
import db.DBRule2

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class HomeController(val controllerComponents: ControllerComponents)
    extends BaseController {

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    getCollections()
    Ok(views.html.index())
  }

  def getCollections() = DB readOnly { implicit session =>
    val rules = DBRule2.findAll()
    println(rules)
  }
}
