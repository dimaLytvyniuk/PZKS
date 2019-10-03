package parsing.models.tree

class BalancedExpressionTree extends ExpressionTree {
  protected override def addFunction(): Unit = {
    println("in balanced tree")

    super.addFunction()
  }
}
