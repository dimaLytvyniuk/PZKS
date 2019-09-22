package parsing.models.views

import parsing.models.tree.{ExpressionNode, NodeType}

final case class ExpressionNodeViewModel(level: Int, nodeType: String, value: String, leftNode: ExpressionNodeViewModel, rightNode: ExpressionNodeViewModel);

object ExpressionNodeViewModel {
  def createFromExpressionNode(expressionNode: ExpressionNode): ExpressionNodeViewModel = {
    if (expressionNode == null) {
      null
    } else {
      val leftNode = createFromExpressionNode(expressionNode.leftNode)
      val rightNode = createFromExpressionNode(expressionNode.rightNode)

      var nodeValue = ""
      expressionNode.nodeType match {
        case NodeType.HasValue => nodeValue = expressionNode.value.getStrValue
        case _ => nodeValue = expressionNode.nodeType.toString
      }

      new ExpressionNodeViewModel(expressionNode.level, expressionNode.nodeType.toString, nodeValue, leftNode, rightNode)
    }
  }
}
