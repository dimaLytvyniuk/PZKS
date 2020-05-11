package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import spray.json._
import taskPlanner.TaskPlannerService
import taskPlanner.views.{EdgeViewModel, GraphNodeViewModel, GraphViewModel, PlanTasksViewModel}

trait TaskPlannerJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val graphNodeViewModelFormat = jsonFormat3(GraphNodeViewModel)
  implicit val edgeViewModelFormat = jsonFormat3(EdgeViewModel)
  implicit val graphViewModelFormat = jsonFormat2(GraphViewModel)
  implicit val planTasksViewModelFormat = jsonFormat2(PlanTasksViewModel)
}

class TaskPlannerController extends Directives with TaskPlannerJsonSupport {
  private val cors = new CorsHandler {}
  private val defaultRoutePrefix = "taskPlanner";

  val routes: Route =
    concat(
      post {
        cors.corsHandler(concat(
          path(defaultRoutePrefix / "lab2") {
            entity(as[GraphViewModel]) { inputModel =>
              secondLabHandler(inputModel)
            }
          },
          path(defaultRoutePrefix / "lab3") {
            entity(as[GraphViewModel]) { inputModel =>
              thirdLabHandler(inputModel)
            }
          },
        ))
      },
      options {
        cors.corsHandler(concat(
          path(defaultRoutePrefix / "lab2") {
            complete(StatusCodes.OK)
          },
          path(defaultRoutePrefix / "lab3") {
            complete(StatusCodes.OK)
          },
        ))
      }
    )

  def secondLabHandler(inputModel: GraphViewModel): Route = {
    val service = new TaskPlannerService
    val queue = service.getSecondLabQueue(inputModel)

    complete(queue)
  }

  def thirdLabHandler(inputModel: GraphViewModel): Route = {
    val service = new TaskPlannerService
    val queue = service.getThirdLabQueue(inputModel)

    complete(queue)
  }

  def sixthLabHandler(inputModel: PlanTasksViewModel): Route = {
    val service = new TaskPlannerService
    val queue = service.planSixthLab(inputModel)

    complete(queue)
  }
}
