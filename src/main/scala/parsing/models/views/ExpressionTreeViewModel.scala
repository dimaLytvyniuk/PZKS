package parsing.models.views

import parsing.models.tree.ExpressionTree

final case class ExpressionTreeViewModel(head: ExpressionNodeViewModel, supportedFunctions: Array[String]);

object ExpressionTreeViewModel {
  def createFromExpressionTree(expressionTree: ExpressionTree): ExpressionTreeViewModel = {
    val headViewModel = ExpressionNodeViewModel.createFromExpressionNode(expressionTree.head)

    new ExpressionTreeViewModel(headViewModel, expressionTree.usedVariables.toArray)
  }
}
