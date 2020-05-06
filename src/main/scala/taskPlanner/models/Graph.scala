package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

abstract class Graph(protected var _nodes: Array[GraphNode], protected var _edges: Array[GraphEdge]) {
  protected val nodesMap: Map[String, GraphNode] = _nodes.map(x => (x.id, x)).toMap
  protected val edgesFromMap: Map[String, Array[GraphEdge]] = _edges.groupBy(x => x.from)
  protected val edgesToMap: Map[String, Array[GraphEdge]] = _edges.groupBy(x => x.to)
  protected val edgesFromToMap: Map[(String, String), GraphEdge] = _edges.map(x => ((x.from, x.to), x)).toMap

  def getCriticalRoute: ArrayBuffer[String]
}
