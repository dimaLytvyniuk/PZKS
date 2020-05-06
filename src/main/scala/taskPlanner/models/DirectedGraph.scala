package taskPlanner.models

import taskPlanner.views.GraphViewModel

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class DirectedGraph(_nodes: Array[GraphNode], _edges: Array[GraphEdge]) extends Graph(_nodes, _edges) {
  val nodesRoutesMap = getNodesRoutesMap()
  val criticalRoute = getCriticalRoute
  val criticalLen = getRouteLen(criticalRoute)
  val criticalRoutesFromNodes = getCriticalRoutesFromNodes
  val criticalRoutesToNodes = getCriticalRoutesToNodes

  def getCriticalRoute: ArrayBuffer[String] = {
    val criticalPathes = getCriticalRoutesFromNodes
    var maxLen = 0
    var maxNodeId = _nodes(0).id

    for ((targetNodeId, criticalPath) <- criticalPathes) {
      var len = getRouteLen(criticalPath)

      if (len > maxLen) {
        maxLen = len
        maxNodeId = targetNodeId
      }
    }

    criticalPathes(maxNodeId)
  }

  def getCriticalRoutesFromNodes: mutable.HashMap[String, ArrayBuffer[String]] = {
    val criticalPathes = new mutable.HashMap[String, ArrayBuffer[String]]

    for (node <- _nodes) {
      criticalPathes(node.id) = getCriticalRouteFromNode(node.id)
    }

    criticalPathes
  }

  def getCriticalRouteFromNode(nodeId: String): ArrayBuffer[String] = {
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
      val criticalRoute = getRouteFromMap(sourceRoutes._2, maxTargetNodeId, maxCountEdges)

      criticalRoute.reverse
    }
  }

  def getCriticalRoutesToNodes: mutable.HashMap[String, ArrayBuffer[String]] = {
    val criticalPathes = new mutable.HashMap[String, ArrayBuffer[String]]

    for (node <- _nodes) {
      criticalPathes(node.id) = getCriticalRouteToNode(node.id)
    }

    criticalPathes
  }

  def getCriticalRouteToNode(targetNodeId: String): ArrayBuffer[String] = {
    var maxSourceNodeId = targetNodeId
    var maxCountEdges = 0
    var maxLen = 0

    for ((sourceNodeId, sourceRoutes) <- nodesRoutesMap) {
      val targetRoutes = sourceRoutes._1(targetNodeId)
      for (i <- targetRoutes.indices) {
        if (maxLen < targetRoutes(i)) {
          maxLen = targetRoutes(i)
          maxCountEdges = i
          maxSourceNodeId = sourceNodeId
        }
      }
    }

    if (maxSourceNodeId == targetNodeId) {
      new ArrayBuffer[String]()
    } else {
      val routes = nodesRoutesMap(maxSourceNodeId)._2
      val criticalRoute = getRouteFromMap(routes, targetNodeId, maxCountEdges)

      criticalRoute.reverse
    }
  }

  def getRouteLen(route: ArrayBuffer[String]): Int = {
    var len = 0
    for (nodeId <- route) {
      len += nodesMap(nodeId).weight
    }

    len
  }

  def getCriticalLenToStartForNode(nodeId: String): Int = {
    val criticalRouteToNode = criticalRoutesToNodes(nodeId)

    var sum = 0
    if (criticalRouteToNode.nonEmpty) {
      criticalRouteToNode.init.foreach(x => sum += nodesMap(x).weight)
    }

    sum
  }

  def getCriticalLenToEndForNode(nodeId: String): Int = {
    val criticalRouteFromNode = criticalRoutesFromNodes(nodeId)

    var sum = 0
    if (criticalRouteFromNode.nonEmpty) {
      criticalRouteFromNode.foreach(x => sum += nodesMap(x).weight)
    } else {
      sum = nodesMap(nodeId).weight
    }

    sum
  }

  def getEarlyNodeExecutionTime(nodeId: String): Int = {
    getCriticalLenToStartForNode(nodeId) + 1
  }

  def getLateNodeExecutionTime(nodeId: String): Int = {
    criticalLen - getCriticalLenToEndForNode(nodeId) + 1
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

  def getSortedNodesByDiffBetweenLastAndEarlyExecution: Array[String] = {
    var diffExecutionTimes = new Array[(String, Int)](_nodes.length)

    for (i <- _nodes.indices) {
      val earlyExecutionTime = getEarlyNodeExecutionTime(_nodes(i).id)
      val lateExecutionTime = getLateNodeExecutionTime(_nodes(i).id)
      val diff = lateExecutionTime - earlyExecutionTime

      println(s"${_nodes(i).id}, ${earlyExecutionTime} ${lateExecutionTime}")

      diffExecutionTimes(i) = (_nodes(i).id, diff)
    }

    diffExecutionTimes.sortBy(x => x._2).map(x => x._1)
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
    routeNodes += currentNodeId

    while (currentEdgeCount > 0) {
      var nodeId = routes(currentNodeId)(currentEdgeCount)
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
