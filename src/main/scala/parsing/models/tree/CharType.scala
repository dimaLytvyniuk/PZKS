package parsing.models.tree

object CharType extends Enumeration {
  type OperationTypes = Value
  val ArithmeticOperation, Number, Variable, OpenBrace, ClosedBrace, None, OpenedFunctionBrace, FunctionComa, ClosedFunctionBrace, FunctionIntValue, FunctionCharValue  = Value
}
