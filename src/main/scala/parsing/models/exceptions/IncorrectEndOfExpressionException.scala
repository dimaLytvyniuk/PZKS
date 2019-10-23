package parsing.models.exceptions

class IncorrectEndOfExpressionException extends BaseParsingException {
  override def getMessage: String = "Incorrect end of expression"
}
