import java.lang.Class

import scala.reflect.ClassTag

object Implicits2 {
  implicit class Js2Scala(val src: java.lang.Object) {
    def getScalaClass = {
      println("xxxxxxxxxxxxxxxxx")
      println(src.getClass)
//      val test = src.getClass.cast(src)
      src match {
        case x:Seq[_] =>
          x match {
            case x: Seq[Int] => println("-> Seq[Int]")
            case x: Seq[String] => println("-> Seq[Str]")
          }
        case x:String => println("-> Stringが来た")
        case x:java.lang.Integer => println("-> Integerが来た")
        case x:java.lang.Double => println("-> Doubleが来た")
        case x:jdk.nashorn.api.scripting.ScriptObjectMirror =>
          println("-> Objectが来た")
        case x:java.util.Arrays => println("-> Arraysが来た")
        case x:Array[_] => println("-> Arrayが来た")
        case x:List[Any] => println("-> Listが来た")
        case x:Map[Any, Any] => println("-> Mapが来た")
        case x:Array[Int] => println("-> Array[Int]が来た")
        case _ => println(src.getClass)
      }
    }
    def jsCast = {
      src match {
        case x:Seq[_] => x.toList
      }
    }
  }


  /**
    * プリミティブならキャスト
    * コレクションなら頑張ってListにキャスト
    * Arrayなら頑張ってListにキャスト
    * オブジェクトで未定義ならエラーか空データ返す
    *
    * javascriptのtypeof演算子使って求めてもいいかも
    */

  implicit class SeqTools[T: ClassTag](val src: Seq[T]) {
    val c = implicitly[ClassTag[T]]
    def getClassTag = {
      println(c)
      println(c.getClass)
    }
  }
  implicit class ArrayTools[T: ClassTag](val src: Array[T]) {
    val c = implicitly[ClassTag[T]]
    def getClassTag = {
      println(c)
      println(c.getClass)
    }
  }
}