package taskPlanner.models

object ProcessorCoreState extends Enumeration {
  type OperationTypes = Value
  val Free, Busy = Value
}
