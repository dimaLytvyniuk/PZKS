package parsing.models.exceptions

class IncorrectCountOfBracesException extends Exception {
  override def getMessage: String = "Incorrect count of braces"
}
