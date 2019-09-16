package parsing.models.exceptions

class IncorrectOpenBraceException extends Exception {
  override def getMessage: String = "Incorrect place for open brace"
}
