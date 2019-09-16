package parsing.models

import scala.collection.mutable.ArrayBuffer

class OutputParsedExpressionModel {
  val exceptions = new ArrayBuffer[ExceptionModel]()

  def addException(exception: ExceptionModel): Unit = {
    exceptions += exception
  }
}
