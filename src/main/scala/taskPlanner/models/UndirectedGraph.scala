package taskPlanner.models

import taskPlanner.views.GraphViewModel

import scala.collection.mutable.ArrayBuffer

class UndirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  val twoDirectionEdges = getTwoDirectionEdges
  val twoDirectionEdgesFromMap: Map[String, Array[GraphEdge]] = twoDirectionEdges.groupBy(x => x.from)
  val twoDirectionEdgesToMap: Map[String, Array[GraphEdge]] = twoDirectionEdges.groupBy(x => x.to)

  def getCriticalRoute: ArrayBuffer[String] = {
    new ArrayBuffer[String]()
  }

  def getTwoDirectionEdges: Array[GraphEdge] = {
    val twoDirectionEdges = new Array[GraphEdge](_edges.length * 2)

    for (i <- _edges.indices) {
      twoDirectionEdges(2 * i) = _edges(i)
      twoDirectionEdges(2 * i + 1) = _edges(i).getWithReversedDirection
    }

    twoDirectionEdges
  }

  def getSortedByCountOfConnections: Array[String] = {
    twoDirectionEdgesFromMap
      .map(x => (x._1, x._2.length))
      .toSeq
      .sortBy(x => x._2)
      .map(x => x._1)
      .toArray
  }
}

object UndirectedGraph {
  def createFromViewModel(viewModel: GraphViewModel): UndirectedGraph = {
    if (viewModel == null) {
      null
    } else {
      val nodes = viewModel.nodes.map(x => GraphNode.createFromViewModel(x))
      val edges = viewModel.edges.map(x => GraphEdge.createFromViewModel(x))

      new UndirectedGraph(nodes, edges)
    }
  }
}
