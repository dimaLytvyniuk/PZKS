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

  def parseOptimizedExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    var exceptionModel: ExceptionModel = null
    var treeViewModel: ExpressionTreeViewModel = null
    var evaluatedResult: String = null

    try {
      val tree = buildBalancedExpressionTree(expressionModel.expression)
      evaluatedResult = tree.evaluateStr

      treeViewModel = ExpressionTreeViewModel.createFromExpressionTree(tree)
    } catch {
      case e: Exception => exceptionModel = new ExceptionModel(e.getMessage)
    }

    new OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
  }

  def parseWithOpenBracesExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    var exceptionModel: ExceptionModel = null
    var treeViewModel: ExpressionTreeViewModel = null
    var evaluatedResult: String = null

    try {
      val tree = buildWithoutBracesBalancedExpressionTree(expressionModel.expression)
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
          case e: Exception => {
            println(e)
            throw new Exception(s"Exception at ${i + 1}: ${e.getMessage}.")
          }
        }
      }
    }
    tree.endBuildingExpression()

    tree
  }

  private def buildBalancedExpressionTree(expression: String): ExpressionTree = {
    val tree = new BalancedExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case e: Exception => {
            println(e)
            throw new Exception(s"Exception at ${i + 1}: ${e.getMessage}.")
          }
        }
      }
    }
    tree.endBuildingExpression()

    tree
  }

  private def buildWithoutBracesBalancedExpressionTree(expression: String): ExpressionTree = {
    val tree = new WithoutBracesBalancedExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case e: Exception => {
            println(e)
            throw new Exception(s"Exception at ${i + 1}: ${e.getMessage}.")
          }
        }
      }
    }
    tree.endBuildingExpression()

    tree.getTreeWithOpenBraces()
  }
}
