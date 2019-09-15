package controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

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
    complete(Item("item", 2))
  }

  def onItemCreated(item: Item): Route = {
    println(s"In order created ${item.id} ${item.name}")
    complete("oreder created")
  }
}
