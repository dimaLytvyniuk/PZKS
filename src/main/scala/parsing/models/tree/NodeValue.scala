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

  def getCopy(): NodeValue = {
    val newNode = new NodeValue
    newNode.tokenValue = if (tokenValue != null) tokenValue.getCopy() else null
    newNode.functionImplementation = functionImplementation

    newNode
  }
}
