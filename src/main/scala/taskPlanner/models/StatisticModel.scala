package taskPlanner.models

import taskPlanner.views.StatisticViewModel

class StatisticModel(val queueAlgorithm: String, val processorCount: Int, val onOneCoreTime: Int, val criticalTime: Int, val onSystemTime: Int) {

  def accelerationCoefficient: Double = onOneCoreTime.asInstanceOf[Double] / onSystemTime.asInstanceOf[Double]

  def systemEfficiencyCoefficient: Double = accelerationCoefficient / processorCount

  def plannerEfficiencyCoefficient: Double = criticalTime.asInstanceOf[Double] / onSystemTime.asInstanceOf[Double]
}
