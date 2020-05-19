package taskPlanner.models

class Message(val fromTask: String, val toTask: String, val toProc: String, val volume: Int, val route: Array[String]) {
  private var _progress: Int = 0

  def nonCompletedWorkCapacity = volume - _progress

  def updateProgress(completedWork: Int): Unit = _progress = _progress + completedWork
}
