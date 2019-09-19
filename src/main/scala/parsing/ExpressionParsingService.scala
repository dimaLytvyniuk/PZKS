package parsing

import parsing.models.tree.{ExpressionTree, NodeType}
import parsing.models.{InputExpressionModel, OutputParsedExpressionModel}
import parsing.models.tree.NodeType._

import scala.collection.mutable

class ExpressionParsingService {
  def parseExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    val tree = buildExpressionTree(expressionModel.expression)
    println(tree.evaluateStr)
    null
  }

  private def buildExpressionTree(expression: String): ExpressionTree = {
    val tree = new ExpressionTree

    for (ch <- expression) {
      if (ch != ' ') {
        tree.addChar(ch)
      }
    }
    tree.endBuildingExpression()

    tree
  }
}
