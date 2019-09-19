package parsing.models.tree

import NodeType._

class ExpressionNode(private var _level: Int, var nodeType: NodeType.Value, var value: NodeValue, val braceNumber: Int) {
  private var _rightNode: ExpressionNode = null
  private var _leftNode: ExpressionNode = null
  private var _parent: ExpressionNode = null

  def leftNode = _leftNode
  def leftNode_=(node: ExpressionNode) {
    _leftNode = node
    _leftNode.parent = this
  }

  def rightNode = _rightNode
  def rightNode_=(node: ExpressionNode) {
    _leftNode = node
    _leftNode.parent = this
  }

  def parent = _parent
  def parent_=(node: ExpressionNode) {
    _parent = node
  }

  def addRightNode(node: ExpressionNode): Unit = {
    _rightNode = node
  }

  def level = _level
  def level_=(newLevel: Int) {
    _level = newLevel

    if (_rightNode != null) {
      _rightNode.level = newLevel - 1
    }

    if (_leftNode != null) {
      _leftNode.level = newLevel - 1
    }
  }
}

object ExpressionNode {
  def getEmptyNode() = new ExpressionNode(0, NodeType.None, new NodeValue())

  def getEmptyNode(level: Int) = new ExpressionNode(level, NodeType.None, new NodeValue())
}
