package parsing.models.views

import parsing.models.tree.ExpressionTree

import scala.collection.mutable.ArrayBuffer

final case class ExpressionTreeViewModel(head: ExpressionNodeViewModel, supportedFunctions: Array[String], evaluatedResults: Array[String]);

object ExpressionTreeViewModel {
  def createFromExpressionTree(expressionTree: ExpressionTree): ExpressionTreeViewModel = {
    val headViewModel = ExpressionNodeViewModel.createFromExpressionNode(expressionTree.head)

    new ExpressionTreeViewModel(headViewModel, expressionTree.usedVariables.toArray, expressionTree.evaluatedResults.toArray)
  }
}
