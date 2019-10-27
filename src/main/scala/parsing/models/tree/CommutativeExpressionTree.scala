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
      case NodeType.Subtraction => applySubtractionCommutativity(node)
      case NodeType.Division => applyDivisionCommutativity(node)
      case NodeType.Multiplication => applyMultiplicationCommutativity(node)
      case _ => {}
    }
  }

  protected def applySumCommutativity(expressionNode: ExpressionNode): Unit = {
    val allSumNodes = getAllSumNodesInSameBraces(expressionNode)


    if (allSumNodes.length == 1) {
      compareAndSwapLastSumNode(allSumNodes(0))
    } else {
      for (i <- 0 until allSumNodes.length - 1) {
        compareAndSwapSumNodes(allSumNodes(i), allSumNodes(i + 1))
      }

      compareAndSwapLastSumNode(allSumNodes(allSumNodes.length - 1))

      for (i <- 1 until allSumNodes.length) {
        for (j <- 0 until allSumNodes.length - i) {
          compareAndSwapSumNodes(allSumNodes(j), allSumNodes(j + 1))
        }
      }
    }

    applySubtractionCommutativity(expressionNode)
  }

  protected def applySubtractionCommutativity(expressionNode: ExpressionNode): Unit = {
    val allSubtractionNodes = getAllSubtractionNodesInSameBraces(expressionNode)

    for (i <- 1 until allSubtractionNodes.length) {
      for (j <- 0 until allSubtractionNodes.length - i) {
        compareAndSwapSubtractionNodes(allSubtractionNodes(j), allSubtractionNodes(j+1))
      }
    }
  }

  protected def applyMultiplicationCommutativity(expressionNode: ExpressionNode): Unit = {
    val allMultiplicationNodes = getAllMultiplicationNodes(expressionNode)

    if (allMultiplicationNodes.length == 1) {
      compareAndSwapLastMultiplicationNode(allMultiplicationNodes(0))
    } else {
      for (i <- 0 until allMultiplicationNodes.length - 1) {
        compareAndSwapMultiplicationNodes(allMultiplicationNodes(i), allMultiplicationNodes(i + 1))
      }

      compareAndSwapLastMultiplicationNode(allMultiplicationNodes(allMultiplicationNodes.length - 1))

      for (i <- 1 until allMultiplicationNodes.length) {
        for (j <- 0 until allMultiplicationNodes.length - i) {
          compareAndSwapMultiplicationNodes(allMultiplicationNodes(j), allMultiplicationNodes(j + 1))
        }
      }
    }
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
      if (!currentNode.isLeftNodeInSameBraces || currentNode.leftNode.isMultiplication || currentNode.rightNode.isDivision) {
        applyCommutativity(currentNode.leftNode)
      }

      if (!currentNode.isRightNodeInSameBraces || currentNode.rightNode.isMultiplication || currentNode.rightNode.isDivision) {
          applyCommutativity(currentNode.rightNode)
      }
      nodes += currentNode

      currentNode = currentNode.rightNode
    }

    nodes
  }

  protected def getAllSubtractionNodesInSameBraces(startNode: ExpressionNode): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && (currentNode.isSum|| currentNode.isSubtraction)) {
      val subtractionChilds = currentNode.getAllSubtractionChilds()
      nodes ++= subtractionChilds
      if (currentNode.isSum) {
        currentNode = currentNode.rightNode
      }
      else {
        nodes += currentNode
        currentNode = null
      }
    }

    nodes.foreach(x => {
      if (!x.isLeftNodeInSameBraces || x.leftNode.isMultiplication || x.leftNode.isDivision) {
        applyCommutativity(x.leftNode)
      }

      if (!x.isRightNodeInSameBraces || x.rightNode.isMultiplication || x.rightNode.isDivision) {
        applyCommutativity(x.rightNode)
      }
    })

    nodes
  }

  protected def getAllMultiplicationNodes(startNode: ExpressionNode): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && currentNode.isMultiplication) {
      if (!currentNode.isLeftNodeInSameBraces || currentNode.leftNode.isDivision) {
        applyCommutativity(currentNode.leftNode)
      }

      if (!currentNode.isRightNodeInSameBraces || currentNode.rightNode.isDivision) {
        applyCommutativity(currentNode.rightNode)
      }
      nodes += currentNode

      currentNode = currentNode.rightNode
    }

    nodes
  }

  protected def compareAndSwapSumNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    compareAndSwapSumSumNode(firstNode.leftNode, secondNode.leftNode)

    (firstNode, secondNode) match {
      case x if (x._1.isLeftNodeInSameBraces && x._1.leftNode.isSubtraction && x._2.isLeftNodeInSameBraces && x._2.leftNode.isSubtraction) => compareAndSwapSubtractionSubtractionNode(x._1.leftNode, x._2.leftNode)
      case x if (x._1.isLeftNodeInSameBraces && x._1.leftNode.isSubtraction) => compareAndSwapSubtractionSumNode(x._1.leftNode, x._2.leftNode)
      case x if (x._2.isLeftNodeInSameBraces && x._2.leftNode.isSubtraction) => compareAndSwapSumSubtractionNode(x._1.leftNode, x._2.leftNode)
      case _ => {}
    }
  }

  protected def compareAndSwapLastSumNode(secondNode: ExpressionNode): Unit = {
    compareAndSwapSumSumNode(secondNode.leftNode, secondNode.rightNode)

    secondNode match {
      case x if (x.isLeftNodeInSameBraces && x.leftNode.isSubtraction && x.isRightNodeInSameBraces && x.rightNode.isSubtraction) => compareAndSwapSubtractionSubtractionNode(x.leftNode, x.rightNode)
      case x if (x.isLeftNodeInSameBraces && x.leftNode.isSubtraction) =>  compareAndSwapSubtractionSumNode(x.leftNode, x.rightNode)
      case x if (x.isRightNodeInSameBraces && x.rightNode.isSubtraction) => compareAndSwapSumSubtractionNode(x.leftNode, x.rightNode)
      case _ => {}
    }
  }

  protected def compareAndSwapSubtractionNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.rightNode.complexity(_operationsComplexity) > secondNode.rightNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode.rightNode, secondNode.rightNode)
    }
  }

  protected def compareAndSwapMultiplicationNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.leftNode.complexity(_operationsComplexity) > secondNode.leftNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode.leftNode, secondNode.leftNode)
    }
  }

  protected def compareAndSwapLastMultiplicationNode(secondNode: ExpressionNode): Unit = {
    if (secondNode.leftNode.complexity(_operationsComplexity) > secondNode.rightNode.complexity(_operationsComplexity)) {
      swapNodes(secondNode.leftNode, secondNode.rightNode)
    }
  }

  protected def compareAndSwapSubtractionSubtractionNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val lastFirstLeftNode = firstNode.lastLeftSubtractionNodeSameBraces()
    val lastSecondLeftNode = secondNode.lastLeftSubtractionNodeSameBraces()

    if (lastFirstLeftNode.complexity(_operationsComplexity) > lastSecondLeftNode.complexity(_operationsComplexity)) {
      swapNodes(lastFirstLeftNode, lastSecondLeftNode)
    }
  }

  protected def compareAndSwapSubtractionSumNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val lastFirstLeftNode = firstNode.lastLeftSubtractionNodeSameBraces()

    if (lastFirstLeftNode.complexity(_operationsComplexity) > secondNode.complexity(_operationsComplexity)) {
      swapNodes(lastFirstLeftNode, secondNode)
    }
  }

  protected def compareAndSwapSumSubtractionNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val lastSecondLeftNode = secondNode.lastLeftSubtractionNodeSameBraces()

    if (firstNode.complexity(_operationsComplexity) > lastSecondLeftNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode, lastSecondLeftNode)
    }
  }

  protected def compareAndSwapSumSumNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.complexity(_operationsComplexity) > secondNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode, secondNode)
    }
  }

  protected def swapNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val firstParentNode = firstNode.parent
    val secondParentNode = secondNode.parent
    val isSecondRightChild = secondNode.isRightChild

    if (firstNode.isRightChild) {
      firstParentNode.rightNode = secondNode
    } else {
      firstParentNode.leftNode = secondNode
    }

    if (isSecondRightChild) {
      secondParentNode.rightNode = firstNode
    } else {
      secondParentNode.leftNode = firstNode
    }
  }
}
