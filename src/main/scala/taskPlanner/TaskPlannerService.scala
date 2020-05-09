package taskPlanner

import taskPlanner.models.{DirectedGraph, PlannerEmulator, UndirectedGraph}
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModel.graphSystem)

    graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution
  }
}
