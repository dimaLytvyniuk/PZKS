package parsing.models.tree

class WithoutBracesBalancedExpressionTree extends ExpressionTree {
  def getTreeWithOpenBraces(): ExpressionTree = {
    val copiedTree = getCopy()

    copiedTree
  }


}
