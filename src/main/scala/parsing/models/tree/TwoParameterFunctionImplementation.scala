package parsing.models.tree

class TwoParameterFunctionImplementation(val firstParameter: TokenValue, val secondParameter: TokenValue, val declaration: TwoParameterFunctionDeclaration) extends FunctionImplementation {
  override def executeStr(): String = declaration.executeStr(firstParameter, secondParameter)
}
