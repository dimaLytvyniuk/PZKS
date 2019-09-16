package parsing.models.tree

import NodeType._

class ExpressionNode(var level: Int, var nodeType: NodeType.Value, var value: NodeValue) {
  var rightNode: ExpressionNode = null
  var leftNode: ExpressionNode = null
}
