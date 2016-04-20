import scala.util.{Failure, Success, Try}

/**
  * Created by kentac55 on 16/04/20.
  */
trait castSupport {
  def castType[T <: Any: Manifest](input: Any): Option[T] = {
    Try(input.asInstanceOf[T]) match {
      case Success(x) => {
        println(x.isInstanceOf[T])
        Some(x)
      }
      case Failure(x) => None
    }
  }
}
