package parsing.models.tree

object NodeType extends Enumeration {
  type OperationTypes = Value
  val Sum, Subtraction, Division, Multiplication, HasValue, None  = Value

  def checkPrioritization(firstNode: NodeType.Value, secondNode: NodeType.Value): Int = {
    if (secondNode == HasValue) {
      1
    } else if (firstNode == Sum || firstNode == Subtraction) {
      if (secondNode == Division || secondNode == Multiplication) {
        1
      }
      else {
        0
      }
    } else {
      if (secondNode == Division || secondNode == Multiplication) {
        0
      }
      else {
        -1
      }
    }
  }
}
