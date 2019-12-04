package pipelines.models

import scala.collection.mutable.ArrayBuffer

class StaticRebuildingPipeline(val serialNumber: Int) {
  private val _steps = new ArrayBuffer[String]

  def steps: ArrayBuffer[String] = _steps
}
