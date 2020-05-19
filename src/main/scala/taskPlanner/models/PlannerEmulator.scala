package taskPlanner.models

import scala.collection.immutable.Queue
import scala.collection.mutable
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
      prepareStep()

      processorCores.foreach(x => x.doWork())

      onCompletedStep()
    }
  }

  def prepareStep(): Unit = {
    val freeProcs = getFreeProcessorCores()
    if (pendingTasks.nonEmpty && freeProcs.nonEmpty) {
      val procQueue = mutable.Queue(freeProcs: _*)
      val tasksToStart = getNextPendingTasks(freeProcs.length)
      for (taskToStart <- tasksToStart) {
        val proc = procQueue.dequeue
        proc.currentTask = taskToStart

        if (taskToStart.isHasDependency) {
          taskToStart.state = ExecutionTaskState.WaitingData

          assignMessagesForTask(taskToStart, proc.id)
        } else {
          inProgressTasks += taskToStart
          pendingTasks -= taskToStart

          taskToStart.state = ExecutionTaskState.ReadyForExecution
        }
      }
    }
  }

  def onCompletedStep(): Unit = {

  }

  private def getNextPendingTasks(maxCount: Int): Array[ExecutionTask] = {
    pendingTasks.filter(x => isCompletedTasks(x.dependencies)).take(maxCount).toArray
  }

  private def isCompletedTasks(tasks: Array[String]): Boolean = {
    tasks.forall(x => completedTasks.contains(x))
  }

  private def getFreeProcessorCores(): Array[ProcessorCore] = {
    processorCores.filter(x => x.isFree)
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

  private def assignMessagesForTask(task: ExecutionTask, targetProcId: String): Unit = {
    for (dependencyId <- task.dependencies) {
      val sourceProcId = processorCores.find(x => x.completedTasks.exists(x => x.id == dependencyId)).get.id
      val procsWithNotFreeLinks = processorCores.filter(x => !x.isLinksFree).map(x => x.id)

      var routeForMessage: Array[String] = null
      if (procsWithNotFreeLinks.length == processorCores.length) {
        routeForMessage = graphSystem.getTheShortestRoute(sourceProcId, targetProcId, new Array[String](0))
      } else {
        routeForMessage = graphSystem.getTheShortestRoute(sourceProcId, targetProcId, procsWithNotFreeLinks)
      }

      val firstProc = processorCores.find(x => x.id == routeForMessage(1)).get
      val dataVolume = graphTask.edgesFromToMap((dependencyId, task.id)).weight
      val nextSteps = routeForMessage.drop(2)
      val message = new Message(dependencyId, task.id, sourceProcId, dataVolume, nextSteps)

      firstProc.addMessageToQueue(message)
    }
  }
}
