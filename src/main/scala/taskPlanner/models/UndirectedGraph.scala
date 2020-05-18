package taskPlanner.models

import taskPlanner.views.GraphViewModel

import scala.collection.mutable
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

  def getTheShortestRoute(from: String, to: String, excludeNodes: Array[String]): Array[String] = {
    val (routesLenMap, routesNodesMap) = getNodeShortRoutesMap(from, excludeNodes)
    val targetRoutes = routesLenMap(to)
    var minLen = Int.MaxValue
    var minIndex = 0

    for (i <- targetRoutes.indices) {
      if (minLen > targetRoutes(i)) {
        minLen = targetRoutes(i)
        minIndex = i
      }
    }

    if (minLen == Int.MaxValue) {
      if (excludeNodes.isEmpty) {
        new Array[String](0)
      } else {
        getTheShortestRoute(from, to, new Array[String](0))
      }
    } else {
      getRouteFromMap(routesNodesMap, to, minIndex).reverse.toArray
    }
  }

  def getNodeShortRoutesMap(sourceId: String, excludeNodes: Array[String]): (mutable.HashMap[String, Array[Int]], mutable.HashMap[String, Array[String]]) = {
    val routesLenMap = getRoutesLenMap
    val routesNodesMap = getRoutesNodesMap
    routesLenMap(sourceId)(0) = 0

    for (i <- 1 until _nodes.length) {
      for (edge <- twoDirectionEdges) {
        if (excludeNodes.isEmpty || !excludeNodes.contains(edge.to)) {
          val newWeight = routesLenMap(edge.from)(i - 1) + edge.weight
          if (routesLenMap(edge.from)(i - 1) != Int.MaxValue && routesLenMap(edge.to)(i) > newWeight) {
            routesLenMap(edge.to)(i) = newWeight
            routesNodesMap(edge.to)(i) = edge.from
          }
        }
      }
    }

    (routesLenMap, routesNodesMap)
  }

  private def getRoutesLenMap: mutable.HashMap[String, Array[Int]] = {
    val routesMap = new mutable.HashMap[String, Array[Int]]()
    for (node <- _nodes) {
      routesMap(node.id) = new Array[Int](_nodes.length)
      for (i <- 0 until _nodes.length) {
        routesMap(node.id)(i) = Int.MaxValue
      }
    }

    routesMap
  }

  private def getRoutesNodesMap: mutable.HashMap[String, Array[String]] = {
    val routesMap = new mutable.HashMap[String, Array[String]]()
    for (node <- _nodes) {
      routesMap(node.id) = new Array[String](_nodes.length)
    }

    routesMap
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
