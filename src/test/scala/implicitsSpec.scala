import org.scalatest._
import Implicits._

class implicitsSpec extends FlatSpec with DiagrammedAssertions {
  val testBytes:Array[Byte] = "test String \n repeat, test String".getBytes
  val testStr = "これはテストスクリプト"
  val utf8Bytes = testStr.getBytes("utf8")
  val ms932Bytes = testStr.getBytes("ms932")
  val eucJpBytes = testStr.getBytes("EUC_JP_Solaris")


  "mkString2関数" should "バイト配列から文字を返すことができる" in {
    assert(testBytes.mkString2("utf8" ,false) === "test String \n repeat, test String")
  }
  it should "改行を削除できる" in {
    assert(testBytes.mkString2("utf8", true) === "test String  repeat, test String")
  }
  it should "自動でエンコードを認識して日本語にしてくれる" in {
    assert(utf8Bytes.mkString2() === testStr)
    assert(ms932Bytes.mkString2() === testStr)
    assert(eucJpBytes.mkString2() === testStr)
  }

  "detectEncode関数" should "自動でエンコードを認識してくれる" in {
    assert(utf8Bytes.detectEncode === "utf8")
    assert(ms932Bytes.detectEncode === "ms932")
    assert(eucJpBytes.detectEncode === "EUC_JP_Solaris")
  }

  it should "認識できないエンコードを検知したらエラー吐く" in {
    intercept[Exception] {
      val errorBytes:Array[Byte] = List(-10,20,-30,40,-50,60,-70,80,-90,100).map(_.toByte).toArray
      errorBytes.detectEncode
    }
  }

  "mkByte関数" should "改行の除去ができる" in {
    val script = "テスト\r\nテスト"
    val t = script.mkByte("utf8", true)
    val f = script.mkByte("utf8", false)
    assert((t.contains(10), t.contains(13)) === (false, false) )
    assert((f.contains(10), f.contains(13)) === (true, true) )
  }


}
