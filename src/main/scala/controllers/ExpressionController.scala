package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.{Directives, Route}
import parsing.ExpressionParsingService
import parsing.models.views._
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.model.StatusCodes

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
      "supportedFunctions" -> tree.supportedFunctions.toJson
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
}

class ExpressionController extends Directives with JsonSupport {
  private val cors = new CorsHandler {}

  val routes: Route =
    concat(
      post {
        cors.corsHandler(
        path("expression" / "lab1") {
          entity(as[InputExpressionModel]) { inputExpressionModel =>
            parseFirstLabExpression(inputExpressionModel)
          }
        })
      },
      options {
        cors.corsHandler(
          path("expression" / "lab1") {
            complete(StatusCodes.OK)
          })
      }
    )

  def parseFirstLabExpression(inputExpressionModel: InputExpressionModel): Route = {
    val service = new ExpressionParsingService
    val outputModel = service.parseExpression(inputExpressionModel)

    complete(outputModel)
  }
}
