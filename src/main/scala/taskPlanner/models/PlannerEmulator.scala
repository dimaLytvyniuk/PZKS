package taskPlanner.models

import scala.collection.mutable.ArrayBuffer

class PlannerEmulator(val graphTask: DirectedGraph, val graphSystem: UndirectedGraph) {
  def emulateWork: Unit = {
    val tasks = getExecutionTasks(graphTask)
  }

  private def getExecutionTasks(graphTask: DirectedGraph): ArrayBuffer[ExecutionTask] = {
    val dependenciesTo = graphTask.edgesToMap
    val sortedByPriority = graphTask.getSortedNodesByDiffBetweenLastAndEarlyExecution

    var tasks = new ArrayBuffer[ExecutionTask]()
    for (i <- sortedByPriority.indices) {
      val node = graphTask.nodesMap(sortedByPriority(i))
      var dependencies = new Array[String](0)
      if (dependenciesTo.contains(node.id)) {
        dependencies = dependenciesTo(node.id).map(d => d.from)
      }

      tasks += ExecutionTask.createFromGraphNode(node, i, dependencies)
    }

    tasks
  }
}
