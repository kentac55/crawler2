import jdk.nashorn.api.scripting.ScriptObjectMirror

trait CastableCopy {
  def castToOption[A: Manifest](value: Any): Option[A] = {
    val erasure = manifest[A] match {
      case Manifest.Byte    => classOf[java.lang.Byte]
      case Manifest.Short   => classOf[java.lang.Short]
      case Manifest.Char    => classOf[java.lang.Character]
      case Manifest.Long    => classOf[java.lang.Long]
      case Manifest.Float   => classOf[java.lang.Float]
      case Manifest.Double  => classOf[java.lang.Double]
      case Manifest.Boolean => classOf[java.lang.Boolean]
      case Manifest.Int     => classOf[java.lang.Integer]
      case m                => m.runtimeClass
    }
    value match {
      case simpleValue if erasure.isInstance(simpleValue) =>
        Some(value.asInstanceOf[A])
      case scriptObject:ScriptObjectMirror =>
        if(scriptObject.isInstanceOf[Array[Any]]) {
          println("array")
        }
        None
      case _ =>
        println("failure->" + value.getClass)
        None
    }
  }
}
