package parsing.models.tree

trait NewBalancedTree extends ExpressionTree {
  def newBalanceTree(): Unit = {
    balanceNode(head)
  }

  protected def balanceNode(node: ExpressionNode): Unit = {
    node.nodeType match {
      case NodeType.Sum => balanceSumMultiplication(node)
      case NodeType.Multiplication => balanceSumMultiplication(node)
      case NodeType.Division => balanceSubtractionDivisionNode(node)
      case NodeType.Subtraction => balanceSubtractionDivisionNode(node)
      case _ => {}
    }
  }

  protected def balanceSumMultiplication(node: ExpressionNode): Unit = {
    if (node.rightNode.nodeType == node.nodeType && node.rightNode.braceNumber == node.braceNumber && (node.rightNode.height - node.leftNode.height) > 1) {
      val newCenterNode = node.rightNode
      val nodeParent = node.parent
      val isNodeRightChild = node.isRightChild

      node.rightNode = newCenterNode.leftNode
      newCenterNode.leftNode = node

      if (nodeParent == null) {
        newCenterNode.parent = null
        _head = newCenterNode
      } else {
        if (isNodeRightChild) {
          nodeParent.rightNode = newCenterNode
        } else {
          nodeParent.leftNode = newCenterNode
        }
      }

      if (newCenterNode.nodeType == newCenterNode.rightNode.nodeType && node.rightNode.braceNumber == node.braceNumber) {
        balanceNode(newCenterNode.leftNode)
        balanceNode(newCenterNode)
      } else {
        balanceNode(newCenterNode.leftNode)
        balanceNode(newCenterNode.rightNode)
      }
    } else {
      if (!node.leftNode.isHasValue) {
        balanceNode(node.leftNode)
      }
      if (!node.rightNode.isHasValue) {
          balanceNode(node.rightNode)
      }
    }
  }

  protected def balanceSubtractionDivisionNode(node: ExpressionNode): Unit = {
    if (!node.leftNode.isHasValue) {
      balanceNode(node.leftNode)
    }
    if (!node.rightNode.isHasValue) {
      balanceNode(node.rightNode)
    }
  }
}
