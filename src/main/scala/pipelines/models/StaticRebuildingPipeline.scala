package pipelines.models

import parsing.models.tree.ExpressionNode

import scala.collection.mutable.ArrayBuffer

class StaticRebuildingPipeline(val serialNumber: Int) {
  private val _steps = new ArrayBuffer[String]
  private var _calculatedLevel = 0
  private var _onCompleted: (ExpressionNode) => Unit = (ExpressionNode) => ()
  private var _currentNode: ExpressionNode = null
  private var _nextNode: ExpressionNode = null
  private var _isStarted = false

  def steps: ArrayBuffer[String] = _steps

  def calculatedLevel: Int = _calculatedLevel
  def calculatedLevel_=(level: Int) {
    _calculatedLevel = calculatedLevel
  }

  def onCompleted: (ExpressionNode) => Unit = _onCompleted
  def onCompleted_= (func: (ExpressionNode) => Unit) {
    _onCompleted = func
  }

  def currentNode: ExpressionNode = _currentNode
  def currentNode_= (node: ExpressionNode) {
    _isStarted = true
    _currentNode = node
  }

  def nextNode: ExpressionNode = _nextNode
  def nextNode_= (node: ExpressionNode) {
    _nextNode = node
  }

  def hasNext: Boolean = _nextNode != null

  def tact(): Unit = {
    if (!_isStarted) {
      _steps += ""
    } else {
      if (_currentNode == null) {
        _steps += "-"
      } else {
        _steps += _currentNode.evaluateWithBracesOnlyForCurrentNodeStr()
      }

      _onCompleted(_currentNode)
    }
  }

  def updateState(): Unit = {
    _currentNode = _nextNode
    _nextNode = null

    if (_currentNode != null) {
      _isStarted = true
    }
  }
}
