package parsing.models.tree

object NodeType extends Enumeration {
  type OperationTypes = Value
  val Sum, Subtraction, Division, Multiplication, HasValue, None  = Value

  def checkPrioritization(firstNode: NodeType.Value, secondNode: NodeType.Value): Int = {
    if (secondNode == HasValue) {
      1
    } else if (firstNode == Sum) {
      if (secondNode == Division || secondNode == Multiplication || secondNode == Subtraction) {
        1
      } else {
        0
      }
    } else if (firstNode == Subtraction) {
      if (secondNode == Division || secondNode == Multiplication) {
        1
      } else if (secondNode == Sum) {
        -1
      } else {
        0
      }
    } else if (firstNode == Multiplication) {
      if (secondNode == Sum || secondNode == Subtraction) {
        -1
      } else if (secondNode == Division) {
        1
      } else {
        0
      }
    } else {
      if (secondNode == Sum || secondNode == Subtraction || secondNode == Multiplication) {
        -1
      } else {
        0
      }
    }
  }
}
