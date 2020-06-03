package taskPlanner.views

final case class StatisticViewModel(
                                     queueAlgorithm: String,
                                     onOneCoreTime: Int,
                                     onSystemTime: Int,
                                     criticalTime: Int,
                                     accelerationCoefficient: Double,
                                     systemEfficiencyCoefficient: Double,
                                     plannerEfficiencyCoefficient: Double,
                                   )
