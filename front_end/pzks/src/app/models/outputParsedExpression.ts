import { ExpressionTree } from './expressionTree';
import { ExceptionModel } from './exceptionModel';

export class OutputParsedExpression {
    expressionTree: ExpressionTree;
    exceptionModel: ExceptionModel;
    evaluatedResult: string;
    inputExpression: string;
}
