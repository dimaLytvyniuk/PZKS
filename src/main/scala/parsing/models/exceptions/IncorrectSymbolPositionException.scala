package parsing.models.exceptions

class IncorrectSymbolPositionException extends Exception {
  override def getMessage: String = "Incorrect position of symbol"
}
