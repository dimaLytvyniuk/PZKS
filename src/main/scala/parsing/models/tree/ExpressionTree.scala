package parsing.models.tree

import parsing.models.exceptions.IncorrectOpenBraceException

import scala.collection.mutable

class ExpressionTree {
  private val operations = Array('+', '-', '/', '*')
  private val numbers = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  private var head = null
  private val stack = new mutable.Stack()
  private var previousCharType = CharType.None

  private var strValues: String = ""
  private var intValues: String = 0

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
      addConstantName(ch)
    }
  }

  private def addOpenBrace(): Unit = {
    if (
      previousCharType == CharType.ClosedBrace ||
      previousCharType == CharType.Number ||
      previousCharType == CharType.Variable)
    {
        throw new IncorrectOpenBraceException()
    }

    if (previousCharType = CharType.None)
  }

  private def addClosedBrace(): Unit = {

  }

  private def addNumber(ch: Char): Unit = {

  }

  private def addConstantName(ch: Char): Unit = {

  }

  private def addOperation(ch: Char): Unit = {

  }
}
