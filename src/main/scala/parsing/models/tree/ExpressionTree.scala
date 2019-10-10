package parsing.models.tree

import parsing.models.exceptions._

import scala.collection.mutable.ArrayBuffer
import scala.collection.{immutable, mutable}

class ExpressionTree {
  protected val operations = Array('+', '-', '/', '*')
  protected val numbers = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  protected var _head: ExpressionNode = null
  protected var _currentNode: ExpressionNode = null

  protected var _previousCharType = CharType.None

  protected var _strValues: String = ""
  protected var _numberValue: Double = 0
  protected var _currentDoubleDivider = 10
  protected var _currentValueSign: Int = 1

  protected var _countOfOpenedBraces = 0

  protected var _usedVariables = new immutable.HashSet[String]()
  protected val _supportedFunctions = Set("max", "min")
  protected var _currentFunctionName: String = null
  protected var _currentFunctionParameters = new ArrayBuffer[TokenValue]()

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
    else if (ch == '.') {
      addDot()
    }
    else {
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

    if (isValuePrevious) {
      addNewLeaf()
    }
  }

  protected def addOpenBrace(): Unit = {
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

  protected def addClosedBrace(): Unit = {
    if (!isClosedBraceAllowed)
    {
      throw new IncorrectClosedBraceException()
    }

    if (isFunctionNow && isValuePrevious) {
      addFunction()
    } else {
      if (isValuePrevious) {
        addNewLeaf()
      }

      _countOfOpenedBraces -= 1
    }

    _previousCharType = CharType.ClosedBrace
  }

  protected def addNumber(ch: Char): Unit = {
    if (!isNumberAllowed)
    {
      throw new IncorrectNumberPositionException()
    }

    if (_previousCharType == CharType.Dot || _previousCharType == CharType.DoubleValue) {
      val currDigit: Double = ch.asDigit
      _numberValue += currDigit / _currentDoubleDivider
      _currentDoubleDivider *= 10

      _previousCharType = CharType.DoubleValue
    } else {
      _numberValue *= 10
      _numberValue += ch.asDigit

      _previousCharType = CharType.IntValue
    }
  }

  protected def addComa(): Unit = {
    if (!isComaAllowed) {
      throw new IncorrectSymbolPositionException()
    }

    val tokenValue = getCurrentTokenValue()
    _currentFunctionParameters += tokenValue

    _previousCharType = CharType.FunctionComa
  }

  protected def addVariableName(ch: Char): Unit = {
    if (!isVariableNameAllowed)
    {
      throw new IncorrectSymbolPositionException()
    }

    _strValues += ch

    _previousCharType = CharType.Variable
  }

  protected def addOperation(ch: Char): Unit = {
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
    } else {
      if (isValuePrevious) {
        addNewLeaf()
      }

      addOperationNode(ch)
    }

    _previousCharType = CharType.ArithmeticOperation
  }

  protected def addDot(): Unit = {
    if (!isDotAllowed) {
      throw new IncorrectDotException
    }

    _previousCharType = CharType.Dot
  }

  protected def addFunction(): Unit = {
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

  protected def addOperationNode(operation: Char): Unit = {
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

  protected def getCurrentTokenValue(): TokenValue = {
    val newTokenValue = new TokenValue()

    if (_previousCharType == CharType.Variable) {
      newTokenValue.constName = _strValues
      _usedVariables += _strValues

      _strValues = ""
    } else {
      newTokenValue.numberValue = _numberValue

      _numberValue = 0
      _currentDoubleDivider = 10
    }
    newTokenValue.sign = this._currentValueSign
    _currentValueSign = 1

    newTokenValue
  }

  protected def getNewValueNode(): ExpressionNode = {
    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    newNode.nodeType = NodeType.HasValue
    newNode.value = new NodeValue { tokenValue = getCurrentTokenValue() }

    newNode
  }

  def getCopy(): ExpressionTree = {
    val newTree = new ExpressionTree
    newTree._head = _head
    newTree._usedVariables = usedVariables

    newTree
  }

  protected def isFunctionNow = _currentFunctionName != null && _currentFunctionName != ""

  protected def isValuePrevious = isNumberPrevious || _previousCharType == CharType.Variable

  protected def isNumberPrevious = _previousCharType == CharType.IntValue || _previousCharType == CharType.DoubleValue

  protected def isClosedBraceAllowed: Boolean = {
    ((_previousCharType == CharType.Variable ||
      isValuePrevious ||
      _previousCharType == CharType.ClosedBrace) && _countOfOpenedBraces > 0) ||
    ((isValuePrevious || _previousCharType == CharType.OpenedFunctionBrace) &&
     isFunctionNow)
  }

  protected def isOpenBraceAllowed: Boolean = {
    _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.Variable ||
      _previousCharType == CharType.None
  }

  protected def isNumberAllowed: Boolean = {
    _previousCharType == CharType.ArithmeticOperation ||
      isNumberPrevious ||
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.None ||
      _previousCharType == CharType.OpenedFunctionBrace ||
      _previousCharType == CharType.FunctionComa ||
      _previousCharType == CharType.Dot
  }

  protected def isVariableNameAllowed: Boolean = {
    _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.Variable ||
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.None ||
      _previousCharType == CharType.OpenedFunctionBrace ||
      _previousCharType == CharType.FunctionComa
  }

  protected def isOperationAllowed: Boolean = {
    isValuePrevious ||
    _previousCharType == CharType.ClosedBrace ||
    _previousCharType == CharType.None ||
    _previousCharType == CharType.OpenBrace
  }

  protected def isComaAllowed: Boolean = {
    isFunctionNow && isValuePrevious
  }

  protected def isEndBuildingExpressionAllowed: Boolean = {
    _countOfOpenedBraces == 0 &&
      (_previousCharType == CharType.ClosedBrace ||
        isValuePrevious)
  }

  protected def isDotAllowed: Boolean = {
    _previousCharType == CharType.IntValue
  }
}
