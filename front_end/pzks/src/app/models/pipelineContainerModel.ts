import { OutputParsedExpression } from './outputParsedExpression';
import { CalculationStatisticModel } from './calculationStatisticModel';

export class PipelineContainerModel {
    tactSteps: string[][];
    expressionTree: OutputParsedExpression;
    calculationStatistic: CalculationStatisticModel;
}
