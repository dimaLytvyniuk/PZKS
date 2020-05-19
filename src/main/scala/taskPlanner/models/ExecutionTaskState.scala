package taskPlanner.models

object ExecutionTaskState extends Enumeration {
  type OperationTypes = Value
  val Pending, WaitingData, ReadyForExecution, Executing, Completed = Value
}
