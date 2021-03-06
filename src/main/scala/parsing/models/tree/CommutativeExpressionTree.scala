package parsing.models.tree

import scala.collection.immutable.HashMap
import scala.collection.{SortedMap, immutable, mutable}
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

class CommutativeExpressionTree extends NewBalancedTree {
  override def treeType = "commutative"

  private val _operationsComplexity = Map(
    NodeType.HasValue -> 0,
    NodeType.Sum -> 1,
    NodeType.Subtraction -> 1,
    NodeType.Multiplication -> 2,
    NodeType.Division -> 4)

  private var _variantsOfTree = new ArrayBuffer[ExpressionNode]

  def calculateCommutative(): Unit = {
    applyCommutativity(_head)

    _evaluatedResults += evaluateWithoutBracesStr()
    val headCopy = _head.getCopy()
    calculateAllAvailableCommutativeVariants()

    val newTree = new CommutativeExpressionTree {
      _head = headCopy
    }
    newTree.newBalanceTree()
    _treeVariants += newTree
    newBalanceTree()
  }

  protected def calculateAllAvailableCommutativeVariants(): Unit = {
    val headVariants = calculateAllAvailableCommutativeVariants(_head)

    for (variant <- headVariants) {
      _evaluatedResults += variant.evaluateWithoutBracesStr()

      val newVariant = variant.getCopy()
      val newTree = new CommutativeExpressionTree {
        _head = newVariant
      }
      newTree.newBalanceTree()
      _treeVariants += newTree
    }
  }

  protected def calculateAllAvailableCommutativeVariants(node: ExpressionNode): Array[ExpressionNode] = {
    node.nodeType match {
      case NodeType.Sum => calculateAllCommutativityVariants(node, NodeType.Sum)
      case NodeType.Subtraction => calculateAllCommutativityVariants(node, NodeType.Subtraction)
      case NodeType.Division => calculateAllCommutativityVariants(node, NodeType.Division)
      case NodeType.Multiplication => calculateAllCommutativityVariants(node, NodeType.Multiplication)
      case _ => new Array[ExpressionNode](0)
    }
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
    applySubtractionCommutativity(expressionNode)
    val allSumNodes = getAllSumNodesInSameBraces(expressionNode, applyCommutativityForChilds)

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
  }

  protected def applySubtractionCommutativity(expressionNode: ExpressionNode): Unit = {
    val allSubtractionNodes = getAllSubtractionNodesInSameBraces(expressionNode, applyCommutativityForChilds)

    for (i <- 1 until allSubtractionNodes.length) {
      for (j <- 0 until allSubtractionNodes.length - i) {
        compareAndSwapSubtractionNodes(allSubtractionNodes(j), allSubtractionNodes(j+1))
      }
    }
  }

  protected def applyMultiplicationCommutativity(expressionNode: ExpressionNode): Unit = {
    val allMultiplicationNodes = getAllMultiplicationNodes(expressionNode, applyCommutativityForChilds)

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
    val allDivisionNodes = getAllDivisionNodes(expressionNode, applyCommutativityForChilds)

    for (i <- 1 until allDivisionNodes.length) {
      for (j <- 0 until allDivisionNodes.length - i) {
        compareAndSwapDivisionNodes(allDivisionNodes(j), allDivisionNodes(j+1))
      }
    }
  }

  protected def calculateAllCommutativityVariants(expressionNode: ExpressionNode, nodeType: NodeType.Value): Array[ExpressionNode] = {
    val allCommutativeNodes = getAllNodesInSameBracesForCommutative(expressionNode, nodeType)
    val complexityMap = getComplexitiesPermutationsMap(allCommutativeNodes, nodeType)

    val totalCountOfVariants = complexityMap.foldLeft(1)((x, y) => x * y._2.length)

    var leftCountOfVariants = totalCountOfVariants
    var lenMap = HashMap[Int, Int]()
    var currentIndexMap = mutable.HashMap[Int, Int]()
    var index = 0
    for ((complexity, permutations) <- complexityMap) {
      leftCountOfVariants /= permutations.length
      if (leftCountOfVariants == 0) {
        leftCountOfVariants = 1
      }

      lenMap += (index -> leftCountOfVariants)
      currentIndexMap += (index -> -1)

      index += 1
    }

    val resultArray = new Array[ExpressionNode](totalCountOfVariants)
    for (i <- 0 until totalCountOfVariants) {
      val newNode = expressionNode.getCopy()
      val allNewNodes = getAllNodesInSameBracesForCommutative(newNode, nodeType)

      var complexityIndex = 0
      var nodeIndex = 0
      for ((complexity, permutations) <- complexityMap) {
        var currentPermutationIndex = currentIndexMap(complexityIndex)
        if (i % lenMap(complexityIndex) == 0) {
          currentPermutationIndex += 1
          if (currentPermutationIndex == permutations.length) {
            currentPermutationIndex = 0
          }

          currentIndexMap.update(complexityIndex, currentPermutationIndex)
        }

        val currentPermutation = permutations(currentPermutationIndex)
        for (node <- currentPermutation) {
          setNewCommutativeNode(nodeType, nodeIndex, allNewNodes, node.getCopy())

          nodeIndex += 1
        }
        complexityIndex += 1
      }

      resultArray(i) = newNode
    }

    resultArray
  }

  protected def setNewCommutativeNode(nodeType: NodeType.Value, nodeIndex: Int, sourceNodes: ArrayBuffer[ExpressionNode], newNode: ExpressionNode): Unit = {
    nodeType match {
      case NodeType.Sum => setNewSumMultiplicationCommutativeNode(nodeIndex, sourceNodes, newNode)
      case NodeType.Multiplication => setNewSumMultiplicationCommutativeNode(nodeIndex, sourceNodes, newNode)
      case NodeType.Division => setNewSubtractionDivisionCommutativeNode(nodeIndex, sourceNodes, newNode)
      case NodeType.Subtraction => setNewSubtractionDivisionCommutativeNode(nodeIndex, sourceNodes, newNode)
    }
  }

  protected def setNewSumMultiplicationCommutativeNode(nodeIndex: Int, sourceNodes: ArrayBuffer[ExpressionNode], newNode: ExpressionNode): Unit = {
    if (nodeIndex < sourceNodes.length) {
      sourceNodes(nodeIndex).leftNode = newNode
    } else {
      sourceNodes(nodeIndex - 1).rightNode = newNode
    }
  }

  protected def setNewSubtractionDivisionCommutativeNode(nodeIndex: Int, sourceNodes: ArrayBuffer[ExpressionNode], newNode: ExpressionNode): Unit = {
    sourceNodes(nodeIndex).rightNode = newNode
  }

  protected def getAllNodesInSameBracesForCommutative(startNode: ExpressionNode, nodeType: NodeType.Value): ArrayBuffer[ExpressionNode] = {
    nodeType match {
      case NodeType.Sum => getAllSumNodesInSameBraces(startNode, doNothingForChilds)
      case NodeType.Subtraction => getAllSubtractionNodesInSameBraces(startNode, doNothingForChilds)
      case NodeType.Multiplication => getAllMultiplicationNodes(startNode, doNothingForChilds)
      case NodeType.Division => getAllDivisionNodes(startNode, doNothingForChilds)
      case _ => throw new IllegalArgumentException
    }
  }

  protected def getAllSumNodesInSameBraces(startNode: ExpressionNode, onAcceptedNode: (ExpressionNode, ExpressionNode => Boolean) => Unit): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && currentNode.isSum) {
      onAcceptedNode(currentNode, (node) => node.isMultiplication || node.isDivision)

      nodes += currentNode

      currentNode = currentNode.rightNode
    }

    nodes
  }

  protected def getAllSubtractionNodesInSameBraces(startNode: ExpressionNode, onAcceptedNode: (ExpressionNode, ExpressionNode => Boolean) => Unit): ArrayBuffer[ExpressionNode] = {
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
      onAcceptedNode(x, (node) => node.isMultiplication || node.isDivision)
    })

    nodes
  }

  protected def getAllMultiplicationNodes(startNode: ExpressionNode, onAcceptedNode: (ExpressionNode, ExpressionNode => Boolean) => Unit): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && currentNode.isMultiplication) {
      onAcceptedNode(currentNode, node => node.isDivision)

      nodes += currentNode

      currentNode = currentNode.rightNode
    }

    nodes
  }

  protected def getAllDivisionNodes(startNode: ExpressionNode, onAcceptedNode: (ExpressionNode, ExpressionNode => Boolean) => Unit): ArrayBuffer[ExpressionNode] = {
    val nodes = new ArrayBuffer[ExpressionNode]()

    val startBraces = startNode.braceNumber
    var currentNode = startNode
    while (currentNode != null && currentNode.braceNumber == startBraces && currentNode.isDivision) {
      onAcceptedNode(currentNode, node => node.isMultiplication)

      nodes += currentNode

      currentNode = currentNode.leftNode
    }

    nodes.reverse
  }

  protected def compareAndSwapSumNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    //compareAndSwapSumSumNode(firstNode.leftNode, secondNode.leftNode)

    (firstNode, secondNode) match {
      case x if (x._1.isLeftNodeInSameBraces && x._1.leftNode.isSubtraction && x._2.isLeftNodeInSameBraces && x._2.leftNode.isSubtraction) => {
        compareAndSwapSubtractionSubtractionNode(x._1.leftNode, x._2.leftNode)
        compareAndSwapSubtractionSubtreeSumNode(x._1.leftNode, x._2.leftNode)
        compareAndSwapSumSubtractionSubtreeNode(x._1.leftNode, x._2.leftNode)
      }
      case x if (x._1.isLeftNodeInSameBraces && x._1.leftNode.isSubtraction) => {
        compareAndSwapSubtractionSumNode(x._1.leftNode, x._2.leftNode)
        compareAndSwapSubtractionSubtreeSumNode(x._1.leftNode, x._2.leftNode)
      }
      case x if (x._2.isLeftNodeInSameBraces && x._2.leftNode.isSubtraction) => {
        compareAndSwapSumSubtractionNode(x._1.leftNode, x._2.leftNode)
        compareAndSwapSumSubtractionSubtreeNode(x._1.leftNode, x._2.leftNode)
      }
      case _ => compareAndSwapSumSumNode(firstNode.leftNode, secondNode.leftNode)
    }
  }

  protected def compareAndSwapLastSumNode(secondNode: ExpressionNode): Unit = {
    //compareAndSwapSumSumNode(secondNode.leftNode, secondNode.rightNode)

    secondNode match {
      case x if (secondNode.isLeftNodeInSameBraces && secondNode.leftNode.isSubtraction && secondNode.isRightNodeInSameBraces && secondNode.rightNode.isSubtraction) => {
        compareAndSwapSubtractionSubtractionNode(secondNode.leftNode, secondNode.rightNode)
        compareAndSwapSubtractionSubtreeSumNode(secondNode.leftNode, secondNode.rightNode)
        compareAndSwapSumSubtractionSubtreeNode(secondNode.leftNode, secondNode.rightNode)
      }
      case x if (secondNode.isLeftNodeInSameBraces && secondNode.leftNode.isSubtraction) =>  {
          compareAndSwapSubtractionSumNode(secondNode.leftNode, secondNode.rightNode)
          compareAndSwapSubtractionSubtreeSumNode(secondNode.leftNode, secondNode.rightNode)
        }
      case x if (secondNode.isRightNodeInSameBraces && secondNode.rightNode.isSubtraction) => {
        compareAndSwapSumSubtractionNode(secondNode.leftNode, secondNode.rightNode)
        compareAndSwapSumSubtractionSubtreeNode(secondNode.leftNode, secondNode.rightNode)
      }
      case _ => compareAndSwapSumSumNode(secondNode.leftNode, secondNode.rightNode)
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

  protected def compareAndSwapSubtractionSubtreeSumNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val secondNodeComplexity = secondNode.complexity(_operationsComplexity)
    var rightSubtreeHead = secondNode
    val subtractionNodes = getAllSubtractionNodesInSameBraces(firstNode, doNothingForChilds)

    for (i <- subtractionNodes.indices) {
      if (secondNodeComplexity < subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
        if (!rightSubtreeHead.isSubtraction || rightSubtreeHead.braceNumber != subtractionNodes(i).braceNumber) {
          moveSubtractionNode(subtractionNodes(i), rightSubtreeHead)
          rightSubtreeHead = subtractionNodes(i)
        } else {
          if (rightSubtreeHead.rightNode.complexity(_operationsComplexity) <= subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
            moveSubtractionNode(subtractionNodes(i), rightSubtreeHead)
            rightSubtreeHead = subtractionNodes(i)
          } else {
            var targetNode = rightSubtreeHead
            while (targetNode.isSubtraction && targetNode.rightNode.complexity(_operationsComplexity) > subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
              targetNode = targetNode.leftNode
            }
            moveSubtractionNode(subtractionNodes(i), targetNode)
          }
        }
      }
    }
  }

  protected def compareAndSwapSumSubtractionNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val lastSecondLeftNode = secondNode.lastLeftSubtractionNodeSameBraces()

    if (firstNode.complexity(_operationsComplexity) > lastSecondLeftNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode, lastSecondLeftNode)
    }
  }

  protected def compareAndSwapSumSubtractionSubtreeNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    val lastSecondNode = secondNode.lastLeftSubtractionNodeSameBraces()
    val lastSecondNodeComplexity = lastSecondNode.complexity(_operationsComplexity)
    var leftSubtreeHead = firstNode
    val subtractionNodes = getAllSubtractionNodesInSameBraces(secondNode, doNothingForChilds)

    for (i <- subtractionNodes.indices) {
      if (lastSecondNodeComplexity >= subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
        if (!leftSubtreeHead.isSubtraction || leftSubtreeHead.braceNumber != subtractionNodes(i).braceNumber) {
          moveSubtractionNode(subtractionNodes(i), leftSubtreeHead)
          leftSubtreeHead = subtractionNodes(i)
        } else {
          if (leftSubtreeHead.rightNode.complexity(_operationsComplexity) <= subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
            moveSubtractionNode(subtractionNodes(i), leftSubtreeHead)
            leftSubtreeHead = subtractionNodes(i)
          } else {
            var targetNode = leftSubtreeHead
            while (targetNode.isSubtraction && targetNode.rightNode.complexity(_operationsComplexity) > subtractionNodes(i).rightNode.complexity(_operationsComplexity)) {
              targetNode = targetNode.leftNode
            }
            moveSubtractionNode(subtractionNodes(i), targetNode)
          }
        }
      }
    }
  }

  protected def compareAndSwapSumSumNode(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.complexity(_operationsComplexity) > secondNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode, secondNode)
    }
  }

  protected def compareAndSwapDivisionNodes(firstNode: ExpressionNode, secondNode: ExpressionNode): Unit = {
    if (firstNode.rightNode.complexity(_operationsComplexity) > secondNode.rightNode.complexity(_operationsComplexity)) {
      swapNodes(firstNode.rightNode, secondNode.rightNode)
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

  protected def moveSubtractionNode(subtractionNode: ExpressionNode, targetNode: ExpressionNode): Unit = {
    val subtractionParent = subtractionNode.parent
    val targetParent = targetNode.parent
    val isTargetRightChild = targetNode.isRightChild

    if (subtractionNode.isRightChild) {
      subtractionParent.rightNode = subtractionNode.leftNode
    } else {
      subtractionParent.leftNode = subtractionNode.leftNode
    }

    subtractionNode.leftNode = targetNode

    if (isTargetRightChild) {
      targetParent.rightNode = subtractionNode
    } else {
      targetParent.leftNode = subtractionNode
    }
  }

  protected def applyCommutativityForChilds(currentNode: ExpressionNode, isApplicableChild: ExpressionNode => Boolean):Unit = {
    if (!currentNode.isLeftNodeInSameBraces || isApplicableChild(currentNode.leftNode)) {
      applyCommutativity(currentNode.leftNode)
    }

    if (!currentNode.isRightNodeInSameBraces || isApplicableChild(currentNode.rightNode)) {
      applyCommutativity(currentNode.rightNode)
    }
  }

  protected def doNothingForChilds(currentNode: ExpressionNode, isApplicableChild: ExpressionNode => Boolean):Unit = {

  }

  protected def getComplexitiesPermutationsMap(nodes: ArrayBuffer[ExpressionNode], nodeType: NodeType.Value): SortedMap[Int, ArrayBuffer[Array[ExpressionNode]]] = {
    nodeType match {
      case NodeType.Sum => getSumMultiplicationComplexitiesPermutationsMap(nodes, NodeType.Sum)
      case NodeType.Multiplication => getSumMultiplicationComplexitiesPermutationsMap(nodes, NodeType.Multiplication)
      case NodeType.Division => getSubtractionDivisionComplexitiesPermutationsMap(nodes)
      case NodeType.Subtraction => getSubtractionDivisionComplexitiesPermutationsMap(nodes)
    }
  }

  protected def getSumMultiplicationComplexitiesPermutationsMap(nodes: ArrayBuffer[ExpressionNode], nodeType: NodeType.Value): SortedMap[Int, ArrayBuffer[Array[ExpressionNode]]] = {
    var complexitiesMap = HashMap[Int, ArrayBuffer[ExpressionNode]]()

    for (node <- nodes) {
      val nodeComplexity = node.leftNode.complexity(_operationsComplexity)
      if (!complexitiesMap.contains(nodeComplexity)) {
        complexitiesMap += (nodeComplexity -> new ArrayBuffer[ExpressionNode]())
      }

      complexitiesMap(nodeComplexity) += node.leftNode

      if (!node.isRightNodeInSameBraces || node.rightNode.nodeType != nodeType) {
        val rightNodeComplexity = node.rightNode.complexity(_operationsComplexity)
        if (!complexitiesMap.contains(rightNodeComplexity)) {
          complexitiesMap += (rightNodeComplexity -> new ArrayBuffer[ExpressionNode]())
        }

        complexitiesMap(rightNodeComplexity) += node.rightNode
      }
    }

    getPermutationComplexitiesMap(complexitiesMap)
  }

  protected def getSubtractionDivisionComplexitiesPermutationsMap(nodes: ArrayBuffer[ExpressionNode]): SortedMap[Int, ArrayBuffer[Array[ExpressionNode]]] = {
    var complexitiesMap = HashMap[Int, ArrayBuffer[ExpressionNode]]()

    for (node <- nodes) {
      val rightNodeComplexity = node.rightNode.complexity(_operationsComplexity)
      if (!complexitiesMap.contains(rightNodeComplexity)) {
        complexitiesMap += (rightNodeComplexity -> new ArrayBuffer[ExpressionNode]())
      }

      complexitiesMap(rightNodeComplexity) += node.rightNode
    }

    getPermutationComplexitiesMap(complexitiesMap)
  }

  protected def getPermutationComplexitiesMap(complexitiesMap: HashMap[Int, ArrayBuffer[ExpressionNode]]): SortedMap[Int, ArrayBuffer[Array[ExpressionNode]]] = {
    var permutationMap = SortedMap[Int, ArrayBuffer[Array[ExpressionNode]]]()

    for ((complexity, values) <- complexitiesMap) {
      if (complexity == 0) {
        permutationMap += (complexity -> ArrayBuffer(values.toArray))
      } else {
        permutationMap += (complexity -> getAllPermutationValues(values))
      }
    }

    permutationMap
  }

  protected def getAllPermutationValues(nodes: ArrayBuffer[ExpressionNode]): ArrayBuffer[Array[ExpressionNode]] = {
    val nodeVariants = new Array[ArrayBuffer[ExpressionNode]](nodes.length)

    for (i <- nodes.indices) {
      nodeVariants(i) = new ArrayBuffer[ExpressionNode]()

      if (nodes(i).isOperation) {
        val allVariants = calculateAllAvailableCommutativeVariants(nodes(i))
        allVariants.foreach(x => nodeVariants(i) += x)
      } else {
        nodeVariants(i) += nodes(i)
      }
    }

    val totalCountOfVariants = nodeVariants.foldLeft(1)((x, y) => x * y.length)

    var leftCountOfVariants = totalCountOfVariants
    val lenMap = new Array[Int](nodeVariants.length)
    val currentIndexArray = new Array[Int](nodeVariants.length)
    for (i <- nodeVariants.indices) {
      leftCountOfVariants /= nodeVariants(i).length
      if (leftCountOfVariants == 0) {
        leftCountOfVariants = 1
      }

      lenMap(i) = leftCountOfVariants
      currentIndexArray(i) = -1
    }

    val combinations = new Array[Array[ExpressionNode]](totalCountOfVariants)
    for (i <- 0 until totalCountOfVariants) {
      combinations(i) = new Array[ExpressionNode](nodeVariants.length)

      for (j <- nodeVariants.indices) {
        var currentNodeVariantIndex = currentIndexArray(j)
        if (i % lenMap(j) == 0) {
          currentNodeVariantIndex += 1
          if (currentNodeVariantIndex == nodeVariants(j).length) {
            currentNodeVariantIndex = 0
          }

          currentIndexArray(j) =  currentNodeVariantIndex
        }

        combinations(i)(j) = nodeVariants(j)(currentNodeVariantIndex)
      }
    }

    var permutations = new ArrayBuffer[Array[ExpressionNode]]()
    for (combination <- combinations) {
      permutations ++= combination.permutations
    }

    permutations
  }
}

object CommutativeExpressionTree {
  def getCopy(tree: CommutativeExpressionTree): CommutativeExpressionTree = {
    val newTree = new CommutativeExpressionTree
    newTree._head = tree.head.getCopy()
    newTree._usedVariables ++= tree.usedVariables

    newTree
  }

  def getCopy(tree: ExpressionTree): CommutativeExpressionTree = {
    val newTree = new CommutativeExpressionTree
    newTree._head = tree.head.getCopy()
    newTree._usedVariables ++= tree.usedVariables

    newTree
  }
}
