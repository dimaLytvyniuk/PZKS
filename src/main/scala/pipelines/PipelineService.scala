package pipelines

import parsing.models.exceptions.BaseParsingException
import parsing.models.views.{ExceptionModel, InputExpressionModel}
import pipelines.models.StaticRebuildingPipelineContainer
import pipelines.views.PipelineContainerViewModel

class PipelineService {
  def emulateStaticRebuildingPipeline(inputExpression: InputExpressionModel): PipelineContainerViewModel = {
    val pipelineContainer = new StaticRebuildingPipelineContainer(inputExpression.expression)
    var exceptionModel: ExceptionModel = null

    try {
      pipelineContainer.emulateCalculating()
    } catch {
      case parsingException: BaseParsingException => { exceptionModel = ExceptionModel(parsingException.getMessage); println(parsingException); }
      case e: Throwable => throw e
    }

    PipelineContainerViewModel.createFromExpressionTree(pipelineContainer, exceptionModel, inputExpression.expression)
  }
}
