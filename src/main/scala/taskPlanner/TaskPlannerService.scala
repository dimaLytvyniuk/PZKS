package taskPlanner

import taskPlanner.models.DirectedGraph
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val sortedTasks = graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution

    sortedTasks.foreach(x => print(s"${x} "))
    println
  }
}
