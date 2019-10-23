package parsing.models.exceptions

class IncorrectSymbolPositionException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of symbol"
}
