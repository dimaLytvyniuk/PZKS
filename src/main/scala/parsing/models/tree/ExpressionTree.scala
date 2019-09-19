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

    if (_previousCharType == CharType.None) {
      _head = ExpressionNode.getEmptyNode(0)
      _currentNode = _head
    }

    _currentNode.leftNode = ExpressionNode.getEmptyNode(_currentNode.level + 1)
    _stack.push(_currentNode)
    _currentNode = _currentNode.leftNode
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

    if (_previousCharType == CharType.Variable) {
      storeCurrentVariable()
    } else if (_previousCharType == CharType.Number) {
      storeNumberVariable()
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

    if (_previousCharType == CharType.Variable) {
      storeCurrentVariable()
    } else if (_previousCharType == CharType.Number) {
      storeNumberVariable()
    }

    _previousCharType = CharType.ArithmeticOperation

    _currentNode = _stack.pop()
    ch match {
      case '+' => _currentNode.nodeType = NodeType.Sum
      case '-' => _currentNode.nodeType = NodeType.Subtraction
      case '*' => _currentNode.nodeType = NodeType.Multiplication
      case '/' => _currentNode.nodeType = NodeType.Division
      case _ => throw new IllegalArgumentException("ch")
    }

    _currentNode.addRightNode(ExpressionNode.getEmptyNode(_currentNode.level + 1))
    _stack.push(_currentNode)
    _currentNode = _currentNode.rightNode
  }

  private def storeCurrentVariable(): Unit = {
    val variableName = _strValues
    _currentNode.nodeType = NodeType.HasValue
    _currentNode.value.constName = variableName

    _strValues = ""
    usedVariables += variableName
  }

  private def storeNumberVariable(): Unit = {
    val intValue = _intValue
    _currentNode.nodeType = NodeType.HasValue
    _currentNode.value.intValue = intValue

    _intValue = 0
  }
}
