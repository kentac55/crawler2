
import scala.io.Source

trait util {
  def str2Byte(str: String, enc: String = "utf8", removeCRLF: Boolean = true): Array[Byte] = {
    if (removeCRLF) {
      str.getBytes(enc).filterNot(t => t == 13).filterNot(t => t == 10)
    } else {
      str.getBytes(enc)
    }
  }

  def byte2Str(bytes: Array[Byte], enc: String = "utf8", removeCRLF: Boolean = true): String = {
    if (removeCRLF) {
      Source.fromBytes(bytes.filterNot(t => t == 13).filterNot(t => t == 10), enc).mkString("")
    } else {
      Source.fromBytes(bytes, enc).mkString("")
    }
  }
  def detectEncode(src: Array[Byte]): String = {
    def testConv(code: String): Boolean = {
      val srcStr = byte2Str(src, code)
      val bytes = str2Byte(srcStr, code)
      src.filterNot(t => t == 13).filterNot(t => t == 10).sameElements(bytes)
    }
    val encs = Seq(
      "EUC_JP_Solaris",
      "ms932",
      "utf8"
    )
    val result = encs.filter(enc => testConv(enc))
    if (result.nonEmpty) {
      result.head
    } else {
      throw new Exception("エンコード不明")
    }
  }
}

