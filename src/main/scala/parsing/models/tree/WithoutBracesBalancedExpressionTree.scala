package parsing.models.tree

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class WithoutBracesBalancedExpressionTree extends BalancedExpressionTree {

  def openTreeBraces(): Unit = {
    val nodeStack = new mutable.Stack[ExpressionNode]
    var node = _head
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
  }

  protected def openBraces(node: ExpressionNode): Unit = {
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

  protected def openSumBraces(node: ExpressionNode): Unit = {
    if (node.braceNumber < node.leftNode.braceNumber) {
      node.leftNode.braceNumber = node.braceNumber
    }
    if (node.braceNumber < node.rightNode.braceNumber) {
      node.rightNode.braceNumber = node.braceNumber
    }
  }

  protected def openSubtractionBraces(node: ExpressionNode): Unit = {
    val nodeParent = node.parent
    val nodeLeftChild = node.leftNode

    if (node.braceNumber < node.leftNode.braceNumber) {
      node.leftNode.braceNumber = node.braceNumber
    }
    if (node.braceNumber < node.rightNode.braceNumber) {
      node.rightNode.braceNumber = node.braceNumber
    }

    val subtreeNodes = dropOnSimpleNodesSubtree(node.rightNode)
    val newSubtreeHead = buildSubtractionTree(node, subtreeNodes)
    val lastLeftNode = newSubtreeHead.lastLeftNode()

    if (nodeParent == null) {
      _head = newSubtreeHead
    } else {
      if (node.isRightChild) {
        nodeParent.rightNode = newSubtreeHead
      } else {
        nodeParent.leftNode = newSubtreeHead
      }
    }

    if (lastLeftNode != null) {
      lastLeftNode.leftNode = nodeLeftChild
    } else {
      newSubtreeHead.leftNode = nodeLeftChild
    }
  }

  protected def openDivisionBraces(node: ExpressionNode): Unit = {

  }

  protected def openMultiplicationBraces(node: ExpressionNode): Unit = {

  }

  protected def dropOnSimpleNodesSubtree(subtreeHead: ExpressionNode): ArrayBuffer[ExpressionNode] = {
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

  protected def buildSubtractionTree(startNode: ExpressionNode, subtreeNodes: ArrayBuffer[ExpressionNode]): ExpressionNode = {
    var newSubtreeHead = startNode.getWithoutReferencesCopy()
    var currentSubtreeNode = newSubtreeHead

    for(child <- subtreeNodes) {
      if (child.isSum || child.isSubtraction) {
        if (child.isSum) {
          child.nodeType = NodeType.Subtraction
        } else {
          child.nodeType = NodeType.Sum
        }

        if (currentSubtreeNode.isSum) {
          child.leftNode = currentSubtreeNode.rightNode
          currentSubtreeNode.rightNode = child
          currentSubtreeNode = child
        } else {
          var targetNode = currentSubtreeNode
          while (targetNode.parent != null && targetNode.parent.isSubtraction) {
            targetNode = targetNode.parent
          }
          val targetParent = targetNode.parent

          if (targetParent == null) {
            newSubtreeHead = child
          } else {
            if (targetNode.isRightChild) {
              targetParent.rightNode = child
            } else {
              targetParent.leftNode = child
            }
          }

          child.leftNode = targetNode
          currentSubtreeNode = child
        }
      } else {
        currentSubtreeNode.rightNode = child
        child.braceNumber = currentSubtreeNode.braceNumber
      }
    }

    newSubtreeHead
  }

  protected def buildDivisionMultiplicationTree(rightSubtreeNodes: ArrayBuffer[ExpressionNode], leftSubtreeNodes: ArrayBuffer[ExpressionNode], operation: NodeType.Value): ExpressionNode = {
    var newSubtreeHead:ExpressionNode = null
    var currentSubtreeNode:ExpressionNode = null
    var currentRightOperation = NodeType.Sum
    var currentLeftOperation = NodeType.Sum

    for (leftChild <- rightSubtreeNodes) {
      if (leftChild.isSum || leftChild.isSubtraction) {
        currentLeftOperation = leftChild.nodeType
      } else {
        for (rightChild <- leftSubtreeNodes) {
          if (rightChild.isSum || rightChild.isSubtraction) {
            currentRightOperation = rightChild.nodeType
          } else {
            val mainNode = ExpressionNode.getEmptyNode(0, rightChild.braceNumber)
            leftChild.braceNumber = rightChild.braceNumber

            if (currentLeftOperation == currentRightOperation) {
              mainNode.nodeType = NodeType.Sum
            } else {
              mainNode.nodeType = NodeType.Subtraction
            }

            mainNode.rightNode = applyOperationForNodes(leftChild, rightChild, operation)

            if (currentSubtreeNode != null) {
              if (currentSubtreeNode.isDivision || currentSubtreeNode.isMultiplication) {
                mainNode.leftNode = currentSubtreeNode
                currentSubtreeNode = mainNode
                newSubtreeHead = currentSubtreeNode
              } else if (currentSubtreeNode.isSum) {
                mainNode.leftNode = currentSubtreeNode.rightNode
                currentSubtreeNode.rightNode = mainNode
                currentSubtreeNode = mainNode
              } else {
                var targetNode = currentSubtreeNode
                while (targetNode.parent != null && targetNode.parent.isSubtraction) {
                  targetNode = targetNode.parent
                }
                val targetParent = targetNode.parent

                if (targetParent == null) {
                  newSubtreeHead = mainNode
                } else {
                  if (targetNode.isRightChild) {
                    targetParent.rightNode = mainNode
                  } else {
                    targetParent.leftNode = mainNode
                  }
                }

                mainNode.leftNode = targetNode
                currentSubtreeNode = mainNode
              }
            } else {
              currentSubtreeNode = mainNode.rightNode
              newSubtreeHead = currentSubtreeNode
            }
          }
        }
      }
    }

    newSubtreeHead
  }

  protected def applyOperationForNodes(leftNode: ExpressionNode, rightNode: ExpressionNode, operation: NodeType.Value): ExpressionNode = {
    val newNode = ExpressionNode.getEmptyNode(rightNode.braceNumber, operation)
    newNode.leftNode = leftNode
    newNode.rightNode = rightNode

    if (rightNode.isMultiplication || rightNode.isDivision) {
      rightNode.braceNumber += 1
    }

    newNode
  }

  protected def isOperationBeforeBraces(operationNode: ExpressionNode): Boolean = {
    operationNode.nodeType != NodeType.HasValue &&
      (operationNode.braceNumber < operationNode.rightNode.braceNumber ||
        operationNode.braceNumber < operationNode.leftNode.braceNumber)
  }
}

object WithoutBracesBalancedExpressionTree {
  def getCopy(tree: WithoutBracesBalancedExpressionTree): WithoutBracesBalancedExpressionTree = {
    val newTree = new WithoutBracesBalancedExpressionTree
    newTree._head = tree.head
    newTree._usedVariables = tree.usedVariables

    newTree
  }

  def getCopy(tree: ExpressionTree): WithoutBracesBalancedExpressionTree = {
    val newTree = new WithoutBracesBalancedExpressionTree
    newTree._head = tree.head
    newTree._usedVariables = tree.usedVariables

    newTree
  }
}
