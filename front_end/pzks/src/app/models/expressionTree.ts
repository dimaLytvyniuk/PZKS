import { ExpressionNode } from './expressionNode';

export class ExpressionTree {
    head: ExpressionNode;
    supportedFunctions: string[];
    evaluatedResults: string[];
    treeType: string;
}
