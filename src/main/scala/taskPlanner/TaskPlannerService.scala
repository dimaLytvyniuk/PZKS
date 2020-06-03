package taskPlanner

import taskPlanner.models.{DirectedGraph, ExecutionTask, PlannerEmulator, QueueAlgorithmNames, UndirectedGraph}
import taskPlanner.views.{CombinedStatisticViewModel, GraphViewModel, PlanTasksViewModel, StatisticViewModel}

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

  def rgrAnalyze(planTasksViewModel: Array[PlanTasksViewModel]): Array[CombinedStatisticViewModel] = {
    println(planTasksViewModel(1).graphSystem.nodes(0).id)

    val firstStatisticViewModel = StatisticViewModel(QueueAlgorithmNames.ByDiffBetweenLastAndEarlyExecution, 1, 2, 3, 4.5, 6.6, 6.7)
    val secondStatisticViewModel = StatisticViewModel(QueueAlgorithmNames.ByNodeWeight, 1, 2, 3, 4.5, 6.6, 6.7)
    val combinedStatisticViewModel = CombinedStatisticViewModel(firstStatisticViewModel, secondStatisticViewModel)

    Array(combinedStatisticViewModel)
  }
}
