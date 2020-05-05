package taskPlanner

import taskPlanner.models.DirectedGraph
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val criticalPath = graphTask.getCriticalRoute

    for (route <- criticalPath) {
      print(s"${route} ")
    }
    println()

    println(planTasksViewModel.graphSystem.edges(0).from)
  }
}
