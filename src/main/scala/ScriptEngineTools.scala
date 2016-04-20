/**
  * Created by kentac55 on 16/04/20.
  */
import javax.script.{ScriptEngine, ScriptEngineManager}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.control.Exception._
import NotNothing._

class ScriptEngineTools extends CastableCopy{
  private val m = new ScriptEngineManager()
  private val e = m.getEngineByName("nashorn")
  def put(variable: String, obj: Any): Unit = {
    e.put(variable, obj)
  }
  def eval(script: String): Object = {
    e.eval(script)
  }
  def get(variable: String): Option[java.lang.Object] = {
    Option(e.get(variable))
  }
  def getAs[A:Manifest](variable: String): Option[A] = expand[A](variable)

  def expand[A: Manifest](variable: String): Option[A] = {
    val res = e.get(variable)
    castToOption(res)
  }
}
