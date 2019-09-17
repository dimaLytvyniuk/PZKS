package parsing.models.exceptions

class IncorrectArithmeticOperationException extends Exception {
  override def getMessage: String = "Incorrect position of arithmetic operation"
}
