package parsing.models.tree

class BalancedExpressionTree extends ExpressionTree {
  protected override def addOperationNode(operation: Char): Unit = {
    super.addOperationNode(operation)

    if (canBeBalanced()) {
      balanceTree()
    }
  }

  protected def balanceTree(): Unit = {

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
