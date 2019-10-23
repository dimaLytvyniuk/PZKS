package parsing.models.exceptions

class IncorrectOpenBraceException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of open brace"
}
