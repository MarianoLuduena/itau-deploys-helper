package com.itau.deploys.common

trait ToggleCase {

  private val UNDERSCORE = "_"

  protected def toSnakeCase(str: String): String =
    str.map {
      case c if c.isUpper => UNDERSCORE + c.toLower
      case c => c.toString
    }.mkString

  protected def toCamelCase(str: String): String = {
    val tokens = str.split(UNDERSCORE)
    tokens.tail.foldLeft(tokens.head) {
      case (camelCaseStr, token) => camelCaseStr + token.head.toUpper + token.tail
    }
  }

}
