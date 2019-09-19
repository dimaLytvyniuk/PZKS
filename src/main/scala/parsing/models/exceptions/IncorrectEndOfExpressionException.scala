package parsing.models.exceptions

class IncorrectEndOfExpressionException extends Exception {
  override def getMessage: String = "Incorrect end of expression"
}
