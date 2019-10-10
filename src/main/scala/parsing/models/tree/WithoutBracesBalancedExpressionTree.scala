package parsing.models.tree

class WithoutBracesBalancedExpressionTree extends BalancedExpressionTree {
  def getTreeWithOpenBraces(): ExpressionTree = {
    getCopy()
  }
}
