package parsing.models.tree

import NodeType._

class ExpressionNode(var level: Int, var nodeType: NodeType.Value, var value: NodeValue) {
  var rightNode: ExpressionNode = null
  var leftNode: ExpressionNode = null

  def addLeftNode(node: ExpressionNode): Unit = {
    leftNode = node
  }

  def addRightNode(node: ExpressionNode): Unit = {
    rightNode = node
  }
}

object ExpressionNode {
  def getEmptyNode() = new ExpressionNode(0, NodeType.None, new NodeValue())

  def getEmptyNode(level: Int) = new ExpressionNode(level, NodeType.None, new NodeValue())
}
