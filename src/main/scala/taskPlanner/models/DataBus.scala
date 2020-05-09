package taskPlanner.models

class DataBus(val from: String, val to: String, val capacity: Int) {

}

object DataBus {
  def createFromGraphEdge(edge: GraphEdge): DataBus = {
    if (edge == null) {
      null
    } else {
      new DataBus(edge.from, edge.to, edge.weight)
    }
  }
}