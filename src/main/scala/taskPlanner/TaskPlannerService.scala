package taskPlanner

import taskPlanner.models.{DirectedGraph, ExecutionTask, PlannerEmulator, QueueAlgorithmNames, UndirectedGraph, StatisticModel}
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

    val taskQueue = graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution
    val taskQueue1 = graphTask.getSortedNodesByWeight

    emulator.emulateWork(taskQueue)

    emulator.emulateWork(taskQueue1)
  }

  def rgrAnalyze(planTasksViewModels: Array[PlanTasksViewModel]): Array[CombinedStatisticViewModel] = {
    var statisticViewModels = new Array[CombinedStatisticViewModel](planTasksViewModels.length)

    for (i <- planTasksViewModels.indices) {
      val graphTask = DirectedGraph.createFromViewModel(planTasksViewModels(i).graphTask)
      val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModels(i).graphSystem)
      val emulator = new PlannerEmulator(graphTask, graphSystem)

      statisticViewModels(i) = calculateStatistic(emulator)
    }

    statisticViewModels
  }

  def calculateStatistic(emulator: PlannerEmulator): CombinedStatisticViewModel = {
    val criticalTime = emulator.graphTask.criticalLen
    val onOneCoreTime = emulator.graphTask.totalWeight

    val sortByDiffQueue = emulator.graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution
    val sortByWeightQueue = emulator.graphTask.getSortedNodesByWeight

    val sortByDiffTime = emulator.emulateWork(sortByDiffQueue).length - 1
    val sortByWeightTime = emulator.emulateWork(sortByWeightQueue).length - 1
    val processorCount = emulator.graphSystem.nodes.length

    val firstStatisticViewModel = new StatisticModel(QueueAlgorithmNames.ByDiffBetweenLastAndEarlyExecution, processorCount, onOneCoreTime, criticalTime, sortByDiffTime)
    val secondStatisticViewModel = new StatisticModel(QueueAlgorithmNames.ByNodeWeight, processorCount, onOneCoreTime, criticalTime, sortByWeightTime)
    val combinedStatisticViewModel = TaskPlannerMapper.createCombinedStatisticViewModel(firstStatisticViewModel, secondStatisticViewModel)

    combinedStatisticViewModel
  }
}
