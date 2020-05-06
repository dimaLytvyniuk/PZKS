package taskPlanner.models

class ExecutionTask(val id: String, val weight: Int, val dependencies: Array[String]) {
  var state: ExecutionTaskState.Value = ExecutionTaskState.Pending
}
