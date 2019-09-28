package parsing.models.tree

object CharType extends Enumeration {
  type OperationTypes = Value
  val ArithmeticOperation, IntValue, DoubleValue, Variable, OpenBrace, ClosedBrace, None, OpenedFunctionBrace, FunctionComa, Dot  = Value
}
