package taskPlanner.models

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ProcessorCore(val id: String, val weight: Int, val priority: Int, val links: Array[DataBus]) {
  private var _currentTask: ExecutionTask = null
  private var _currentMessage: Message = null
  private val _tickExecutionTaskLogs = new ArrayBuffer[String]
  private val _tickMessageLogs = new ArrayBuffer[String]
  private var _coreState: ProcessorCoreState.Value = ProcessorCoreState.Free
  private var _messageQueue = new mutable.Queue[Message]()

  private var _completedTasks = new ArrayBuffer[ExecutionTask]()
  private var _sendedMessages = new ArrayBuffer[Message]()

  def currentTask = _currentTask

  def currentTask_=(task: ExecutionTask) {
    _currentTask = task
  }

  def coreState = _coreState

  def isFree: Boolean = _coreState == ProcessorCoreState.Free

  def isLinksFree: Boolean = _messageQueue.isEmpty

  def completedTasks: Array[ExecutionTask] = _completedTasks.toArray

  def doWork(): ExecutionTask = {
    val completedTask = executionTaskWork()

    messageQueueWork()

    completedTask
  }

  def receiveMessage(message: Message): Unit = {
    if (message.route.length == 0) {
      _currentTask.state = ExecutionTaskState.ReadyForExecution
    } else {
      val targetProc = message.route(0)
      val newRoute = message.route.drop(1)
      val newMessage = new Message(message.fromTask, message.toTask, targetProc, message.volume, newRoute)
      addMessageToQueue(newMessage)
    }
  }

  def addMessageToQueue(message: Message): Unit = {
    _messageQueue += message
  }

  private def executionTaskWork(): ExecutionTask = {
    if (_currentTask != null && !_currentTask.isWaitingData) {
      var completedTask: ExecutionTask = null

      if (_currentTask.isReadyForExecution) {
        _currentTask.state = ExecutionTaskState.Executing
      }

      val workCapacity = if (_currentTask.nonCompletedWorkCapacity >= weight) weight else _currentTask.nonCompletedWorkCapacity
      _currentTask.updateProgress(workCapacity)
      _tickExecutionTaskLogs += s"${_currentTask.id} (${workCapacity})"

      if (_currentTask.nonCompletedWorkCapacity == 0) {
        _currentTask.state = ExecutionTaskState.Completed
        _completedTasks += _currentTask
        completedTask = _currentTask
        _currentTask = null
      }

      completedTask
    } else {
      _tickExecutionTaskLogs += ""

      null
    }
  }

  private def messageQueueWork(): Unit = {
    if (_currentMessage == null && _messageQueue.nonEmpty) {
      _currentMessage = _messageQueue.dequeue()
    }

    if (_currentMessage != null) {
      val link = links.find(x => x.to.id == _currentMessage.toProc).get

      val workCapacity = if (_currentMessage.nonCompletedWorkCapacity >= link.capacity) link.capacity else _currentMessage.nonCompletedWorkCapacity
      _currentMessage.updateProgress(workCapacity)
      _tickMessageLogs += s"${_currentMessage.fromTask}-${_currentMessage.toTask} (${_currentMessage.toProc}): ${workCapacity}"

      if (_currentMessage.nonCompletedWorkCapacity == 0) {
        link.to.receiveMessage(_currentMessage)

        _currentMessage = null
      }
    } else {
      _tickMessageLogs += ""
    }
  }
}
