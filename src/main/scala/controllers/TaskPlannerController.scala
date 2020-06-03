package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import spray.json._
import taskPlanner.TaskPlannerService
import taskPlanner.views.{CombinedStatisticViewModel, EdgeViewModel, GraphNodeViewModel, GraphViewModel, PlanTasksViewModel, StatisticViewModel}

trait TaskPlannerJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val graphNodeViewModelFormat = jsonFormat3(GraphNodeViewModel)
  implicit val edgeViewModelFormat = jsonFormat3(EdgeViewModel)
  implicit val graphViewModelFormat = jsonFormat2(GraphViewModel)
  implicit val planTasksViewModelFormat = jsonFormat2(PlanTasksViewModel)
  implicit val statisticViewModelFormat = jsonFormat7(StatisticViewModel)
  implicit val combinedStatisticViewModelFormat = jsonFormat2(CombinedStatisticViewModel)
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
          path(defaultRoutePrefix / "lab6") {
            entity(as[PlanTasksViewModel]) { inputModel =>
              sixthLabHandler(inputModel)
            }
          },
          path(defaultRoutePrefix / "rgr") {
            entity(as[Array[PlanTasksViewModel]]) { inputModel =>
              rgrHandler(inputModel)
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
          path(defaultRoutePrefix / "lab6") {
            complete(StatusCodes.OK)
          },
          path(defaultRoutePrefix / "rgr") {
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
    val result = service.planSixthLab(inputModel)

    complete(result)
  }

  def rgrHandler(inputModel: Array[PlanTasksViewModel]): Route = {
    val service = new TaskPlannerService
    val result = service.rgrAnalyze(inputModel)

    complete(result)
  }
}
