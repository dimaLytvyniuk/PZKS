package parsing.models.tree

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class CommutativeExpressionTree extends ExpressionTree {
  private val _operationsComplexity = Map(
    NodeType.HasValue -> 0,
    NodeType.Sum -> 1,
    NodeType.Subtraction -> 1,
    NodeType.Multiplication -> 2,
    NodeType.Division -> 4)

  private var _variantsOfTree = new ArrayBuffer[ExpressionNode]

  def calculateCommutative(): Unit = {
    var node = _head
    val stack = new mutable.Stack[ExpressionNode]
    stack.push(node)

    while(!stack.isEmpty) {
      node = stack.pop()
      checkCommutativity(node)

      if (node.leftNode != null) {
        stack.push(node.leftNode)
      }

      if (node.rightNode != null) {
        stack.push(node.rightNode)
      }
    }

    evaluatedResults += evaluateWithoutBracesStr()
  }

  def checkCommutativity(node: ExpressionNode): Unit = {
    node.nodeType match {
      case NodeType.Sum => checkSumCommutativity(node)
      case NodeType.Subtraction => checkSubtractionCommutativity(node)
      case NodeType.Division => checkDivisionCommutativity(node)
      case NodeType.Multiplication => checkMultiplicationCommutativity(node)
      case _ => {}
    }
  }

  def checkSumCommutativity(node: ExpressionNode): Unit = {
    if (node.leftNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode
      node.leftNode = node.rightNode
      node.rightNode = tmp
    }

    if (node.rightNode.isSum) {
      val sortedChilds = mutable.Map(
        node.leftNode.complexity(_operationsComplexity) -> node.leftNode,
        node.rightNode.leftNode.complexity(_operationsComplexity) -> node.rightNode.leftNode,
        node.rightNode.rightNode.complexity(_operationsComplexity) -> node.rightNode.rightNode)
          .toSeq.sortBy(x => x._1).toArray

      node.leftNode = sortedChilds(0)._2
      node.rightNode = sortedChilds(1)._2
      node.rightNode = sortedChilds(2)._2
    }

    if (node.isChildsInSameBraces && node.leftNode.isSubtraction && node.rightNode.isSubtraction &&
        node.leftNode.leftNode.complexity(_operationsComplexity) > node.rightNode.leftNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.leftNode
      node.leftNode.leftNode = node.rightNode.leftNode
      node.rightNode.leftNode = tmp
    } else if (node.isLeftNodeInSameBraces && node.leftNode.isSubtraction &&
        node.leftNode.leftNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.leftNode
      node.leftNode.leftNode = node.rightNode
      node.rightNode = tmp
    } else if (node.isRightNodeInSameBraces && node.rightNode.isSubtraction &&
        node.leftNode.complexity(_operationsComplexity) > node.rightNode.leftNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode
      node.leftNode = node.rightNode.leftNode
      node.rightNode.leftNode = tmp
    }
  }

  def checkSubtractionCommutativity(node: ExpressionNode): Unit = {
    if (node.isLeftNodeInSameBraces && node.leftNode.isSubtraction &&
      node.leftNode.rightNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.rightNode
      node.leftNode.rightNode = node.rightNode
      node.rightNode = tmp
    }
  }

  def checkMultiplicationCommutativity(node: ExpressionNode): Unit = {

  }

  def checkDivisionCommutativity(node: ExpressionNode): Unit = {
    if (node.isLeftNodeInSameBraces && node.leftNode.isDivision &&
      node.leftNode.rightNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.rightNode
      node.leftNode.rightNode = node.rightNode
      node.rightNode = tmp
    }
  }
}
