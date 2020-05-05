package taskPlanner.models

import taskPlanner.views.GraphViewModel

import scala.collection.mutable.ArrayBuffer

class UndirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  def getCriticalRoute: ArrayBuffer[String] = {
    new ArrayBuffer[String]()
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
