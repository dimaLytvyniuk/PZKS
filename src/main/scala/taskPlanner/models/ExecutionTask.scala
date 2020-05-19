package taskPlanner.models

class ExecutionTask(val id: String, val weight: Int, val priority: Int, val dependencies: Array[String]) {
  private var _state: ExecutionTaskState.Value = ExecutionTaskState.Pending
  private var _progress: Int = 0

  def state = _state

  def state_=(state: ExecutionTaskState.Value) {
    _state = state
  }

  def isHasDependency = dependencies.nonEmpty

  def isPending = _state == ExecutionTaskState.Pending
  def isWaitingData = _state == ExecutionTaskState.WaitingData
  def isInProgress = _state == ExecutionTaskState.Executing
  def isCompleted = _state == ExecutionTaskState.Completed
  def isReadyForExecution = _state == ExecutionTaskState.ReadyForExecution

  def nonCompletedWorkCapacity = weight - _progress

  def updateProgress(completedWork: Int): Unit = _progress = _progress + completedWork
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
