package parsing.models.tree

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class WithoutBracesBalancedExpressionTree extends BalancedExpressionTree {

  def getTreeWithOpenBraces(): ExpressionTree = {
    val copiedTree = getCopy()

    val nodeStack = new mutable.Stack[ExpressionNode]
    var node = copiedTree.head
    var lastVisitedNode: ExpressionNode = null

    while (!nodeStack.isEmpty || node != null) {
      if (node != null) {
        nodeStack.push(node)
        node = node.leftNode
      } else {
        val peekNode = nodeStack.top
        if (peekNode.rightNode != null && lastVisitedNode != peekNode.rightNode) {
          node = peekNode.rightNode
        } else {
          if (isOperationBeforeBraces(peekNode)) {
            openBraces(peekNode)
          }
          lastVisitedNode = nodeStack.pop()
        }
      }
    }

    copiedTree
  }

  def openBraces(node: ExpressionNode): Unit = {
    val nodeType = node.nodeType
    if (nodeType == NodeType.Subtraction) {
      openSubtractionBraces(node)
    } else if (nodeType == NodeType.Sum) {
      openSumBraces(node)
    } else if (nodeType == NodeType.Division) {
      openDivisionBraces(node)
    } else if (nodeType == NodeType.Multiplication) {
      openMultiplicationBraces(node)
    } else {
      throw new IllegalArgumentException
    }
  }

  def openSumBraces(node: ExpressionNode): Unit = {
    if (node.braceNumber < node.leftNode.braceNumber) {
      node.leftNode.braceNumber = node.braceNumber
    }
    if (node.braceNumber < node.rightNode.braceNumber) {
      node.rightNode.braceNumber = node.braceNumber
    }
  }

  def openSubtractionBraces(node: ExpressionNode): Unit = {
    val nodeParent = node.parent
    val nodeLeftChild = node.leftNode

    if (node.braceNumber < node.leftNode.braceNumber) {
      node.leftNode.braceNumber = node.braceNumber
    }
    if (node.braceNumber < node.rightNode.braceNumber) {
      node.rightNode.braceNumber = node.braceNumber
    }

    val subtreeNodes = dropOnSimpleNodesSubtree(node.rightNode)
    for (child <- subtreeNodes) {
      println(child.evaluateStr())
    }
  }

  def openDivisionBraces(node: ExpressionNode): Unit = {

  }

  def openMultiplicationBraces(node: ExpressionNode): Unit = {

  }

  def dropOnSimpleNodesSubtree(subtreeHead: ExpressionNode): ArrayBuffer[ExpressionNode] = {
    if (subtreeHead.nodeType == NodeType.HasValue) {
      ArrayBuffer(subtreeHead)
    } else {
      val resultBuffer = new ArrayBuffer[ExpressionNode]()
      val nodeStack = new mutable.Stack[ExpressionNode]
      var node = subtreeHead

      while (!nodeStack.isEmpty || node != null) {
        if (node != null) {
          nodeStack.push(node)
          if (node.leftNode == null || node.isMultiplication || node.isDivision) {
            node = null
          } else {
            node = node.leftNode
          }
        } else {
          node = nodeStack.pop()

          if (node.isDivision || node.isMultiplication) {
            resultBuffer += node.getCopy()
          } else {
            resultBuffer += node.getWithoutReferencesCopy()
          }

          if (node.rightNode == null || node.isMultiplication || node.isDivision) {
            node = null
          } else {
            node = node.rightNode
          }
        }
      }

      resultBuffer
    }
  }

  def isOperationBeforeBraces(operationNode: ExpressionNode): Boolean = {
    operationNode.nodeType != NodeType.HasValue &&
      (operationNode.braceNumber < operationNode.rightNode.braceNumber ||
        operationNode.braceNumber < operationNode.leftNode.braceNumber)
  }
}
