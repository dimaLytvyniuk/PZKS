package parsing.models.exceptions

class IncorrectNumberPositionException extends Exception {
  override def getMessage: String = "Incorrect position of number"
}
