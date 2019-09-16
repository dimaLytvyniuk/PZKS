package parsing

import parsing.models.tree.{ExpressionTree, NodeType}
import parsing.models.{InputExpressionModel, OutputParsedExpressionModel}
import parsing.models.tree.NodeType._

import scala.collection.mutable

class ExpressionParsingService {
  def parseExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {

  }

  private[this] def buildExpressionTree(expression: String): ExpressionTree = {
    val stack = new mutable.Stack()
    val head = new ExpressionTree(0, NodeType.None, null)

    for (ch <- expression) {
      ch match {
        case '(' => println("")
      }
    }
  }
}
