package parsing.models.tree

class TwoParameterFunctionDeclaration(val name: String, onStrExecute: (TokenValue, TokenValue) => String) {
  def executeStr(firstValue: TokenValue, secondValue: TokenValue) = onStrExecute(firstValue, secondValue)
}

object TwoParameterFunctionDeclaration {
  val nameToDeclarationMap = Map("min" -> getMinDeclaration, "max" -> getMaxDeclaration)

  def getMinDeclaration = new TwoParameterFunctionDeclaration("min", (firstParam, secondParam) => s"min(${firstParam.getStrValue()}, ${secondParam.getStrValue()}")
  def getMaxDeclaration = new TwoParameterFunctionDeclaration("min", (firstParam, secondParam) => s"min(${firstParam.getStrValue()}, ${secondParam.getStrValue()}")
}
