package parsing.models.exceptions

class IncorrectFunctionException extends BaseParsingException {
  override def getMessage: String = "Incorrect usage of function"
}
