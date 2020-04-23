package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import spray.json._
import taskPlanner.TaskPlannerService
import taskPlanner.views.{EdgeViewModel, GraphNodeViewModel, GraphViewModel, PlanTasksViewModel}

trait TaskPlannerJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object PlanTasksViewModelJsonFormat extends RootJsonFormat[PlanTasksViewModel] {
    def write(planTasksViewModel: PlanTasksViewModel) = JsObject(
      "graphTask" -> planTasksViewModel.graphTask.toJson,
      "graphSystem" -> planTasksViewModel.graphSystem.toJson
    )

    def read(value: JsValue) = {
      val graphTask = fromField[GraphViewModel](value, "graphTask")
      val graphSystem = fromField[GraphViewModel](value, "graphSystem")

      PlanTasksViewModel(graphTask, graphSystem)
    }
  }

  implicit object GraphViewModelJsonFormat extends RootJsonFormat[GraphViewModel] {
    def write(graph: GraphViewModel) = {
      JsObject(
        "nodes" -> graph.nodes.toJson,
        "edges" -> graph.edges.toJson,
      )
    }

    def read(value: JsValue) = {
      val nodes = fromField[Array[GraphNodeViewModel]](value, "nodes")
      val edges = fromField[Array[EdgeViewModel]](value, "edges")

      GraphViewModel(nodes, edges)
    }
  }

  implicit object GraphNodeViewModelJsonFormat extends RootJsonFormat[GraphNodeViewModel] {
    def write(graphNodeViewModel: GraphNodeViewModel) = {
      val weightJs = if (graphNodeViewModel.weight.isDefined) JsNumber(graphNodeViewModel.weight.get) else JsNull

      JsObject(
        "from" -> JsString(graphNodeViewModel.from),
        "to" -> JsString(graphNodeViewModel.to),
        "weight" -> weightJs)
    }

    def read(value: JsValue) = {
      val from = fromField[String](value, "from")
      val to = fromField[String](value, "to")
      val weight = fromField[Option[Int]](value, "weight")

      GraphNodeViewModel(from, to, weight)
    }
  }

  implicit object EdgeViewModelJsonFormat extends RootJsonFormat[EdgeViewModel] {
    def write(edgeViewModel: EdgeViewModel) = {
      val weightJs = if (edgeViewModel.weight.isDefined) JsNumber(edgeViewModel.weight.get) else JsNull

      JsObject(
        "from" -> JsString(edgeViewModel.from),
        "to" -> JsString(edgeViewModel.to),
        "weight" -> weightJs)
    }

    def read(value: JsValue) = {
      val from = fromField[String](value, "from")
      val to = fromField[String](value, "to")
      val weight = fromField[Option[Int]](value, "weight")

      EdgeViewModel(from, to, weight)
    }
  }
}

class TaskPlannerController extends Directives with TaskPlannerJsonSupport {
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
