package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

class PlannerEmulator(val graphTask: DirectedGraph, val graphSystem: UndirectedGraph) {
  private var pendingTasks: ArrayBuffer[ExecutionTask] = null
  private var inProgressTasks: ArrayBuffer[ExecutionTask] = null
  private var completedTasks: ArrayBuffer[ExecutionTask] = null
  private var dataBuses: Array[DataBus] = null
  private var dataBusesFromMap: Map[String, Array[DataBus]] = null
  private var processorCores: Array[ProcessorCore] = null

  def emulateWork(): Unit = {
    pendingTasks = getExecutionTasks(graphTask)
    inProgressTasks = new ArrayBuffer[ExecutionTask]()
    completedTasks = new ArrayBuffer[ExecutionTask]()
    dataBuses = getDataBuses(graphSystem)
    dataBusesFromMap = dataBuses.groupBy(x => x.from)
    processorCores = getProcessorCores(graphSystem, dataBusesFromMap)

    while (pendingTasks.nonEmpty || inProgressTasks.nonEmpty) {

    }
  }

  def doTick(): Unit = {
    for (processorCore <- processorCores) {
      if (processorCore.isFree) {

      }
    }
  }

  private def getExecutionTasks(graph: DirectedGraph): ArrayBuffer[ExecutionTask] = {
    val dependenciesTo = graph.edgesToMap
    val sortedByPriority = graph.getSortedNodesByDiffBetweenLastAndEarlyExecution

    var tasks = new ArrayBuffer[ExecutionTask]()
    for (i <- sortedByPriority.indices) {
      val node = graph.nodesMap(sortedByPriority(i))
      var dependencies = new Array[String](0)
      if (dependenciesTo.contains(node.id)) {
        dependencies = dependenciesTo(node.id).map(d => d.from)
      }

      tasks += ExecutionTask.createFromGraphNode(node, i, dependencies)
    }

    tasks
  }

  private def getDataBuses(graph: UndirectedGraph): Array[DataBus] = {
    graph.twoDirectionEdges.map(x => DataBus.createFromGraphEdge(x))
  }

  private def getProcessorCores(graph: UndirectedGraph, dataBusesFromMap: Map[String, Array[DataBus]]): Array[ProcessorCore] = {
    val sortedByConnections = graph.getSortedByCountOfConnections.reverse

    sortedByConnections.zipWithIndex.map(x => {
      val node = graph.nodesMap(x._1)
      var links = new Array[DataBus](0)
      if (dataBusesFromMap.contains(node.id)) {
        links = dataBusesFromMap(node.id)
      }

      new ProcessorCore(node.id, node.weight, x._2, links)
    })
  }
}
