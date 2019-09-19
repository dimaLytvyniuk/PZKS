package parsing.models.tree

class NodeValue {
  var tokenValue: TokenValue = null
  var functionImplementation: FunctionImplementation = null

  def getStrValue: String = {
    if (tokenValue != null) {
      tokenValue.getStrValue()
    } else {
      functionImplementation.executeStr()
    }
  }
}
