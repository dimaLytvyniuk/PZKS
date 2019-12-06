package pipelines

import parsing.ExpressionParsingService
import parsing.models.exceptions.BaseParsingException
import parsing.models.tree.ExpressionTree
import parsing.models.views.{ExceptionModel, InputExpressionModel}
import pipelines.models.StaticRebuildingPipelineContainer
import pipelines.views.PipelineContainerViewModel

import scala.collection.mutable.ArrayBuffer

class PipelineService {
  def emulateStaticRebuildingPipeline(inputExpression: InputExpressionModel): PipelineContainerViewModel = {
    var pipelineContainer: StaticRebuildingPipelineContainer = null
    var exceptionModel: ExceptionModel = null

    try {
      val tree = buildBalancedTree(inputExpression.expression)
      pipelineContainer = new StaticRebuildingPipelineContainer(tree)
      pipelineContainer.emulateCalculating()
    } catch {
      case parsingException: BaseParsingException => { exceptionModel = ExceptionModel(parsingException.getMessage); println(parsingException); }
      case e: Throwable => throw e
    }

    PipelineContainerViewModel.createFromExpressionTree(pipelineContainer, exceptionModel, inputExpression.expression)
  }

  def emulateAllVariantsStaticRebuildingPipeline(inputExpression: InputExpressionModel): Array[PipelineContainerViewModel] = {
    var exceptionModel: ExceptionModel = null
    var treeVariants: Array[ExpressionTree] = null

    try {
      treeVariants = getAllTreeVariants(inputExpression.expression)
    } catch {
      case parsingException: BaseParsingException => { exceptionModel = ExceptionModel(parsingException.getMessage); println(parsingException); }
      case e: Throwable => throw e
    }

    if (exceptionModel == null) {
      val pipelineContainers = treeVariants.map(tree => {
        val pipelineContainer = new StaticRebuildingPipelineContainer(tree)
        pipelineContainer.emulateCalculating()
        PipelineContainerViewModel.createFromExpressionTree(pipelineContainer, null, inputExpression.expression)
      })

      pipelineContainers
    } else {
      Array(PipelineContainerViewModel.createFromExpressionTree(null, exceptionModel, inputExpression.expression))
    }
  }

  private def buildBalancedTree(expression: String): ExpressionTree = {
    val parsingService = new ExpressionParsingService()
    parsingService.buildBalancedExpressionTree(expression)
  }

  private def getAllTreeVariants(expression: String): Array[ExpressionTree] = {
    val parsingService = new ExpressionParsingService()
    var allVariants = new ArrayBuffer[ExpressionTree]

    val balancedTree = parsingService.buildBalancedExpressionTree(expression)
    val withoutBracesTree = parsingService.buildWithoutBracesBalancedExpressionTree(expression)
    val commutativeTree = parsingService.buildCommutativeExpressionTree(expression)

    allVariants += balancedTree
    allVariants ++= withoutBracesTree.treeVariants
    allVariants ++= commutativeTree.treeVariants

    for (tree <- allVariants) {
      println(tree.evaluateWithoutBracesStr())
    }

    allVariants.toArray
  }
}
