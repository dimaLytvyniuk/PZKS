package taskPlanner

import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    println(planTasksViewModel.graphSystem.edges(0).from)
  }
}
