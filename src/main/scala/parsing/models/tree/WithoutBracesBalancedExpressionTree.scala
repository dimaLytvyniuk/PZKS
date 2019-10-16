package parsing.models.tree

import scala.collection.mutable

class WithoutBracesBalancedExpressionTree extends ExpressionTree {
  val nodeStack = new mutable.Stack[ExpressionNode];

  def getTreeWithOpenBraces(): ExpressionTree = {
    val copiedTree = getCopy()
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

  }

  def isOperationBeforeBraces(operationNode: ExpressionNode): Boolean = {
    operationNode.nodeType != NodeType.HasValue &&
      operationNode.braceNumber < operationNode.rightNode.braceNumber
  }
}
