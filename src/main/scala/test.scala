import org.json4s._
import org.json4s.native.JsonMethods._
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager, ScriptException}

import jdk.nashorn.api.scripting.ScriptObjectMirror

import scala.collection.JavaConverters._
import scala.io.Source._
import scala.util.control.Exception._

import Implicits2._

/**
  * Created by kentac55 on 16/04/17.
  */
object test extends App {
  val file = fromFile("t.json")
  val json = parse(file.mkString)
  file.close()

  implicit val formats = DefaultFormats
  case class Scripts(scripts: Option[List[String]])

  case class Person(name: String, age: Int)

  val scripts = json.extract[Scripts].scripts
  if (scripts.isDefined) {
    val sampleFile = fromFile("test.html")
    val sample = sampleFile.mkString
    sampleFile.close()
    val concatenatedScripts = scripts.get.foldLeft(s"'use strict';")((x, y) => x + " " + y + ";")
    val manager = new ScriptEngineManager()
    val engine = manager.getEngineByName("nashorn")
    val test = "test"
    engine.put("value", test)
    engine.put("person", Person("kc5m", 23))
    engine.put("float2", 5.5)
    engine.put("int2", 2)
    engine.eval("var sample = [\"testA\", \"testB\"];")
    engine.eval("var text = \"test\";")
    engine.eval("var single = \"a\";")
    engine.eval("var number = 1;")
    engine.eval("var obj = { a: \"testObj\", b: 100 };")
    engine.eval("var person2 = person; person2.name = \"kc6m\";")
    engine.eval("var person3 = person; person3.man = true;")
    engine.eval("var float = 1.00001;")

    val get = engine.get("sample")
    val get2 = engine.get("text")
    val get3 = engine.get("person")
    val get4 = engine.get("single")
    val get5 = engine.get("number")
    val get6 = engine.get("obj")
    val get7 = engine.get("person2")
    val get8 = engine.get("person3")
    val get9 = engine.get("float")
    val get10 = engine.get("float2")
    val get11 = engine.get("int2")

    val inv = engine.asInstanceOf[Invocable]
    val res = catching(classOf[ClassCastException]) opt get.asInstanceOf[Array[String]]

    // todo てかメモ↓
    // javaで作られたクラスはいろいろイジられてもエンジンをそのまま通過する
    // 現地で作られたjavascript的なオブジェクトはプリミティブならプリミティブ値、オブジェクトならScriptObjectMirror


//    println(get.getClass) // class jdk.nashorn.api.scripting.ScriptObjectMirror
//    println(get2.getClass) // class java.lang.String
//    println(get3.getClass) // class test$Person
//    println(get4.getClass) // class java.lang.String
//    println(get5.getClass) // class java.lang.Integer
//    println(get6.getClass) // class jdk.nashorn.api.scripting.ScriptObjectMirror
//    println(get7.getClass) // class test$Person
//    println(get8.getClass) // class test$Person
//    println(get9.getClass) // class java.lang.Double
//    println(get10.getClass) // class java.lang.Double
//    println(get11.getClass) // class java.lang.Integer
    println("--------")


    implicit class JsToScala(val src:java.lang.Object) {
      def debug() = {
        val str = src.getClass.cast(src)
        println(str)
      }
      def autoCast() = {
        src.getClass.cast(this)
      }
    }
    get.getScalaClass
    get2.getScalaClass
    get3.getScalaClass
    get4.getScalaClass
    get5.getScalaClass
    get6.getScalaClass
    get7.getScalaClass
    get8.getScalaClass
    get9.getScalaClass
    get10.getScalaClass
    get11.getScalaClass


//    println(s + " --> " + s.getClass.toString)
//    println(n + " -->" + s.getClass.toString)

//    println(get.asInstanceOf[ScriptObjectMirror].get("0"))
//    println(get2.asInstanceOf[ScriptObjectMirror].get())
//    println(get3.asInstanceOf[ScriptObjectMirror].get("name"))
//    println(res.getOrElse(Array()))
//    println(res)
//    println(concatenatedScripts)
  }
}
