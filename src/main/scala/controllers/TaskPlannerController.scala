package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import spray.json.DefaultJsonProtocol.jsonFormat1
import spray.json.{JsNull, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}
import taskPlanner.TaskPlannerService
import taskPlanner.views.{GraphNodeViewModel, GraphViewModel, PlanTasksViewModel}


class TaskPlannerController extends Directives with JsonSupport {
  private val cors = new CorsHandler {}
  private val defaultRoutePrefix = "taskPlanner";

  val routes: Route =
    concat(
      post {
        cors.corsHandler(concat(
          path(defaultRoutePrefix / "lab2") {
            entity(as[PlanTasksViewModel]) { inputModel =>
              parseFirstLabExpression(inputModel)
            }
          },
        ))
      },
      options {
        cors.corsHandler(concat(
          path(defaultRoutePrefix / "lab2") {
            complete(StatusCodes.OK)
          },
        ))
      }
    )

  def parseFirstLabExpression(inputModel: PlanTasksViewModel): Route = {
    val service = new TaskPlannerService
    service.planSecondLab(inputModel)

    complete(StatusCodes.OK)
  }
}
