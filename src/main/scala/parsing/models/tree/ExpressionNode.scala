package parsing.models.tree

import NodeType._

class ExpressionNode(private var _level: Int, var nodeType: NodeType.Value, var value: NodeValue, val braceNumber: Int) {
  private var _rightNode: ExpressionNode = null
  private var _leftNode: ExpressionNode = null
  private var _parent: ExpressionNode = null
  private var _isRightChild = false
  private var _wasInversed = false

  def leftNode = _leftNode
  def leftNode_=(node: ExpressionNode) {
    _leftNode = node
    _leftNode.parent = this
    _leftNode.isRightChild = false
  }

  def rightNode = _rightNode
  def rightNode_=(node: ExpressionNode) {
    _rightNode = node
    _rightNode.parent = this
    _rightNode.isRightChild = true
  }

  def parent = _parent
  def parent_=(node: ExpressionNode) {
    _parent = node
  }

  def level = _level
  def level_=(newLevel: Int) {
    _level = newLevel

    if (_rightNode != null) {
      _rightNode.level = newLevel + 1
    }

    if (_leftNode != null) {
      _leftNode.level = newLevel + 1
    }
  }

  def isRightChild: Boolean = _isRightChild
  def isRightChild_=(newValue: Boolean) {
    _isRightChild = newValue
  }

  def wasInversed: Boolean = _wasInversed
  def wasInversed_=(newValue: Boolean): Unit = {
    _wasInversed = newValue
  }

  def evaluateStr(): String = {
    var result = ""
    if (nodeType == NodeType.None) {
      throw new Exception("Incorrect evaluated expression")
    }

    var nodeValue: String = ""

    if (nodeType == NodeType.HasValue) {
      nodeValue = value.getStrValue
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
      if (leftNode != null) {
        result += leftNode.evaluateStr()
      }
      result += nodeValue
      if (rightNode != null) {
        result += rightNode.evaluateStr()
      }
      result += ")"
    } else {
      result = nodeValue
    }

    result
  }

  def height: Int = {
    val leftHeight = if (_leftNode == null) 0 else _leftNode.height
    val rightHeight = if (_rightNode == null) 0 else _rightNode.height

    if (leftHeight >= rightHeight) {
      leftHeight + 1
    } else {
      rightHeight + 1
    }
  }

  def checkPrioritization(secondNode: ExpressionNode): Int = {
    if (secondNode.wasInversed &&
      secondNode.nodeType == nodeType &&
      (secondNode.nodeType == NodeType.Sum || secondNode.nodeType == NodeType.Multiplication)) {
      1
    } else if (secondNode.wasInversed && secondNode.nodeType == NodeType.Sum && nodeType == NodeType.Subtraction) {
      0
    } else if (secondNode.wasInversed && secondNode.nodeType == NodeType.Multiplication && nodeType == NodeType.Division) {
      0
    }
    else {
      NodeType.checkPrioritization(nodeType, secondNode.nodeType)
    }
  }

  def getCopy(): ExpressionNode = {
    val newNode = new ExpressionNode(level, nodeType, value.getCopy(), braceNumber)
    if (_leftNode != null) {
      newNode.leftNode = _leftNode.getCopy()
    }
    if (_rightNode != null) {
      newNode.rightNode = _rightNode.getCopy()
    }

    newNode.wasInversed = _wasInversed

    newNode
  }
}

object ExpressionNode {
  def getEmptyNode() = new ExpressionNode(0, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int, braceNumber: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), braceNumber)
}
