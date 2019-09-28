package parsing.models.exceptions

class IncorrectDotException extends Exception {
  override def getMessage: String = "Incorrect position of dot"
}
