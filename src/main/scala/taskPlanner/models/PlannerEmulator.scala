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
  private var processorsMap: Map[String, ProcessorCore] = null

  def emulateWork(taskQueue: Array[String]): Array[Array[String]] = {
    pendingTasks = getExecutionTasks(graphTask, taskQueue)
    inProgressTasks = new ArrayBuffer[ExecutionTask]()
    completedTasks = new ArrayBuffer[ExecutionTask]()
    processorCores = getProcessorCores(graphSystem)
    processorsMap = processorCores.map(x => (x.id, x)).toMap
    dataBuses = getDataBuses(graphSystem, processorsMap)
    dataBusesFromMap = dataBuses.groupBy(x => x.from.id)
    setLinksForProcessorCores(dataBusesFromMap)

    var iterationCount = 0;
    while (pendingTasks.nonEmpty || inProgressTasks.nonEmpty) {
      prepareStep()

      for (proc <- processorCores) {
        val completedTask = proc.doWork()
        if (completedTask != null) {
          inProgressTasks -= completedTask
          completedTasks += completedTask
        }
      }

      for (proc <- processorCores) {
        proc.doMessageWork()
      }

      onCompletedStep()
      iterationCount += 1
    }

    val result = Array.ofDim[String](iterationCount + 1, processorCores.length)
    for (i <- processorCores.indices) {
      result(0)(i) = processorCores(i).id
    }

    for (i <- 1 until (iterationCount + 1)) {
      for (j <- processorCores.indices) {
        result(i)(j) = s"${processorCores(j).tickExecutionTaskLogs(i - 1)}; ${processorCores(j).tickMessageLogs(i - 1)};"
      }
    }

    result
  }

  def prepareStep(): Unit = {
    val freeProcs = getFreeProcessorCores()
    if (pendingTasks.nonEmpty && freeProcs.nonEmpty) {
      val procQueue = mutable.Queue(freeProcs: _*)
      val tasksToStart = getNextPendingTasks(freeProcs.length)
      for (taskToStart <- tasksToStart) {
        val proc = procQueue.dequeue
        proc.currentTask = taskToStart
        pendingTasks -= taskToStart
        inProgressTasks += taskToStart
        
        if (taskToStart.isHasDependency) {
          taskToStart.state = ExecutionTaskState.WaitingData

          assignMessagesForTask(taskToStart, proc)
        } else {
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
    tasks.forall(x => completedTasks.exists(c => c.id == x))
  }

  private def getFreeProcessorCores(): Array[ProcessorCore] = {
    processorCores.filter(x => x.isFree)
  }

  private def getExecutionTasks(graph: DirectedGraph, taskQueue: Array[String]): ArrayBuffer[ExecutionTask] = {
    val dependenciesTo = graph.edgesToMap

    var tasks = new ArrayBuffer[ExecutionTask]()
    for (i <- taskQueue.indices) {
      val node = graph.nodesMap(taskQueue(i))
      var dependencies = new Array[String](0)
      if (dependenciesTo.contains(node.id)) {
        dependencies = dependenciesTo(node.id).map(d => d.from)
      }

      tasks += ExecutionTask.createFromGraphNode(node, i, dependencies)
    }

    tasks
  }

  private def getDataBuses(graph: UndirectedGraph, processorsMap: Map[String, ProcessorCore]): Array[DataBus] = {
    graph.twoDirectionEdges.map(x => new DataBus(processorsMap(x.from), processorsMap(x.to), x.weight))
  }

  private def getProcessorCores(graph: UndirectedGraph): Array[ProcessorCore] = {
    val sortedByConnections = graph.getSortedByCountOfConnections.reverse

    sortedByConnections.zipWithIndex.map(x => {
      val node = graph.nodesMap(x._1)
      var links = new Array[DataBus](0)

      new ProcessorCore(node.id, node.weight, x._2, new Array[DataBus](0))
    })
  }

  private def setLinksForProcessorCores(dataBusesFromMap: Map[String, Array[DataBus]]): Unit = {
    processorCores.foreach(x => {
      if (dataBusesFromMap.contains(x.id)) {
        x.links = dataBusesFromMap(x.id)
      }
    })
  }

  private def assignMessagesForTask(task: ExecutionTask, targetProc: ProcessorCore): Unit = {
    if (targetProc.hasDataForAllTasks(task.dependencies)) {
      task.state = ExecutionTaskState.ReadyForExecution
    } else {
      for (dependencyId <- task.dependencies) {
        if (!targetProc.hasDataFromTask(dependencyId)) {
          val sourceProc = processorCores.find(x => x.completedTasks.exists(x => x.id == dependencyId)).get
          val procsWithNotFreeLinks = processorCores.filter(x => !x.isLinksFree).map(x => x.id)

          var routeForMessage: Array[String] = null
          if (procsWithNotFreeLinks.length == processorCores.length) {
            routeForMessage = graphSystem.getTheShortestRoute(sourceProc.id, targetProc.id, new Array[String](0))
          } else {
            routeForMessage = graphSystem.getTheShortestRoute(sourceProc.id, targetProc.id, procsWithNotFreeLinks)
          }

          val firstProcId = processorCores.find(x => x.id == routeForMessage(1)).get.id
          val dataVolume = graphTask.edgesFromToMap((dependencyId, task.id)).weight
          val nextSteps = routeForMessage.drop(2)
          val message = new Message(dependencyId, task.id, firstProcId, dataVolume, nextSteps)

          sourceProc.addMessageToQueue(message)
        }
      }
    }
  }
}
