package taskPlanner.models

import taskPlanner.views.GraphViewModel

class DirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  def getCriticalRoutes: Array[Array[Int]] = {
    val reversedNodes = _nodes.map(x => x.createWithOppositeWeight)

    Array.ofDim[Int](_nodes.length,_nodes.length)
  }
}

object DirectedGraph {
  def createFromViewModel(viewModel: GraphViewModel): DirectedGraph = {
    if (viewModel == null) {
      null
    } else {
      val nodes = viewModel.nodes.map(x => GraphNode.createFromViewModel(x))
      val edges = viewModel.edges.map(x => GraphEdge.createFromViewModel(x))

      new DirectedGraph(nodes, edges)
    }
  }
}
