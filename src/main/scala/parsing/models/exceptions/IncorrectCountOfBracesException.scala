package parsing.models.exceptions

class IncorrectCountOfBracesException extends BaseParsingException {
  override def getMessage: String = "Incorrect count of braces"
}
