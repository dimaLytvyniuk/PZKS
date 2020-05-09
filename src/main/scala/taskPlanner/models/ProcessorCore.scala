package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

class ProcessorCore(val id: String, val weight: Int, val priority: Int, val links: Array[DataBus]) {
  private var _currentTask: ExecutionTask = null
  private val _tickLogs = new ArrayBuffer[String]
  private val _coreState: ProcessorCoreState.Value = ProcessorCoreState.Free

  def currentTask = _currentTask

  def currentTask_=(task: ExecutionTask) {
    _currentTask = task
  }

  def coreState = _coreState
}
