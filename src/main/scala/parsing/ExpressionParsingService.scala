package parsing

import parsing.models.exceptions.BaseParsingException
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
      case parsing: BaseParsingException => { exceptionModel = ExceptionModel(parsing.getMessage); println(parsing); }
      case e: Throwable => throw e
    }

    OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
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
      case parsing: BaseParsingException => { exceptionModel = ExceptionModel(parsing.getMessage); println(parsing); }
      case e: Throwable => throw e
    }

    OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
  }

  def parseWithOpenBracesExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    var exceptionModel: ExceptionModel = null
    var treeViewModel: ExpressionTreeViewModel = null
    var evaluatedResult: String = null

    try {
      val tree = buildWithoutBracesBalancedExpressionTree(expressionModel.expression)
      evaluatedResult = tree.evaluateWithoutBracesStr()

      treeViewModel = ExpressionTreeViewModel.createFromExpressionTree(tree)
    } catch {
      case parsing: BaseParsingException => { exceptionModel = ExceptionModel(parsing.getMessage); println(parsing); }
      case e: Throwable => throw e
    }

    OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
  }

  def parseCommutativeExpression(expressionModel: InputExpressionModel): OutputParsedExpressionModel = {
    var exceptionModel: ExceptionModel = null
    var treeViewModel: ExpressionTreeViewModel = null
    var evaluatedResult: String = null

    try {
      val tree = buildCommutativeExpressionTree(expressionModel.expression)
      evaluatedResult = tree.evaluateWithoutBracesStr()

      treeViewModel = ExpressionTreeViewModel.createFromExpressionTree(tree)
    } catch {
      case parsing: BaseParsingException => { exceptionModel = ExceptionModel(parsing.getMessage); println(parsing); }
      case e: Throwable => throw e
    }

    OutputParsedExpressionModel(treeViewModel, exceptionModel, evaluatedResult, expressionModel.expression)
  }

  def buildExpressionTree(expression: String): ExpressionTree = {
    val tree = new ExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case parsing: BaseParsingException => throw new BaseParsingException(s"Exception at ${i + 1}: ${parsing.getMessage}.")
          case e: Throwable => throw e
        }
      }
    }
    tree.endBuildingExpression()

    tree
  }

  def buildBalancedExpressionTree(expression: String): ExpressionTree = {
    val tree = new BalancedExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case parsing: BaseParsingException => throw new BaseParsingException(s"Exception at ${i + 1}: ${parsing.getMessage}.")
          case e: Throwable => throw e
        }
      }
    }
    tree.endBuildingExpression()

    tree
  }

  def buildWithoutBracesBalancedExpressionTree(expression: String): ExpressionTree = {
    val tree = new WithoutBracesBalancedExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case parsing: BaseParsingException => throw new BaseParsingException(s"Exception at ${i + 1}: ${parsing.getMessage}.")
          case e: Throwable => throw e
        }
      }
    }
    tree.endBuildingExpression()

    tree.openTreeBraces()

    tree
  }

  def buildCommutativeExpressionTree(expression: String): ExpressionTree = {
    val tree = new CommutativeExpressionTree

    for (i <- 0 until expression.length) {
      if (expression(i) != ' ') {
        try {
          tree.addChar(expression(i))
        } catch {
          case parsing: BaseParsingException => throw new BaseParsingException(s"Exception at ${i + 1}: ${parsing.getMessage}.")
          case e: Throwable => throw e
        }
      }
    }
    tree.endBuildingExpression()
    tree.calculateCommutative()

    tree
  }
}
