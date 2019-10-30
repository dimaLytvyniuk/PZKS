package parsing.models.tree

import NodeType._

import scala.collection.mutable.ArrayBuffer

class ExpressionNode(private var _level: Int, var nodeType: NodeType.Value, var value: NodeValue, private var _braceNumber: Int) {
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

  def braceNumber: Int = _braceNumber

  def braceNumber_=(newValue: Int): Unit = {
    if (leftNode != null) {
      if (leftNode.braceNumber > _braceNumber) {
        leftNode.braceNumber = newValue + 1
      } else {
        leftNode.braceNumber = newValue
      }
    }

    if (rightNode != null) {
      if (rightNode.braceNumber != 1) {
        val i = 0
      }

      if (rightNode.braceNumber > _braceNumber) {
        rightNode.braceNumber = newValue + 1
      } else {
        rightNode.braceNumber = newValue
      }
    }

    _braceNumber = newValue
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

  def evaluateWithoutBracesStr(): String = {
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
      var leftNodeResult = ""
      if (leftNode != null) {
        if (leftNode.braceNumber > braceNumber) {
          leftNodeResult = s"(${leftNode.evaluateWithoutBracesStr()})"
        } else {
          leftNodeResult = leftNode.evaluateWithoutBracesStr()
        }
      }

      var rightNodeResult = ""
      if (rightNode != null) {
        if (rightNode.braceNumber > braceNumber) {
          rightNodeResult = s"(${rightNode.evaluateWithoutBracesStr()})"
        } else {
          rightNodeResult = rightNode.evaluateWithoutBracesStr()
        }
      }

      result += leftNodeResult
      result += nodeValue
      result += rightNodeResult
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
    val newNode = new ExpressionNode(level, nodeType, value.getCopy(), _braceNumber)
    if (_leftNode != null) {
      newNode.leftNode = _leftNode.getCopy()
    }
    if (_rightNode != null) {
      newNode.rightNode = _rightNode.getCopy()
    }

    newNode.wasInversed = _wasInversed
    newNode.isRightChild = isRightChild

    newNode
  }

  def getWithoutReferencesCopy(): ExpressionNode = {
    val newNode = new ExpressionNode(level, nodeType, value.getCopy(), _braceNumber)

    newNode.wasInversed = _wasInversed
    newNode.isRightChild = isRightChild

    newNode
  }

  def optimizeBraceNumbers(): Unit = {
    if (rightNode != null && leftNode != null) {
      if (!wasInversed && rightNode.wasInversed && rightNode.braceNumber == braceNumber) {
        if (nodeType == NodeType.Subtraction && rightNode.nodeType == NodeType.Sum) {
          rightNode.braceNumber = braceNumber + 1;
        }
      }

      leftNode.optimizeBraceNumbers()
      rightNode.optimizeBraceNumbers()
    }
  }

  def lastLeftNode(): ExpressionNode = {
    if (leftNode == null) {
      null
    } else {
      var lastNode = leftNode
      while (lastNode.leftNode != null) {
        lastNode = lastNode.leftNode
      }

      lastNode
    }
  }

  def lastLeftSubtractionNodeSameBraces(): ExpressionNode = {
    if (leftNode == null || leftNode.isHasValue || !isLeftNodeInSameBraces) {
      leftNode
    } else {
      val startBraceNumber = braceNumber
      var lastNode = leftNode
      while (lastNode.leftNode != null &&
          lastNode.leftNode.braceNumber == startBraceNumber &&
          lastNode.leftNode.isSubtraction) {
        lastNode = lastNode.leftNode
      }

      lastNode.leftNode
    }
  }

  def complexity(operationComplexities: Map[NodeType.Value, Int]): Int = {
    var complexity = operationComplexities(nodeType)
    if (leftNode != null) {
      complexity += rightNode.complexity(operationComplexities)
    }

    if (rightNode != null) {
      complexity += leftNode.complexity(operationComplexities)
    }

    complexity
  }

  def getAllSubtractionChilds(): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    if (leftNode != null && isLeftNodeInSameBraces && leftNode.isSubtraction) {
      var lastNode = leftNode
      nodes += lastNode
      while (lastNode.leftNode != null &&
        lastNode.leftNode.braceNumber == lastNode.braceNumber &&
        lastNode.leftNode.nodeType == NodeType.Subtraction) {
        lastNode = lastNode.leftNode
        nodes += lastNode
      }
    }

    nodes.reverse
  }

  def isSum = nodeType == NodeType.Sum
  def isSubtraction = nodeType == NodeType.Subtraction
  def isMultiplication = nodeType == NodeType.Multiplication
  def isDivision = nodeType == NodeType.Division
  def isHasValue = nodeType == NodeType.HasValue
  def isOperation = isSum || isSubtraction || isMultiplication || isDivision

  def isLeftNodeInSameBraces = leftNode != null && leftNode.braceNumber == braceNumber
  def isRightNodeInSameBraces = rightNode != null && rightNode.braceNumber == braceNumber
  def isChildsInSameBraces = isLeftNodeInSameBraces && isRightNodeInSameBraces
  def isParentInSameBraces = parent != null && parent.braceNumber == braceNumber
}

object ExpressionNode {
  def getEmptyNode() = new ExpressionNode(0, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), 0)

  def getEmptyNode(level: Int, braceNumber: Int) = new ExpressionNode(level, NodeType.None, new NodeValue(), braceNumber)

  def getEmptyNode(braceNumber: Int, nodeType: NodeType.Value): ExpressionNode = {
    val node = getEmptyNode(0, braceNumber)
    node.nodeType = nodeType

    node
  }
}
