package taskPlanner

import taskPlanner.models.DirectedGraph
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    var len = graphTask.getEarlyNodeExecutionTime("7")
    var len1 = graphTask.getEarlyNodeExecutionTime("1")
    var len2 = graphTask.getEarlyNodeExecutionTime("8")
    println(len)
    println(len1)
    println(len2)

    len = graphTask.getLateNodeExecutionTime("7")
    len1 = graphTask.getLateNodeExecutionTime("1")
    len2 = graphTask.getLateNodeExecutionTime("8")
    println(len)
    println(len1)
    println(len2)
  }
}
