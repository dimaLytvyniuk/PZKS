package parsing

import parsing.models.tree._
import parsing.models.views._

class ExpressionParsingService {
  def parseExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    var exceptionModel: ExceptionModel = null
    var treeViewModel: ExpressionTreeViewModel = null
    var evaluatedResult: String = null

    try {
      val tree = buildExpressionTree(expressionModel.expression)
      evaluatedResult = tree.evaluateStr

      treeViewModel = ExpressionTreeViewModel.createFromExpressionTree(tree)
    } catch {
      case e: Exception => exceptionModel = new ExceptionModel(e.getMessage)
    }

    new OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
  }

  private def buildExpressionTree(expression: String): ExpressionTree = {
    val tree = new ExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case e: Exception => throw new Exception(s"Exception at ${i + 1}: ${e.getMessage}.")
        }
      }
    }
    tree.endBuildingExpression()

    tree
  }
}
