package taskPlanner

import taskPlanner.models.{DirectedGraph, UndirectedGraph}
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModel.graphSystem)
    val sortedTasks = graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution

    graphSystem.getSortedByCountOfConnections.reverse.foreach(x => print(s"${x} "))
    println
  }
}
