package parsing.models.exceptions

class IncorrectNumberPositionException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of number"
}
