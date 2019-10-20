package parsing.models.tree

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class WithoutBracesBalancedExpressionTree extends BalancedExpressionTree {
  def openTreeBraces(): Unit = {
    val nodeStack = new mutable.Stack[ExpressionNode]
    var node = _head
    var lastVisitedNode: ExpressionNode = null

    evaluatedResults = new ArrayBuffer[String]()

    while (!nodeStack.isEmpty || node != null) {
      if (node != null) {
        nodeStack.push(node)
        node = node.leftNode
      } else {
        var peekNode = nodeStack.top
        if (peekNode.rightNode != null && lastVisitedNode != peekNode.rightNode) {
          node = peekNode.rightNode
        } else {
          if (isOperationBeforeBraces(peekNode)) {
            peekNode = openBraces(peekNode)
            evaluatedResults += evaluateWithoutBracesStr()

            nodeStack.pop()
            lastVisitedNode = peekNode
          } else {
            lastVisitedNode = nodeStack.pop()
          }
        }
      }
    }
  }

  protected def openBraces(node: ExpressionNode): ExpressionNode = {
    val nodeType = node.nodeType
    if (nodeType == NodeType.Subtraction) {
      openSubtractionBraces(node)
    } else if (nodeType == NodeType.Sum) {
      openSumBraces(node)
    } else if (nodeType == NodeType.Division || nodeType == NodeType.Multiplication) {
      openMultiplicationDivisionBraces(node, nodeType)
    } else {
      throw new IllegalArgumentException
    }
  }

  protected def openSumBraces(node: ExpressionNode): ExpressionNode = {
    if (node.braceNumber < node.leftNode.braceNumber) {
      node.leftNode.braceNumber = node.braceNumber
    }
    if (node.braceNumber < node.rightNode.braceNumber) {
      node.rightNode.braceNumber = node.braceNumber
    }

    node
  }

  protected def openSubtractionBraces(node: ExpressionNode): ExpressionNode = {
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

    newSubtreeHead
  }

  protected def openMultiplicationDivisionBraces(node: ExpressionNode, operation: NodeType.Value): ExpressionNode = {
    val nodeParent = node.parent

    val leftSubtreeNodes = dropOnSimpleNodesSubtree(node.leftNode)
    val rightSubtreeNodes = dropOnSimpleNodesSubtree(node.rightNode)
    val newSubtreeHead = buildDivisionMultiplicationTree(leftSubtreeNodes, rightSubtreeNodes, operation, node.braceNumber + 1)

    if (nodeParent == null) {
      _head = newSubtreeHead
      newSubtreeHead.braceNumber = newSubtreeHead.braceNumber - 1
      val i = 0
    } else {
      if (node.isRightChild) {
        nodeParent.rightNode = newSubtreeHead
      } else {
        nodeParent.leftNode = newSubtreeHead
      }
    }

    newSubtreeHead
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

  protected def buildDivisionMultiplicationTree(leftSubtreeNodes: ArrayBuffer[ExpressionNode], rightSubtreeNodes: ArrayBuffer[ExpressionNode], operation: NodeType.Value, braceNumber: Int): ExpressionNode = {
    var newSubtreeHead:ExpressionNode = null
    var currentSubtreeNode:ExpressionNode = null
    var currentRightOperation = NodeType.Sum
    var currentLeftOperation = NodeType.Sum

    for (leftChild <- leftSubtreeNodes) {
      if (leftChild.isSum || leftChild.isSubtraction) {
        currentLeftOperation = leftChild.nodeType
      } else {
        currentRightOperation = NodeType.Sum
        for (rightChild <- rightSubtreeNodes) {
          if (rightChild.isSum || rightChild.isSubtraction) {
            currentRightOperation = rightChild.nodeType
          } else {
            val mainNode = ExpressionNode.getEmptyNode(0, braceNumber)
            leftChild.braceNumber = braceNumber
            rightChild.braceNumber = braceNumber

            if (currentLeftOperation == currentRightOperation) {
              mainNode.nodeType = NodeType.Sum
            } else {
              mainNode.nodeType = NodeType.Subtraction
            }

            mainNode.rightNode = applyOperationForNodes(leftChild, rightChild, operation, braceNumber)

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

  protected def applyOperationForNodes(leftNode: ExpressionNode, rightNode: ExpressionNode, operation: NodeType.Value, braceNumber: Int): ExpressionNode = {
    val newNode = ExpressionNode.getEmptyNode(braceNumber, operation)
    newNode.leftNode = leftNode.getCopy()
    newNode.rightNode = rightNode.getCopy()
    newNode.leftNode.braceNumber = braceNumber
    newNode.rightNode.braceNumber = braceNumber

    if (rightNode.isMultiplication || rightNode.isDivision) {
      newNode.rightNode.braceNumber = braceNumber + 1
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
