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
    _rightNode = node
    _rightNode.parent = this
  }

  def parent = _parent
  def parent_=(node: ExpressionNode) {
    _parent = node
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

  def evaluateStr(): String = {
    var result = ""
    if (nodeType == NodeType.None) {
      throw new Exception("Incorrect evaluated expression")
    }

    var nodeValue = ""

    if (nodeType == NodeType.HasValue) {
      if (value.constName != null && value.constName != "") {
        nodeValue = value.constName
      }
      else {
        nodeValue = value.intValue.toString()
      }
    } else {
      nodeType match {
        case NodeType.Sum => nodeValue = "+"
        case NodeType.Subtraction => nodeValue = "-"
        case NodeType.Multiplication => nodeValue = "*"
        case NodeType.Division => nodeValue = "/"
        case _ => throw new Exception("Incorrect node type of node")
      }
    }

    if (nodeType != NodeType.HasValue) {
      result += "("
      result += leftNode.evaluateStr()
      result += nodeValue
      result += rightNode.evaluateStr()
      result += ")"
    } else {
      result = nodeValue
    }

    result
  }
}

object ExpressionNode {
  def getEmptyNode() = new ExpressionNode(0, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int, braceNumber: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), braceNumber)
}
