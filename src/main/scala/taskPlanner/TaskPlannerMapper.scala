package taskPlanner

import taskPlanner.models.StatisticModel
import taskPlanner.views.{CombinedStatisticViewModel, StatisticViewModel}

object TaskPlannerMapper {
  def createStatisticViewModel(statisticModel: StatisticModel): StatisticViewModel = {
    StatisticViewModel(
      statisticModel.queueAlgorithm,
      statisticModel.onOneCoreTime,
      statisticModel.onSystemTime,
      statisticModel.criticalTime,
      statisticModel.accelerationCoefficient,
      statisticModel.systemEfficiencyCoefficient,
      statisticModel.plannerEfficiencyCoefficient)
  }

  def createCombinedStatisticViewModel(firstModel: StatisticModel, secondModel: StatisticModel): CombinedStatisticViewModel = {
    CombinedStatisticViewModel(createStatisticViewModel(firstModel), createStatisticViewModel(secondModel))
  }
}
