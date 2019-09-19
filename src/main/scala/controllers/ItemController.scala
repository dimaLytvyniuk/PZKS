package controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import parsing.ExpressionParsingService
import parsing.models.InputExpressionModel

final case class Item(name: String, id: Long)

class ItemController {
  implicit val itemFormat = jsonFormat2(Item)

  val routes: Route =
    concat(
      get {
        path("item") {
          getItem()
        }
      },
      post {
        path("item") {
          entity(as[Item]) { item =>
            onItemCreated(item)
          }
        }
      }
  )

  def getItem(): Route = {
    val service = new ExpressionParsingService
    val model = new InputExpressionModel("(5*3 + 5 * 3) - 2")
    service.parseExpression(model)
    complete(Item("item", 2))
  }

  def onItemCreated(item: Item): Route = {
    println(s"In order created ${item.id} ${item.name}")
    complete("oreder created")
  }
}
