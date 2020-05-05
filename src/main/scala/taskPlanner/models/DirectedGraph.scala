package taskPlanner.models

import taskPlanner.views.GraphViewModel

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class DirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  val nodesRoutesMap = getNodesRoutesMap()

  def getCriticalRoute: ArrayBuffer[String] = {
    val criticalPathes = getCriticalPathes
    var maxLen = 0
    var maxNodeId = _nodes(0).id

    for ((targetNodeId, criticalPath) <- criticalPathes) {
      var len = 0
      for (nodeId <- criticalPath) {
        len += nodesMap(nodeId).weight
      }

      if (len > maxLen) {
        maxLen = len
        maxNodeId = targetNodeId
      }
    }

    criticalPathes(maxNodeId)
  }

  def getCriticalPathes: mutable.HashMap[String, ArrayBuffer[String]] = {
    val criticalPathes = new mutable.HashMap[String, ArrayBuffer[String]]

    for (node <- _nodes) {
      criticalPathes(node.id) = getCriticalPath(node.id)
    }

    criticalPathes
  }

  def getCriticalPath(nodeId: String): ArrayBuffer[String] = {
    val sourceRoutes = nodesRoutesMap(nodeId)
    var maxTargetNodeId = nodeId
    var maxCountEdges = 0
    var maxLen = 0

    for ((targetNodeId, routes)<- sourceRoutes._1) {
      for (i <- routes.indices) {
        if (maxLen < routes(i)) {
          maxTargetNodeId = targetNodeId
          maxCountEdges = i
          maxLen = routes(i)
        }
      }
    }

    if (maxTargetNodeId == nodeId) {
      new ArrayBuffer[String]()
    } else {
      var criticalRoute = getRouteFromMap(sourceRoutes._2, maxTargetNodeId, maxCountEdges)
      criticalRoute += nodeId

      criticalRoute.reverse
    }
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

    for (i <- 1 until _nodes.length) {
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
      routesMap(node.id) = new Array[Int](_nodes.length)
      for (i <- 0 until _nodes.length) {
        routesMap(node.id)(i) = Int.MinValue
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

  private def getRouteFromMap(routes: mutable.HashMap[String, Array[String]], lastNodeId: String, edgeCount: Int): ArrayBuffer[String] = {
    val routeNodes = new ArrayBuffer[String]()
    var currentNodeId = lastNodeId
    var currentEdgeCount = edgeCount

    while (currentEdgeCount > 0) {
      var nodeId = routes(lastNodeId)(edgeCount)
      routeNodes += nodeId
      currentNodeId = nodeId
      currentEdgeCount -= 1
    }

    routeNodes
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
