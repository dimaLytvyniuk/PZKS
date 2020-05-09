package taskPlanner.models

class ExecutionTask(val id: String, val weight: Int, val priority: Int, val dependencies: Array[String]) {
  var state: ExecutionTaskState.Value = ExecutionTaskState.Pending
}

object ExecutionTask {
  def createFromGraphNode(node: GraphNode, priority: Int, dependencies: Array[String]): ExecutionTask = {
    if (node == null) {
      null
    } else {
      new ExecutionTask(node.id, node.weight, priority, dependencies)
    }
  }
}
