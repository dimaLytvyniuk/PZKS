package pipelines.models

import parsing.ExpressionParsingService
import parsing.models.tree.{ExpressionTree, NodeType}

import scala.collection.mutable.ArrayBuffer

class StaticRebuildingPipelineContainer(val expression: String) {
  val pipelineCount = 4
  private var _pipelines: Array[StaticRebuildingPipeline] = null
  private var _tactNumbers: ArrayBuffer[Int] = null
  private var _tree: ExpressionTree = null

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
  }

  private def buildTree(): ExpressionTree = {
    val parsingService = new ExpressionParsingService()
    parsingService.buildBalancedExpressionTree(expression)
  }
}
