package parsing.models.exceptions

class IncorrectDotException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of dot"
}
