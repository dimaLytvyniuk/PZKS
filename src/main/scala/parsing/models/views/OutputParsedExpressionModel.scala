package parsing.models.views

final case class OutputParsedExpressionModel(expressionTree: ExpressionTreeViewModel, exceptionModel: ExceptionModel, evaluatedResult: String, inputExpression: String);
