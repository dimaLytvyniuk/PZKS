package parsing.models.tree

object NodeType extends Enumeration {
  type OperationTypes = Value
  val Sum, Subtraction, Division, Multiplication, HasValue, None  = Value
}
