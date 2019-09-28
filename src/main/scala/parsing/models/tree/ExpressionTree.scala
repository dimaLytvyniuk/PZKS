package parsing.models.tree

import parsing.models.exceptions._

import scala.collection.mutable.ArrayBuffer
import scala.collection.{immutable, mutable}

class ExpressionTree {
  private val operations = Array('+', '-', '/', '*')
  private val numbers = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  private var _head: ExpressionNode = null
  private var _currentNode: ExpressionNode = null

  private var _previousCharType = CharType.None

  private var _strValues: String = ""
  private var _numberValue: Int = 0
  private var _currentValueSign: Int = 1

  private var _countOfOpenedBraces = 0

  private var _usedVariables = new immutable.HashSet[String]()
  private var _supportedFunctions = Set("max", "min")
  private var _currentFunctionName: String = null
  private var _currentFunctionParameters = new ArrayBuffer[TokenValue]()

  def head = _head
  def usedVariables = _usedVariables

  def addChar(ch: Char): Unit = {
    if (ch == '(') {
      addOpenBrace()
    }
    else if (ch == ')') {
      addClosedBrace()
    }
    else if (operations.contains(ch)) {
      addOperation(ch)
    }
    else if (numbers.contains(ch)) {
      if (_previousCharType == CharType.Variable) {
        addVariableName(ch)
      } else {
        addNumber(ch)
      }
    }
    else if (ch == ',') {
      addComa()
    }
    else
    {
      addVariableName(ch)
    }
  }

  def evaluateStr: String = _head.evaluateStr()

  def endBuildingExpression(): Unit = {
    if (!isEndBuildingExpressionAllowed)
    {
      throw new IncorrectEndOfExpressionException()
    }

    if (_countOfOpenedBraces > 0) {
      throw new IncorrectCountOfBracesException
    }

    if (_previousCharType == CharType.Number || _previousCharType == CharType.Variable) {
      addNewLeaf()
    }
  }

  private def addOpenBrace(): Unit = {
    if (!isOpenBraceAllowed)
    {
        throw new IncorrectOpenBraceException()
    }

    if (_previousCharType == CharType.Variable) {
      if (_supportedFunctions.contains(_strValues)) {
        _currentFunctionName = _strValues
        _strValues = ""
        _previousCharType = CharType.OpenedFunctionBrace
      } else {
        throw new IncorrectOpenBraceException()
      }
    } else {
      _countOfOpenedBraces += 1
      _previousCharType = CharType.OpenBrace
    }
  }

  private def addClosedBrace(): Unit = {
    if (!isClosedBraceAllowed)
    {
      throw new IncorrectClosedBraceException()
    }

    if (_previousCharType == CharType.FunctionIntValue || _previousCharType == CharType.FunctionCharValue) {
      addFunction()
    } else if (_previousCharType == CharType.Number || _previousCharType == CharType.Variable) {
      addNewLeaf()
    }

    if (_previousCharType != CharType.FunctionIntValue && _previousCharType != CharType.FunctionCharValue) {
      _countOfOpenedBraces -= 1
    }

    _previousCharType = CharType.ClosedBrace
  }

  private def addNumber(ch: Char): Unit = {
    if (!isNumberAllowed)
    {
      throw new IncorrectNumberPositionException()
    }

    if (_currentFunctionName == null || _currentFunctionName == "") {
      _previousCharType = CharType.Number
    } else {
      _previousCharType = CharType.FunctionIntValue
    }

    _numberValue *= 10
    _numberValue += ch.asDigit
  }

  private def addComa(): Unit = {
    if (!isComaAllowed) {
      throw new IncorrectSymbolPositionException()
    }

    val tokenValue = getCurrentTokenValue()
    _currentFunctionParameters += tokenValue

    _previousCharType = CharType.FunctionComa
  }

  private def addVariableName(ch: Char): Unit = {
    if (!isVariableNameAllowed)
    {
      throw new IncorrectSymbolPositionException()
    }

    if (_currentFunctionName == null || _currentFunctionName == "") {
      _previousCharType = CharType.Variable
    } else {
      _previousCharType = CharType.FunctionCharValue
    }

    _strValues += ch
  }

  private def addOperation(ch: Char): Unit = {
    if (!isOperationAllowed)
    {
      throw new IncorrectArithmeticOperationException()
    }

    if (_previousCharType == CharType.None || _previousCharType == CharType.OpenBrace) {
      if (ch == '*' || ch == '/') {
        throw new IncorrectArithmeticOperationException()
      }

      if (ch == '-') {
        _currentValueSign = -1
      }
    } else if (_previousCharType == CharType.Number || _previousCharType == CharType.Variable) {
      addNewLeaf()

      addOperationNode(ch)
    }

    _previousCharType = CharType.ArithmeticOperation
  }

  private def addFunction(): Unit = {
    val tokenValue = getCurrentTokenValue()
    _currentFunctionParameters += tokenValue
    val functionDeclaration = TwoParameterFunctionDeclaration.nameToDeclarationMap(_currentFunctionName)
    if (_currentFunctionParameters.length == 2) {
      val newFunctionImplementation = new TwoParameterFunctionImplementation(_currentFunctionParameters(0), _currentFunctionParameters(1), functionDeclaration)
      val newNodeValue = new NodeValue { functionImplementation = newFunctionImplementation }

      val newNode = new ExpressionNode(0, NodeType.HasValue, newNodeValue, _countOfOpenedBraces)
      if (_head == null) {
        _head = newNode
        _currentNode = _head
      } else {
        newNode.level = _currentNode.level + 1
        _currentNode.rightNode = newNode
        _currentNode = newNode
      }

      _currentFunctionParameters = new ArrayBuffer[TokenValue]()
      _currentFunctionName = ""
    } else {
      throw new IncorrectFunctionException
    }
  }

  def addNewLeaf(): Unit = {
    val newNode = getNewValueNode()

    if (_head == null) {
      newNode.level = 0
      _head = newNode
      _currentNode = _head
    } else {
      newNode.level = _currentNode.level + 1
      _currentNode.rightNode = newNode
      _currentNode = newNode
    }
  }

  private def addOperationNode(operation: Char): Unit = {
    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    operation match {
      case '+' => newNode.nodeType = NodeType.Sum
      case '-' => newNode.nodeType = NodeType.Subtraction
      case '*' => newNode.nodeType = NodeType.Multiplication
      case '/' => newNode.nodeType = NodeType.Division
      case _ => throw new IllegalArgumentException("ch")
    }

    if (_head == null) {
      newNode.level = 0
      _head = newNode
      _currentNode = _head
    } else {
      var lastNode = _currentNode
      while (
        lastNode.parent != null &&
          newNode.braceNumber < lastNode.parent.braceNumber) {
        lastNode = lastNode.parent
      }

      while (
        lastNode.parent != null &&
          NodeType.checkPrioritization(newNode.nodeType, lastNode.parent.nodeType) == 1 &&
          newNode.braceNumber == lastNode.parent.braceNumber) {
        lastNode = lastNode.parent
      }

      if (lastNode.parent == null) {
        newNode.level = 0
        newNode.leftNode = lastNode
        _currentNode = newNode
        _head = _currentNode
      } else {
        val targetNode = lastNode.parent
        newNode.level = targetNode.level + 1
        if (targetNode.rightNode != null) {
          newNode.leftNode = targetNode.rightNode
        }

        targetNode.rightNode = newNode
        _currentNode = newNode
      }
    }
  }

  private def getCurrentTokenValue(): TokenValue = {
    val newTokenValue = new TokenValue()

    if (_previousCharType == CharType.FunctionCharValue || _previousCharType == CharType.Variable) {
      newTokenValue.constName = _strValues
      _usedVariables += _strValues

      _strValues = ""
    } else {
      newTokenValue.numberValue = _numberValue

      _numberValue = 0
    }
    newTokenValue.sign = this._currentValueSign
    _currentValueSign = 1

    newTokenValue
  }

  private def getNewValueNode(): ExpressionNode = {
    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    newNode.nodeType = NodeType.HasValue
    newNode.value = new NodeValue { tokenValue = getCurrentTokenValue() }

    newNode
  }

  private def isClosedBraceAllowed: Boolean = {
    (_previousCharType == CharType.Variable ||
      _previousCharType == CharType.Number ||
      _previousCharType == CharType.FunctionCharValue ||
      _previousCharType == CharType.FunctionIntValue ||
      _previousCharType == CharType.ClosedBrace ||
      (
        (
          _previousCharType == CharType.FunctionCharValue ||
          _previousCharType == CharType.FunctionIntValue ||
          _previousCharType == CharType.OpenedFunctionBrace) &&
        _countOfOpenedBraces < 1)) && _countOfOpenedBraces > 0
  }

  private def isOpenBraceAllowed: Boolean = {
    _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.Variable ||
      _previousCharType == CharType.None
  }

  private def isNumberAllowed: Boolean = {
    _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.Number ||
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.None ||
      _previousCharType == CharType.OpenedFunctionBrace ||
      _previousCharType == CharType.FunctionComa ||
      _previousCharType == CharType.FunctionIntValue
  }

  private def isVariableNameAllowed: Boolean = {
    _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.Variable ||
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.None ||
      _previousCharType == CharType.OpenedFunctionBrace ||
      _previousCharType == CharType.FunctionComa ||
      _previousCharType == CharType.FunctionCharValue
  }

  private def isOperationAllowed: Boolean = {
    _previousCharType == CharType.Variable ||
    _previousCharType == CharType.Number ||
    _previousCharType == CharType.ClosedBrace ||
    _previousCharType == CharType.None ||
    _previousCharType == CharType.OpenBrace
  }

  private def isComaAllowed: Boolean = {
    _previousCharType == CharType.FunctionIntValue ||
    _previousCharType == CharType.FunctionCharValue
  }

  private def isEndBuildingExpressionAllowed = {
    (_countOfOpenedBraces == 0) &&
      _previousCharType == CharType.ClosedBrace ||
      _previousCharType == CharType.Number ||
      _previousCharType == CharType.Variable
  }
}
