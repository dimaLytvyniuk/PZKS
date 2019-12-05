package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.{Directives, Route}
import parsing.ExpressionParsingService
import parsing.models.views._
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.model.StatusCodes
import pipelines.PipelineService
import pipelines.views.{CalculationStatisticViewModel, PipelineContainerViewModel}

trait JsonSupport {
  implicit val inputExpressionModelFormat = jsonFormat1(InputExpressionModel)
  implicit val exceptionModelFormat = jsonFormat1(ExceptionModel)

  implicit object ExpressionNodeViewModelJsonFormat extends RootJsonFormat[ExpressionNodeViewModel] {
    def write(node: ExpressionNodeViewModel) = {
      val leftNode = if (node.leftNode != null) write(node.leftNode) else JsNull
      val rightNode = if (node.rightNode != null) write(node.rightNode) else JsNull

      JsObject(
        "nodeType" -> JsString(node.nodeType),
        "level" -> JsNumber(node.level),
        "value" -> JsString(node.value),
        "leftNode" -> leftNode,
        "rightNode" -> rightNode,
      )
    }

    def read(value: JsValue) = null
  }

  implicit object ExpressionTreeViewModelJsonFormat extends RootJsonFormat[ExpressionTreeViewModel] {
    def write(tree: ExpressionTreeViewModel) = JsObject(
      "head" -> tree.head.toJson,
      "supportedFunctions" -> tree.supportedFunctions.toJson,
      "evaluatedResults" -> tree.evaluatedResults.toJson,
    )

    def read(value: JsValue) = null
  }

  implicit object OutputParsedExpressionModelJsonFormat extends RootJsonFormat[OutputParsedExpressionModel] {
    def write(outputParsedExpressionModel: OutputParsedExpressionModel) = {
      val expressionTree = if (outputParsedExpressionModel.expressionTree != null) outputParsedExpressionModel.expressionTree.toJson else JsNull
      val exceptionModel = if (outputParsedExpressionModel.exceptionModel != null) outputParsedExpressionModel.exceptionModel.toJson else JsNull
      val evaluatedResult = if (outputParsedExpressionModel.evaluatedResult != null) JsString(outputParsedExpressionModel.evaluatedResult) else JsNull

      JsObject(
        "expressionTree" -> expressionTree,
        "exceptionModel" -> exceptionModel,
        "evaluatedResult" -> evaluatedResult,
        "inputExpression" -> JsString(outputParsedExpressionModel.inputExpression)
      )
    }

    def read(value: JsValue) = null
  }

  implicit object CalculationStatisticViewModelJsonFormat extends RootJsonFormat[CalculationStatisticViewModel] {
    def write(calculationStatistic: CalculationStatisticViewModel) = {
      if (calculationStatistic != null) {
        JsObject(
          "sequenceWorkingTime" -> calculationStatistic.sequenceWorkingTime.toJson,
          "pipelineWorkingTime" -> calculationStatistic.pipelineWorkingTime.toJson,
          "boost" -> calculationStatistic.boost.toJson,
          "efficiency" -> calculationStatistic.efficiency.toJson,
        )
      } else {
        null
      }
    }

    def read(value: JsValue) = null
  }

  implicit object PipelineContainerViewModelJsonFormat extends RootJsonFormat[PipelineContainerViewModel] {
    def write(pipelineContainerViewModel: PipelineContainerViewModel) = {
      if (pipelineContainerViewModel != null) {
        val tactSteps = if (pipelineContainerViewModel.tactSteps != null) pipelineContainerViewModel.tactSteps.toJson else JsNull
        val expressionTree = if (pipelineContainerViewModel.expressionTree != null) pipelineContainerViewModel.expressionTree.toJson else JsNull
        val calculationStatistic = if (pipelineContainerViewModel.calculationStatistic != null) pipelineContainerViewModel.calculationStatistic.toJson else JsNull

        JsObject(
          "tactSteps" -> tactSteps,
          "expressionTree" -> expressionTree,
          "calculationStatistic" -> calculationStatistic,
        )
      } else {
        null
      }
    }

    def read(value: JsValue) = null
  }
}

class ExpressionController extends Directives with JsonSupport {
  private val cors = new CorsHandler {}

  val routes: Route =
    concat(
      post {
        cors.corsHandler(concat(
        path("expression" / "lab1") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            parseFirstLabExpression(inputExpressionModel)
          }
        },
        path("expression" / "lab2") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            parseSecondLabExpression(inputExpressionModel)
          }
        },
        path("expression" / "lab3") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            parseThirdLabExpression(inputExpressionModel)
          }
        },
        path("expression" / "lab4") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            parseFourthLabExpression(inputExpressionModel)
          }
        },
        path("expression" / "lab5") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            emulateFifthLabCalculation(inputExpressionModel)
          }
        },
        ))
      },
      options {
        cors.corsHandler(concat(
          path("expression" / "lab1") {
            complete(StatusCodes.OK)
          },
          path("expression" / "lab2") {
            complete(StatusCodes.OK)
          },
          path("expression" / "lab3") {
            complete(StatusCodes.OK)
          },
          path("expression" / "lab4") {
            complete(StatusCodes.OK)
          },
          path("expression" / "lab5") {
            complete(StatusCodes.OK)
          },
        ))
      }
    )

  def parseFirstLabExpression(inputExpressionModel: InputExpressionModel): Route = {
    val service = new ExpressionParsingService
    val outputModel = service.parseExpression(inputExpressionModel)

    complete(outputModel)
  }

  def parseSecondLabExpression(inputExpressionModel: InputExpressionModel): Route = {
    val service = new ExpressionParsingService
    val outputModel = service.parseOptimizedExpression(inputExpressionModel)

    complete(outputModel)
  }

  def parseThirdLabExpression(inputExpressionModel: InputExpressionModel): Route = {
    val service = new ExpressionParsingService
    val outputModel = service.parseWithOpenBracesExpression(inputExpressionModel)

    complete(outputModel)
  }

  def parseFourthLabExpression(inputExpressionModel: InputExpressionModel): Route = {
    val service = new ExpressionParsingService
    val outputModel = service.parseCommutativeExpression(inputExpressionModel)

    complete(outputModel)
  }

  def emulateFifthLabCalculation(inputExpressionModel: InputExpressionModel): Route = {
    val service = new PipelineService
    val outputModel = service.emulateStaticRebuildingPipeline(inputExpressionModel)

    complete(outputModel)
  }
}
