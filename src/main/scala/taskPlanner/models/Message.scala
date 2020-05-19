package taskPlanner.models

class Message(val fromTask: String, val toTask: String, val fromProc: String, val volume: Int, val route: Array[String]) {
  private var _progress: Int = 0
}
