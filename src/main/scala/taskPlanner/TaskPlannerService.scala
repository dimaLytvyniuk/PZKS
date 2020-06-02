package taskPlanner

import taskPlanner.models.{DirectedGraph, ExecutionTask, PlannerEmulator, UndirectedGraph}
import taskPlanner.views.{GraphViewModel, PlanTasksViewModel}

import scala.collection.mutable.ArrayBuffer

class TaskPlannerService {
  def getSecondLabQueue(graphTaskViewModel: GraphViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(graphTaskViewModel)

    graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution
  }

  def getThirdLabQueue(graphTaskViewModel: GraphViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(graphTaskViewModel)

    graphTask.getSortedNodesByWeight
  }

  def planSixthLab(planTasksViewModel: PlanTasksViewModel): Array[Array[String]] = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModel.graphSystem)
    val emulator = new PlannerEmulator(graphTask, graphSystem)

    emulator.emulateWork()
  }

  def rgrAnalyze(planTasksViewModel: Array[PlanTasksViewModel]): Unit = {
    println(planTasksViewModel(1).graphSystem.nodes(0).id)
  }
}
