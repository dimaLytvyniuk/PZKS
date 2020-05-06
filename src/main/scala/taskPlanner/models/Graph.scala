package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

abstract class Graph(protected var _nodes: Array[GraphNode], protected var _edges: Array[GraphEdge]) {
  protected val nodesMap: Map[String, GraphNode] = _nodes.map(x => (x.id, x)).toMap
  protected val edgesFromMap: Map[String, GraphEdge] = _edges.map(x => (x.from, x)).toMap
  protected val edgesToMap: Map[String, GraphEdge] = _edges.map(x => (x.to, x)).toMap
  protected val edgesFromToMap: Map[(String, String), GraphEdge] = _edges.map(x => ((x.from, x.to), x)).toMap

  def getCriticalRoute: ArrayBuffer[String]
}