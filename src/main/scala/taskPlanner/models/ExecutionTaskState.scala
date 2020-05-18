package taskPlanner.models

object ExecutionTaskState extends Enumeration {
  type OperationTypes = Value
  val Pending, WaitingData, Executing, Completed = Value
}
