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
//    var node = _head
//    val stack = new mutable.Stack[ExpressionNode]
//    stack.push(node)
//
//    while(!stack.isEmpty) {
//      node = stack.pop()
//      checkOnSameCommutativity(node)
//
//      if (node.leftNode != null) {
//        stack.push(node.leftNode)
//      }
//
//      if (node.rightNode != null) {
//        stack.push(node.rightNode)
//      }
//    }

    applyCommutativity(_head)

    evaluatedResults += evaluateWithoutBracesStr()
  }

  protected def applyCommutativity(node: ExpressionNode): Unit = {
    node.nodeType match {
      case NodeType.Sum => applySumCommutativity(node)
      case NodeType.Subtraction => checkSubtractionOnSameCommutativity(node)
      case NodeType.Division => checkDivisionOnSameCommutativity(node)
      case NodeType.Multiplication => checkMultiplicationOnSameCommutativity(node)
      case _ => {}
    }
  }

  protected def applySumCommutativity(expressionNode: ExpressionNode): Unit = {
    val allSumNodes = getAllSumNodesInSameBraces(expressionNode)

    for (i <- 1 until allSumNodes.length) {
      for (j <- 0 until allSumNodes.length - i) {
        if (j == allSumNodes.length - i -1) {
          compareAndSwapSumNodesWithLast(allSumNodes(j), allSumNodes(j+1))
        }

        compareAndSwapSumNodes(allSumNodes(j), allSumNodes(j+1))
      }
    }
  }

  protected def applySubtractionCommutativity(expressionNode: ExpressionNode): Unit = {

  }

  protected def applyMultiplicationCommutativity(expressionNode: ExpressionNode): Unit = {

  }

  protected def applyDivisionCommutativity(expressionNode: ExpressionNode): Unit = {

  }

  protected def checkOnSameCommutativity(node: ExpressionNode): Unit = {
    node.nodeType match {
      case NodeType.Sum => checkSumOnSameCommutativity(node)
      case NodeType.Subtraction => checkSubtractionOnSameCommutativity(node)
      case NodeType.Division => checkDivisionOnSameCommutativity(node)
      case NodeType.Multiplication => checkMultiplicationOnSameCommutativity(node)
      case _ => {}
    }
  }

  protected def checkSumOnSameCommutativity(node: ExpressionNode): Unit = {
    if (node.leftNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode
      node.leftNode = node.rightNode
      node.rightNode = tmp
    }

    if (node.isRightNodeInSameBraces && node.rightNode.isSum) {
      if (node.leftNode.complexity(_operationsComplexity) > node.rightNode.rightNode.complexity(_operationsComplexity)) {
        val tmp = node.leftNode
        node.leftNode = node.rightNode.rightNode
        node.rightNode.leftNode = tmp
      }

      if (node.rightNode.leftNode.complexity(_operationsComplexity) > node.rightNode.rightNode.complexity(_operationsComplexity)) {
        val tmp = node.rightNode.rightNode
        node.rightNode.rightNode = node.rightNode.leftNode
        node.rightNode.leftNode = tmp
      }

      if (node.leftNode.complexity(_operationsComplexity) > node.rightNode.leftNode.complexity(_operationsComplexity)) {
        val tmp = node.rightNode.leftNode
        node.rightNode.leftNode = node.leftNode
        node.leftNode = tmp
      }
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

  protected def checkSubtractionOnSameCommutativity(node: ExpressionNode): Unit = {
    if (node.isLeftNodeInSameBraces && node.leftNode.isSubtraction &&
      node.leftNode.rightNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.rightNode
      node.leftNode.rightNode = node.rightNode
      node.rightNode = tmp
    }
  }

  def checkMultiplicationOnSameCommutativity(node: ExpressionNode): Unit = {
    if (node.leftNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode
      node.leftNode = node.rightNode
      node.rightNode = tmp
    }

    if (node.rightNode.isMultiplication) {
      val sortedChilds = mutable.Map(
        node.leftNode.complexity(_operationsComplexity) -> node.leftNode,
        node.rightNode.leftNode.complexity(_operationsComplexity) -> node.rightNode.leftNode,
        node.rightNode.rightNode.complexity(_operationsComplexity) -> node.rightNode.rightNode)
        .toSeq.sortBy(x => x._1).toArray

      node.leftNode = sortedChilds(0)._2
      node.rightNode.leftNode = sortedChilds(1)._2
      node.rightNode.rightNode = sortedChilds(2)._2
    }

    if (node.isLeftNodeInSameBraces && node.leftNode.isDivision &&
      node.leftNode.leftNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.rightNode
      node.rightNode = node.leftNode.leftNode
      node.leftNode.leftNode = tmp
    }
  }

  protected def checkDivisionOnSameCommutativity(node: ExpressionNode): Unit = {
    if (node.isLeftNodeInSameBraces && node.leftNode.isDivision &&
      node.leftNode.rightNode.complexity(_operationsComplexity) > node.rightNode.complexity(_operationsComplexity)) {
      val tmp = node.leftNode.rightNode
      node.leftNode.rightNode = node.rightNode
      node.rightNode = tmp
    }
  }

  protected def getAllSumNodesInSameBraces(startNode: ExpressionNode): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && currentNode.isSum) {
      if (!currentNode.isLeftNodeInSameBraces || currentNode.leftNode.isMultiplication || currentNode.rightNode.isDivision || currentNode.rightNode.isSubtraction) {
        applyCommutativity(currentNode.leftNode)
      }

      if (!currentNode.isRightNodeInSameBraces || currentNode.rightNode.isMultiplication || currentNode.rightNode.isDivision || currentNode.rightNode.isSubtraction) {
          applyCommutativity(currentNode.rightNode)
      }
      nodes += currentNode

      currentNode = currentNode.rightNode
    }

    nodes
  }

  protected def compareAndSwapSumNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.leftNode.complexity(_operationsComplexity) > secondNode.leftNode.complexity(_operationsComplexity)) {
      val tmp = secondNode.leftNode
      secondNode.leftNode = firstNode.leftNode
      firstNode.leftNode = tmp
    }

    if (firstNode.isLeftNodeInSameBraces && firstNode.leftNode.isSubtraction) {
      val lastFirstLeftNode = firstNode.leftNode.lastLeftNodeSameOperationSameBraces()

      if (secondNode.isLeftNodeInSameBraces && secondNode.leftNode.isSubtraction) {
        val lastSecondLeftNode = secondNode.leftNode.lastLeftNodeSameOperationSameBraces()

        if (lastFirstLeftNode.complexity(_operationsComplexity) > lastSecondLeftNode.complexity(_operationsComplexity)) {
          val secondLeftNodeParent = lastSecondLeftNode.parent
          lastFirstLeftNode.parent.leftNode = lastSecondLeftNode
          secondLeftNodeParent.leftNode = lastFirstLeftNode
        }
      } else {
        if (lastFirstLeftNode.complexity(_operationsComplexity) > secondNode.leftNode.complexity(_operationsComplexity)) {
          lastFirstLeftNode.parent.leftNode = secondNode.leftNode
          secondNode.leftNode = lastFirstLeftNode
        }
      }
    } else if (secondNode.isLeftNodeInSameBraces && secondNode.leftNode.isSubtraction) {
      val lastSecondLeftNode = secondNode.leftNode.lastLeftNodeSameOperationSameBraces()

      if (firstNode.leftNode.complexity(_operationsComplexity) > lastSecondLeftNode.complexity(_operationsComplexity)) {
        lastSecondLeftNode.parent.leftNode = firstNode.leftNode
        firstNode.leftNode = lastSecondLeftNode
      }
    }
  }

  protected def compareAndSwapSumNodesWithLast(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    compareAndSwapSumNodes(firstNode, secondNode)

    if (secondNode.leftNode.complexity(_operationsComplexity) > secondNode.rightNode.complexity(_operationsComplexity)) {
      val tmp = secondNode.leftNode
      secondNode.leftNode = secondNode.rightNode
      secondNode.rightNode = tmp
    }

    if (secondNode.isLeftNodeInSameBraces && secondNode.leftNode.isSubtraction) {
      val lastSecondLeftNode = secondNode.leftNode.lastLeftNodeSameOperationSameBraces()

      if (secondNode.isRightNodeInSameBraces && secondNode.rightNode.isSubtraction) {
        val lastSecondRightLeftNode = secondNode.rightNode.lastLeftNodeSameOperationSameBraces()

        if (lastSecondLeftNode.complexity(_operationsComplexity) > lastSecondRightLeftNode.complexity(_operationsComplexity)) {
          val secondLeftNodeParent = lastSecondLeftNode.parent
          lastSecondRightLeftNode.parent.leftNode = lastSecondLeftNode
          secondLeftNodeParent.leftNode = lastSecondRightLeftNode
        }
      } else {
        if (lastSecondLeftNode.complexity(_operationsComplexity) > secondNode.rightNode.complexity(_operationsComplexity)) {
          lastSecondLeftNode.parent.leftNode = secondNode.rightNode
          secondNode.rightNode = lastSecondLeftNode
        }
      }
    } else {
      if (secondNode.isRightNodeInSameBraces && secondNode.rightNode.isSubtraction) {
        val lastSecondRightNode = secondNode.rightNode.lastLeftNodeSameOperationSameBraces()

        if (secondNode.leftNode.complexity(_operationsComplexity) > lastSecondRightNode.complexity(_operationsComplexity)) {
          lastSecondRightNode.parent.leftNode = secondNode.leftNode
          secondNode.leftNode = lastSecondRightNode
        }
      }
    }
  }
}
