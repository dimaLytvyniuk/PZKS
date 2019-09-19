package parsing.models.tree

import parsing.models.exceptions.{IncorrectArithmeticOperationException, IncorrectClosedBraceException, IncorrectNumberPositionException, IncorrectOpenBraceException, IncorrectSymbolPositionException}

import scala.collection.mutable
import scala.collection.immutable

class ExpressionTree {
  private val operations = Array('+', '-', '/', '*')
  private val numbers = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  private var _head: ExpressionNode = null
  private var _currentNode: ExpressionNode = null

  private val _stack = new mutable.Stack[ExpressionNode]()
  private var _previousCharType = CharType.None

  private var _strValues: String = ""
  private var _intValue: Int = 0

  private var _countOfOpenedBraces = 0

  var usedVariables = new immutable.HashSet[String]()

  def head = _head

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
      addNumber(ch)
    }
    else
    {
      addVariableName(ch)
    }
  }

  private def addOpenBrace(): Unit = {
    if (
      _previousCharType == CharType.ClosedBrace ||
      _previousCharType == CharType.Number ||
      _previousCharType == CharType.Variable)
    {
        throw new IncorrectOpenBraceException()
    }

    _countOfOpenedBraces += 1
    _previousCharType = CharType.OpenBrace
  }

  private def addClosedBrace(): Unit = {
    if (
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.None ||
      _countOfOpenedBraces < 1)
    {
      throw new IncorrectClosedBraceException()
    }

    if (
      _previousCharType == CharType.Number ||
      _previousCharType == CharType.Variable) {
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

    _countOfOpenedBraces -= 1
    _previousCharType = CharType.ClosedBrace

    _currentNode = _stack.pop()
  }

  private def addNumber(ch: Char): Unit = {
    if (
      _previousCharType == CharType.ClosedBrace ||
      _previousCharType == CharType.Variable)
    {
      throw new IncorrectNumberPositionException()
    }

    _previousCharType = CharType.Number

    _intValue *= 10
    _intValue += ch.asInstanceOf[Int]
  }

  private def addVariableName(ch: Char): Unit = {
    if (
      _previousCharType == CharType.ClosedBrace ||
      _previousCharType == CharType.Number)
    {
      throw new IncorrectSymbolPositionException()
    }

    _previousCharType = CharType.Variable

    _strValues += ch
  }

  private def addOperation(ch: Char): Unit = {
    if (
      _previousCharType == CharType.OpenBrace ||
      _previousCharType == CharType.ArithmeticOperation ||
      _previousCharType == CharType.None)
    {
      throw new IncorrectArithmeticOperationException()
    }

    if (
      _previousCharType == CharType.Number ||
        _previousCharType == CharType.Variable) {
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

    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    ch match {
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
      var parentNode = _currentNode.parent
      while (
          parentNode != null &&
          newNode.braceNumber < parentNode.braceNumber) {
        parentNode = parentNode.parent
      }

      while (
          parentNode != null &&
          NodeType.checkPrioritization(_currentNode.nodeType, parentNode.nodeType) == 1 &&
          newNode.braceNumber == parentNode.braceNumber) {
        parentNode = parentNode.parent
      }

      if (parentNode == null) {
        newNode.level = 0
        newNode.leftNode = _currentNode
        _currentNode = newNode
        _head = _currentNode
      } else {
        newNode.level = parentNode.level + 1
        newNode.leftNode = parentNode.rightNode
        parentNode.rightNode = newNode
        _currentNode = newNode
      }
    }

    _previousCharType = CharType.ArithmeticOperation
  }

  private def getCurrentVariable(): NodeValue = {
    val variableName = _strValues

    _strValues = ""
    usedVariables += variableName

    new NodeValue { constName = variableName }
  }

  private def getNumberVariable(): NodeValue = {
    val numberValue = _intValue

    _intValue = 0

    new NodeValue { intValue = numberValue }
  }

  private def getNewValueNode(): ExpressionNode = {
    val newNode = ExpressionNode.getEmptyNode(0, _countOfOpenedBraces)
    newNode.nodeType = NodeType.HasValue

    if (_previousCharType == CharType.Variable) {
      newNode.value = getCurrentVariable()
    } else {
      newNode.value = getNumberVariable()
    }

    newNode
  }
}
