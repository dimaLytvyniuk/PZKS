package taskPlanner.models

class ExecutionTask(val id: String, val weight: Int, val priority: Int, val dependencies: Array[String]) {
  private var _state: ExecutionTaskState.Value = ExecutionTaskState.Pending

  def state = _state

  def state_=(state: ExecutionTaskState.Value) {
    _state = state
  }

  def isHasDependency = dependencies.nonEmpty
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
