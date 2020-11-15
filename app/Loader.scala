import scalikejdbc._
import scalikejdbc.config._

import play.api._
import play.api.routing.Router
import _root_.controllers.AssetsComponents
import _root_.controllers.HomeController
import router.Routes

class Loader extends ApplicationLoader {
  private var components: Components = _

  def load(context: ApplicationLoader.Context): Application = {
    components = new Components(context)
    components.application
  }
}

class Components(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
  with play.filters.HttpFiltersComponents
  with AssetsComponents {

  DBs.setupAll()

  lazy val homeController = new HomeController(controllerComponents)

  lazy val router: Router = new Routes(httpErrorHandler, homeController, assets)
}
