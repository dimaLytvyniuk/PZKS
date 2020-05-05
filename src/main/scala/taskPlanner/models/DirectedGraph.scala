package taskPlanner.models

import taskPlanner.views.GraphViewModel
import scala.collection.mutable

class DirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  val nodesRoutesMap = getNodesRoutesMap()

  def getCriticalRoutes: Array[String] = {
    new Array[String](0)
  }

  def getNodesRoutesMap(): mutable.HashMap[String, (mutable.HashMap[String, Array[Int]], mutable.HashMap[String, Array[String]])] = {
    val nodesRoutes = new mutable.HashMap[String, (mutable.HashMap[String, Array[Int]], mutable.HashMap[String, Array[String]])]()
    for (node <- _nodes) {
      nodesRoutes(node.id) = getNodeRoutesMap(node)
    }

    nodesRoutes
  }

  def getNodeRoutesMap(node: GraphNode): (mutable.HashMap[String, Array[Int]], mutable.HashMap[String, Array[String]]) = {
    val routesLenMap = getRoutesLenMap
    val routesNodesMap = getRoutesNodesMap
    routesLenMap(node.id)(0) = node.weight

    for (i <- 1 until _nodes.length - 1) {
      for (edge <- _edges) {
        val newWeight = routesLenMap(edge.from)(i - 1) + nodesMap(edge.to).weight
        if (routesLenMap(edge.to)(i) < newWeight) {
          routesLenMap(edge.to)(i) = newWeight
          routesNodesMap(edge.to)(i) = edge.from
        }
      }
    }

    (routesLenMap, routesNodesMap)
  }

  private def getRoutesLenMap: mutable.HashMap[String, Array[Int]] = {
    val routesMap = new mutable.HashMap[String, Array[Int]]()
    for (node <- _nodes) {
      routesMap(node.id) = new Array[Int](_nodes.length -1)
      for (i <- 0 until _nodes.length - 1) {
        routesMap(node.id)(i) = Int.MinValue
      }
    }

    routesMap
  }

  private def getRoutesNodesMap: mutable.HashMap[String, Array[String]] = {
    val routesMap = new mutable.HashMap[String, Array[String]]()
    for (node <- _nodes) {
      routesMap(node.id) = new Array[String](_nodes.length - 1)
    }

    routesMap
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
