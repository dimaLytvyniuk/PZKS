package taskPlanner.models

class Message(val fromTask: Int, val toTask: Int, val fromProc: Int, val toProc: Int, val volume: Int) {
  private var _progress: Int = 0
}
