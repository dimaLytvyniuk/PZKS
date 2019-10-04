package parsing.models.tree

class BalancedExpressionTree extends ExpressionTree {
  protected override def addOperationNode(operation: Char): Unit = {
    super.addOperationNode(operation)

    if (canBeBalanced()) {
      balanceTree()
    }
  }

  protected def balanceTree(): Unit = {
    val oldCenterTree = _currentNode.parent.parent
    val parentOldCenterTree = oldCenterTree.parent
    val newCenterTree = _currentNode.parent

    oldCenterTree.rightNode = newCenterTree.leftNode
    newCenterTree.leftNode = oldCenterTree

    if (parentOldCenterTree == null) {
      _head = newCenterTree
    } else {
      oldCenterTree.parent.rightNode = newCenterTree
    }
  }

  protected def canBeBalanced(): Boolean = {
    _currentNode.parent != null &&
      _currentNode.parent.nodeType == _currentNode.nodeType &&
      _currentNode.parent.braceNumber == _currentNode.braceNumber &&
      _currentNode.parent.parent != null &&
      _currentNode.parent.parent.nodeType == _currentNode.nodeType &&
      _currentNode.parent.parent.braceNumber == _currentNode.braceNumber
  }
}
