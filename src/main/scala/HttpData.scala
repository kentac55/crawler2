
object HttpData {
  sealed trait httpDataOutput[T] {
    def getValue: T
  }

  sealed trait httpDataBoolean {
    val kv: Map[String, String]
    def isEmpty: Boolean = {
      kv.isEmpty
    }
    def isContain: Boolean = {
      kv.nonEmpty
    }
  }

  case class PostData(kv:Map[String, String] = Map()) extends httpDataOutput[String] with httpDataBoolean{
    def getValue: String = {
      rec()
    }
    private def rec(kv:Map[String, String] = kv, acc: String = ""): String = {
      if (kv.isEmpty) {
        "&$".r.replaceAllIn(acc, m => "")
      } else {
        rec(kv.tail, acc + kv.head._1 + "=" + kv.head._2 + "&")
      }
    }
  }
  case class ReqHeader(kv: Map[String, String] = Map()) extends httpDataOutput[Map[String, String]] with httpDataBoolean {
    def getValue: Map[String, String] = {
      Map("User-Agent" -> "Azuki Crawler") ++ rec()
    }
    private def rec(kv: Map[String, String] = kv, acc:Map[String, String] = Map()): Map[String, String] = {
      if(kv.isEmpty) {
        acc
      } else {
        rec(kv.tail, acc + (kv.head._1 -> kv.head._2))
      }
    }
  }
}
