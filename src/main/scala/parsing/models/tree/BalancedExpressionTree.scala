package parsing.models.tree

class BalancedExpressionTree extends ExpressionTree {
  protected override def addOperationNode(operation: Char): Unit = {
    var previousNodeLevel = 0
    if (_currentNode != null) {
      previousNodeLevel = _currentNode.level
    }
    super.addOperationNode(operation)

    if (canBeBalanced(_currentNode)) {
      balanceTree(_currentNode)
    }
  }

  protected def balanceTree(startNode: ExpressionNode): Unit = {
    val oldCenterTree = startNode.parent.parent
    val parentOldCenterTree = oldCenterTree.parent
    val newCenterTree = startNode.parent

    oldCenterTree.rightNode = newCenterTree.leftNode
    newCenterTree.leftNode = oldCenterTree

    if (parentOldCenterTree == null) {
      newCenterTree.parent = null
      _head = newCenterTree
    } else {
      parentOldCenterTree.rightNode = newCenterTree
    }

    newCenterTree.level = newCenterTree.level - 1
    if (canBeMoreBalanced(newCenterTree)) {
      moreBalanceTree(newCenterTree)
    }
  }

  protected def moreBalanceTree(startNode: ExpressionNode): Unit = {
    val oldCenterTree = startNode.parent
    val parentOldCenterTree = oldCenterTree.parent
    val newCenterTree = startNode

    oldCenterTree.rightNode = newCenterTree.leftNode
    newCenterTree.leftNode = oldCenterTree

    if (parentOldCenterTree == null) {
      newCenterTree.parent = null
      _head = newCenterTree
    } else {
      parentOldCenterTree.rightNode = newCenterTree
    }

    newCenterTree.level = newCenterTree.level - 1
    if (canBeMoreBalanced(newCenterTree)) {
      moreBalanceTree(newCenterTree)
    }
  }

  protected def canBeBalanced(startNode: ExpressionNode): Boolean = {
    (startNode.parent != null && startNode.parent.parent != null) &&
      ((startNode.parent.nodeType != startNode.nodeType &&
        startNode.parent.nodeType == startNode.parent.parent.nodeType &&
        startNode.parent.braceNumber == startNode.parent.parent.braceNumber &&
        startNode.parent.parent.rightNode.height - startNode.parent.parent.leftNode.height > 0) ||
      (startNode.parent.nodeType == startNode.nodeType &&
        startNode.parent.braceNumber == startNode.braceNumber &&
        startNode.parent.parent.nodeType == startNode.nodeType &&
        startNode.parent.parent.braceNumber == startNode.braceNumber &&
        startNode.parent.parent.rightNode.height - startNode.parent.parent.leftNode.height > 1))
  }

  protected def canBeMoreBalanced(startNode: ExpressionNode): Boolean = {
    startNode.parent != null &&
      startNode.parent.nodeType == startNode.nodeType &&
      startNode.parent.braceNumber == startNode.braceNumber &&
      startNode.parent.rightNode.height - startNode.parent.leftNode.height > 0
  }
}
