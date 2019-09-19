package parsing.models.tree

class TokenValue {
  var constName: String = ""
  var intValue: Int = 0

  def getStrValue(): String = {
    if (constName != null && constName != "") {
      constName
    } else {
      intValue.toString
    }
  }
}
