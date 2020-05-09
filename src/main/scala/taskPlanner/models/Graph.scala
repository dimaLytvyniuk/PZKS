package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

abstract class Graph(protected var _nodes: Array[GraphNode], protected var _edges: Array[GraphEdge]) {
  protected val _nodesMap: Map[String, GraphNode] = _nodes.map(x => (x.id, x)).toMap
  protected val _edgesFromMap: Map[String, Array[GraphEdge]] = _edges.groupBy(x => x.from)
  protected val _edgesToMap: Map[String, Array[GraphEdge]] = _edges.groupBy(x => x.to)
  protected val _edgesFromToMap: Map[(String, String), GraphEdge] = _edges.map(x => ((x.from, x.to), x)).toMap

  def getCriticalRoute: ArrayBuffer[String]

  def nodes: Array[GraphNode] = _nodes

  def edges: Array[GraphEdge] = _edges

  def nodesMap = _nodesMap

  def edgesToMap = _edgesToMap

  def edgesFromMap = _edgesFromMap

  def edgesFromToMap = _edgesFromToMap
}
