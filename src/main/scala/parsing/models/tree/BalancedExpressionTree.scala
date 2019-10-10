package parsing.models.tree

class BalancedExpressionTree extends ExpressionTree {
  protected override def addOperationNode(operation: Char): Unit = {
    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    operation match {
      case '+' => newNode.nodeType = NodeType.Sum
      case '-' => newNode.nodeType = NodeType.Subtraction
      case '*' => newNode.nodeType = NodeType.Multiplication
      case '/' => newNode.nodeType = NodeType.Division
      case _ => throw new IllegalArgumentException("ch")
    }

    if (_head == null) {
      newNode.level = 0
      _head = newNode
      _currentNode = _head
    } else {
      var lastNode = _currentNode
      while (
        lastNode.parent != null &&
          newNode.braceNumber < lastNode.parent.braceNumber) {
        lastNode = lastNode.parent
      }

      while (
        lastNode.parent != null &&
         newNode.checkPrioritization(lastNode.parent) == 1 &&
          newNode.braceNumber == lastNode.parent.braceNumber) {
        lastNode = lastNode.parent
      }

      if (lastNode.parent == null) {
        newNode.level = 0
        newNode.leftNode = lastNode
        _currentNode = newNode
        _head = _currentNode
      } else {
        val targetNode = lastNode.parent
        newNode.level = targetNode.level + 1
        if (targetNode.rightNode != null) {
          newNode.leftNode = targetNode.rightNode
        }

        targetNode.rightNode = newNode
        _currentNode = newNode
      }
    }

    if (_currentNode.parent != null && _currentNode.parent.wasInversed && _currentNode.braceNumber == _currentNode.parent.braceNumber) {
      if (_currentNode.parent.nodeType == NodeType.Sum && _currentNode.nodeType == NodeType.Subtraction) {
        _currentNode.nodeType = NodeType.Sum
        _currentNode.wasInversed = true
      } else if (_currentNode.parent.nodeType == NodeType.Multiplication && _currentNode.nodeType == NodeType.Division) {
        _currentNode.nodeType = NodeType.Multiplication
        _currentNode.wasInversed = true
      }
    }

    if (_currentNode.parent != null && _currentNode.parent.nextShouldBeInversed && _currentNode.braceNumber == _currentNode.parent.braceNumber) {
      if (_currentNode.parent.nodeType == NodeType.Subtraction && _currentNode.nodeType == NodeType.Subtraction) {
        _currentNode.nodeType = NodeType.Sum
        _currentNode.wasInversed = true
      } else if (_currentNode.parent == NodeType.Division && _currentNode.nodeType == NodeType.Division) {
        _currentNode.nodeType = NodeType.Multiplication
        _currentNode.wasInversed = true
      }
    }

    if (canBeBalanced(_currentNode)) {
      balanceTree(_currentNode)
    }
  }

  protected def balanceTree(startNode: ExpressionNode): Unit = {
    if (startNode.parent.nodeType == NodeType.Subtraction || startNode.parent.nodeType == NodeType.Division) {
      withInversingBalance(startNode)
    } else {
      standardBalance(startNode)
    }
  }

  protected def standardBalance(startNode: ExpressionNode): Unit = {
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

  protected def withInversingBalance(startNode: ExpressionNode): Unit = {
    val currentParent = startNode.parent

    if (currentParent.nodeType == NodeType.Subtraction) {
      currentParent.nodeType = NodeType.Sum
      currentParent.wasInversed = true

      if (_currentNode.nodeType == NodeType.Subtraction) {
        _currentNode.nodeType = NodeType.Sum
        _currentNode.wasInversed = true
      } else {
        currentParent.nextShouldBeInversed = true
      }
    } else if (currentParent.nodeType == NodeType.Division) {
      currentParent.nodeType = NodeType.Multiplication
      currentParent.wasInversed = true

      if (_currentNode.nodeType == NodeType.Division) {
        _currentNode.nodeType = NodeType.Multiplication
        _currentNode.wasInversed = true
      } else {
        currentParent.nextShouldBeInversed = true
      }
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
