package parsing.models.exceptions

class IncorrectFunctionException extends Exception {
  override def getMessage: String = "Incorrect usage of function"
}
