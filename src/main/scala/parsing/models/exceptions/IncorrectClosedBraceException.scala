package parsing.models.exceptions

class IncorrectClosedBraceException extends Exception {
  override def getMessage: String = "Incorrect position of closed brace"
}
