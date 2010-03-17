/*
 * Copyright 2009 Robey Pointer <robeypointer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lag.configgy

import scala.collection.mutable.Stack
import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader
import net.lag.extensions._


/**
 * An exception thrown when parsing a config file, if there was an error
 * during parsing. The `reason` string will contain the parsing
 * error details.
 */
case class ParseException(message: String, cause:Throwable) extends Exception(message, cause){
  def this(message:String) = this(message, null)
  def this(cause:Throwable) = this(cause.toString, cause)
}


private[configgy] class ConfigParser(var attr: Attributes, val importer: Importer) extends RegexParsers {

  val sections = new Stack[String]
  var prefix = ""


  // tokens
  override val whiteSpace = """(\s+|#[^\n]*\n)+""".r
  val numberToken: Parser[String] = """-?\d+(\.\d+)?""".r
  val stringToken: Parser[String] = """([^\\\"]|\\[^ux]|\\\n|\\u[0-9a-fA-F]{4}|\\x[0-9a-fA-F]{2})*""".r
  val identToken: Parser[String] = """([\da-zA-Z_][-\w]*)(\.[a-zA-Z_][-\w]*)*""".r
  val assignToken: Parser[String] = """=|\?=""".r
  val tagNameToken: Parser[String] = """[a-zA-Z][-\w]*""".r


  def root = rep(includeFile | assignment | toggle | sectionOpen | sectionClose |
                 sectionOpenBrace | sectionCloseBrace)

  def includeFile = "include" ~> string ^^ {
    case filename: String =>
      new ConfigParser(attr.makeAttributes(sections.mkString(".")), importer) parse importer.importFile(filename)
  }

  def assignment = identToken ~ assignToken ~ value ^^ {
    case k ~ a ~ v => if (a match {
      case "=" => true
      case "?=" => ! attr.contains(prefix + k)
    }) v match {
      case x: Int => attr(prefix + k) = x
      case x: String => attr(prefix + k) = x
      case x: Array[String] => attr(prefix + k) = x
      case x: Boolean => attr(prefix + k) = x
    }
  }

  def toggle = identToken ~ trueFalse ^^ { case k ~ v => attr(prefix + k) = v }

  def sectionOpen = "<" ~> tagNameToken ~ rep(tagAttribute) <~ ">" ^^ {
    case name ~ attrList => openBlock(name, attrList)
  }
  def tagAttribute = opt(whiteSpace) ~> (tagNameToken <~ "=") ~ string ^^ { case k ~ v => (k, v) }
  def sectionClose = "</" ~> tagNameToken <~ ">" ^^ { name => closeBlock(Some(name)) }

  def sectionOpenBrace = tagNameToken ~ opt("(" ~> rep(tagAttribute) <~ ")") <~ "{" ^^ {
    case name ~ attrListOption => openBlock(name, attrListOption.getOrElse(Nil))
  }
  def sectionCloseBrace = "}" ^^ { x => closeBlock(None) }

  private def openBlock(name: String, attrList: List[(String, String)]) = {
    val parent = if (sections.size > 0) attr.makeAttributes(sections.mkString(".")) else attr
    sections += name
    prefix = sections.mkString("", ".", ".")
    val newBlock = attr.makeAttributes(sections.mkString("."))
    for ((k, v) <- attrList) k match {
      case "inherit" =>
        newBlock.inheritFrom = Some(if (parent.getConfigMap(v).isDefined) parent.makeAttributes(v) else attr.makeAttributes(v))
      case _ =>
        throw new ParseException("Unknown block modifier")
    }
  }

  private def closeBlock(name: Option[String]) = {
    if (sections.isEmpty) {
      failure("dangling close tag")
    } else {
      val last = sections.pop
      if (name.isDefined && last != name.get) {
        failure("got closing tag for " + name.get + ", expected " + last)
      } else {
        prefix = if (sections.isEmpty) "" else sections.mkString("", ".", ".")
      }
    }
  }


  def value: Parser[Any] = number | string | stringList | trueFalse
  def number = numberToken ^^ { x => if (x.contains('.')) x else x.toInt }
  def string = "\"" ~> stringToken <~ "\"" ^^ { s => attr.interpolate(prefix, s.unquoteC) }
  def stringList = "[" ~> repsep(string | numberToken, opt(",")) <~ (opt(",") ~ "]") ^^ { list => list.toArray }
  def trueFalse: Parser[Boolean] = ("(true|on)".r ^^ { x => true }) | ("(false|off)".r ^^ { x => false })


  def parse(in: String): Unit = {
println("parsing:"+in )    
    parseAll(root, in) match {
      case Success(result, _) => result
      case x @ Failure(msg, z) => throw new ParseException(x.toString)
      case x @ Error(msg, _) => throw new ParseException(x.toString)
    }
  }
}
