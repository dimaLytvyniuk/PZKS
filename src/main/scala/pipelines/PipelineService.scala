package pipelines

import parsing.ExpressionParsingService
import parsing.models.exceptions.BaseParsingException
import parsing.models.tree.ExpressionTree
import parsing.models.views.{ExceptionModel, InputExpressionModel}
import pipelines.models.StaticRebuildingPipelineContainer
import pipelines.views.PipelineContainerViewModel

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

  private def buildBalancedTree(expression: String): ExpressionTree = {
    val parsingService = new ExpressionParsingService()
    parsingService.buildBalancedExpressionTree(expression)
  }
}
