package taskPlanner.models

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ProcessorCore(val id: String, val weight: Int, val priority: Int, val links: Array[DataBus]) {
  private var _currentTask: ExecutionTask = null
  private val _tickLogs = new ArrayBuffer[String]
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

  def doWork(): Unit = {
    sendMessage()
  }

  def addMessageToQueue(message: Message): Unit = {
    _messageQueue += message
  }

  private def sendMessage(): Unit = {

  }
}
