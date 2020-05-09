package taskPlanner

import taskPlanner.models.{DirectedGraph, PlannerEmulator, UndirectedGraph}
import taskPlanner.views.PlanTasksViewModel

class TaskPlannerService {
  def planSecondLab(planTasksViewModel: PlanTasksViewModel): Unit = {
    val graphTask = DirectedGraph.createFromViewModel(planTasksViewModel.graphTask)
    val graphSystem = UndirectedGraph.createFromViewModel(planTasksViewModel.graphSystem)
    val emulator = new PlannerEmulator(graphTask, graphSystem)

    emulator.emulateWork
  }
}
