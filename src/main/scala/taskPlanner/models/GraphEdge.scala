package taskPlanner.models

import taskPlanner.views.EdgeViewModel

class GraphEdge(private var _from: String, private var _to: String, private var _weight: Option[Int]) {
  def from: String = _from

  def to: String = _to

  def weight: Int = _weight.getOrElse(0)

  def getWithOppositeWeight: GraphEdge = {
    new GraphEdge(from, to, Some(weight * (-1)))
  }

  def getWithReversedDirection: GraphEdge = {
    new GraphEdge(to, from, _weight)
  }
}

object GraphEdge {
  def createFromViewModel(viewModel: EdgeViewModel): GraphEdge = {
    if (viewModel == null) {
      null
    } else {
      new GraphEdge(viewModel.from, viewModel.to, viewModel.weight)
    }
  }
}