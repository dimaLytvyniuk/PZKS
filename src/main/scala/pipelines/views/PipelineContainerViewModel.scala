package pipelines.views

import parsing.models.views.{ExceptionModel, ExpressionNodeViewModel, ExpressionTreeViewModel, OutputParsedExpressionModel}
import pipelines.models.StaticRebuildingPipelineContainer

final case class PipelineContainerViewModel(val tactSteps: Array[Array[String]], val expressionTree: OutputParsedExpressionModel);

object PipelineContainerViewModel {
  def createFromExpressionTree(pipelineContainer: StaticRebuildingPipelineContainer, exceptionModel: ExceptionModel, expression: String): PipelineContainerViewModel = {
    var tactSteps: Array[Array[String]] = null
    var expressionTree: OutputParsedExpressionModel = null

    if (exceptionModel != null) {
      expressionTree = new OutputParsedExpressionModel(null, exceptionModel, null, null)
    } else {
      tactSteps = getTactSteps(pipelineContainer)
      expressionTree = getParsedExpressionModel(pipelineContainer, expression)
    }

    new PipelineContainerViewModel(tactSteps, expressionTree)
  }

  private def getTactSteps(pipelineContainer: StaticRebuildingPipelineContainer): Array[Array[String]] = {
    val tactSteps = new Array[Array[String]](pipelineContainer.tactNumbers.length)
    for (i <- pipelineContainer.tactNumbers.indices) {
      tactSteps(i) = new Array[String](pipelineContainer.pipelineCount + 1)
      tactSteps(i)(0) = pipelineContainer.tactNumbers(i).toString

      for (j <- pipelineContainer.pipelines.indices) {
        tactSteps(i)(j + 1) = pipelineContainer.pipelines(j).steps(i)
      }
    }

    tactSteps
  }

  private def getParsedExpressionModel(pipelineContainer: StaticRebuildingPipelineContainer, expression: String): OutputParsedExpressionModel = {
    val treeViewModel = ExpressionTreeViewModel.createFromExpressionTree(pipelineContainer.tree)
    val evaluatedResult = pipelineContainer.tree.evaluateWithoutBracesStr()

    new OutputParsedExpressionModel(treeViewModel, null, evaluatedResult, expression)
  }
}
