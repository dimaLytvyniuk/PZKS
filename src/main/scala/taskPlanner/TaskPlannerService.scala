package taskPlanner

import taskPlanner.models.{DirectedGraph, PlannerEmulator, UndirectedGraph}
import taskPlanner.views.{GraphViewModel, PlanTasksViewModel}

class TaskPlannerService {
  def getSecondLabQueue(graphTaskViewModel: GraphViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(graphTaskViewModel)

    graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution
  }

  def getThirdLabQueue(graphTaskViewModel: GraphViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(graphTaskViewModel)

    graphTask.getSortedNodesByWeight
  }

  def planSixthLab(planTasksViewModel: PlanTasksViewModel): Array[String] = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModel.graphSystem)

    val route = graphSystem.getTheShortestRoute("3", "8")
    route
  }
}
