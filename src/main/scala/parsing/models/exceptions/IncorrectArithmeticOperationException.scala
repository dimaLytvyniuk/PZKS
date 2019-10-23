package parsing.models.exceptions

class IncorrectArithmeticOperationException extends BaseParsingException {
  override def getMessage: String = "Incorrect position of arithmetic operation"
}
