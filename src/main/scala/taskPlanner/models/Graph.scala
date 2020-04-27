package taskPlanner.models

abstract class Graph(protected var _nodes: Array[GraphNode], protected var _edges: Array[GraphEdge]) {
  def getCriticalRoutes: Array[Array[Int]]
}
