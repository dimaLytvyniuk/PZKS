package taskPlanner.models

import taskPlanner.views.GraphNodeViewModel

class GraphNode(private var _id: String, _label: String, _weight: Option[Int]) {
  def id: String = _id

  def label: String = _label

  def weight: Int = _weight.getOrElse(0)

  def createWithOppositeWeight: GraphNode = {
    new GraphNode(id, label, Some(weight * (-1)))
  }
}

object GraphNode {
  def createFromViewModel(viewModel: GraphNodeViewModel): GraphNode = {
    if (viewModel == null) {
      null
    } else {
      new GraphNode(viewModel.id, viewModel.label, viewModel.weight)
    }
  }
}
