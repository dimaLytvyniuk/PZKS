package parsing.models.tree

class TokenValue {
  var constName: String = ""
  var numberValue: Double = 0
  var sign: Int = 1;

  def this(constName: String, sign: Int) {
    this()
    this.constName = constName
    this.sign = sign
  }

  def this(numberValue: Double, sign: Int) {
    this()
    this.numberValue = numberValue
    this.sign = sign
  }

  def getStrValue(): String = {
    if (constName != null && constName != "") {
      if (sign == -1) {
        constName = "-" + constName;
      }
      constName
    } else {
      (numberValue * sign).toString
    }
  }
}
