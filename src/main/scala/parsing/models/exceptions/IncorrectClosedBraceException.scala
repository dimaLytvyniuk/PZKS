package parsing.models.exceptions

class IncorrectClosedBraceException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of closed brace"
}
