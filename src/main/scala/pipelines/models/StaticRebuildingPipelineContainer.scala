package pipelines.models

import parsing.ExpressionParsingService
import parsing.models.tree.{ExpressionNode, ExpressionTree, NodeType}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class StaticRebuildingPipelineContainer(val expression: String) {
  val pipelineCount = 3
  private var _pipelines: Array[StaticRebuildingPipeline] = null
  private var _tactNumbers: ArrayBuffer[Int] = null
  private var _tree: ExpressionTree = null
  private var _byLevelNodes: mutable.HashMap[Int, mutable.HashMap[Int, mutable.Queue[ExpressionNode]]] = null

  private val _operationsComplexity = Map(
    NodeType.HasValue -> 0,
    NodeType.Sum -> 1,
    NodeType.Subtraction -> 1,
    NodeType.Multiplication -> 2,
    NodeType.Division -> 4)

  def pipelines:Array[StaticRebuildingPipeline] = _pipelines
  def tactNumbers: ArrayBuffer[Int] = _tactNumbers
  def tree: ExpressionTree = _tree

  def emulateCalculating(): Unit = {
    _tree = buildTree()
    _pipelines = new Array[StaticRebuildingPipeline](pipelineCount)
    _tactNumbers = new ArrayBuffer[Int]()

    dropNodesByLevels()
    preparePipelines()
    calculate()
  }

  private def buildTree(): ExpressionTree = {
    val parsingService = new ExpressionParsingService()
    parsingService.buildBalancedExpressionTree(expression)
  }

  private def dropNodesByLevels(): Unit = {
    _byLevelNodes = new mutable.HashMap[Int, mutable.HashMap[Int, mutable.Queue[ExpressionNode]]]
    val maxLevel = tree.maxLevel

    val queue = new mutable.Queue[ExpressionNode]()
    queue.enqueue(tree.head)

    while (!queue.isEmpty) {
      val node = queue.dequeue()
      var currentNodeLevel = node.level

      if (node.rightNode.isHasValue && node.leftNode.isHasValue) {
        if (node.leftNode.level < (maxLevel - 1)) {
          currentNodeLevel = maxLevel - 1
        }
      } else {
        if (!node.leftNode.isHasValue) {
          queue.enqueue(node.leftNode)
        }

        if (!node.rightNode.isHasValue) {
          queue.enqueue(node.rightNode)
        }
      }

      if (!_byLevelNodes.contains(currentNodeLevel)) {
        _byLevelNodes(currentNodeLevel) = new mutable.HashMap[Int, mutable.Queue[ExpressionNode]]()
      }

      val operationComplexity = _operationsComplexity(node.nodeType)
      if (!_byLevelNodes(currentNodeLevel).contains(operationComplexity)) {
        _byLevelNodes(currentNodeLevel)(operationComplexity) = new mutable.Queue[ExpressionNode]()
      }
      _byLevelNodes(currentNodeLevel)(operationComplexity) += node
    }
  }

  private def calculate(): Unit = {
    val maxLevel = tree.maxLevel
    var tactCount = 0

    for (i <- (maxLevel - 1) to 0 by -1) {
      if (_byLevelNodes.contains(i)) {
        var isLevelCalculated = false

        var complexityQueue = new mutable.Queue[Int]()
        complexityQueue ++= _byLevelNodes(i).keys
        var currentComplexity = complexityQueue.dequeue()

        while (!isLevelCalculated) {
          if (_byLevelNodes(i)(currentComplexity).isEmpty) {
            if (_pipelines.exists(x => x.hasNext)) {
              doTactForAllPipelines(null)
              tactCount += currentComplexity
              _tactNumbers += tactCount
            } else {
              if (complexityQueue.isEmpty) {
                isLevelCalculated = true
              } else {
                currentComplexity = complexityQueue.dequeue()
                doTactForAllPipelines(_byLevelNodes(i)(currentComplexity).dequeue())
                tactCount += currentComplexity
                _tactNumbers += tactCount
              }
            }
          } else {
            doTactForAllPipelines(_byLevelNodes(i)(currentComplexity).dequeue())
            tactCount += currentComplexity
            _tactNumbers += tactCount
          }
        }
      }
    }
  }

  def dsadas: Unit = {
    val maxLevel = tree.maxLevel
    for (i <- (maxLevel - 1) to 0 by -1) {
      if (_byLevelNodes.contains(i)) {
        for ((complexity, nodes) <- _byLevelNodes(i)) {
          println(s"Level ${i}; Complexity ${complexity}")
          for (node <- nodes) {
            println(node.evaluateWithoutBracesStr())
          }
        }
      }
    }
  }

  private def preparePipelines(): Unit = {
    for (i <- _pipelines.indices) {
      _pipelines(i) = new StaticRebuildingPipeline(i)
    }

    for (i <- 0 until _pipelines.length - 1) {
      onSimplePipelineCompleted(i)
    }
  }

  private def onSimplePipelineCompleted(pipeLineIndex: Int): Unit = {
    _pipelines(pipeLineIndex).onCompleted = node => {
      _pipelines(pipeLineIndex + 1).nextNode = node
    }
  }

  private def doTactForAllPipelines(newNode: ExpressionNode): Unit = {
    _pipelines.foreach(x => x.updateState())
    _pipelines(0).currentNode = newNode
    _pipelines.foreach(x => x.tact())
  }
}
