import org.scalatest._
import HttpData._

class httpDataSpec extends FlatSpec with DiagrammedAssertions {
  val testMap = Map("test" -> "A", "test2" -> "B")

  "httpDataOutput" should "出力形式が正しい" in {
    assert(PostData(testMap).getValue === "abc=123&def=456")
    assert(ReqHeader(testMap).getValue === Map("User-Agent" -> "Azuki Crawler", "abc" -> "123", "def" -> "456"))
  }

  "httpDataBoolean" should "中身の検知ができてる" in {
    assert(PostData(testMap).isEmpty === false)
    assert(PostData(testMap).isContain === true)
    assert(PostData().isEmpty === true)
    assert(PostData().isContain === false)
    assert(ReqHeader(testMap).isEmpty === false)
    assert(ReqHeader(testMap).isContain === true)
    assert(ReqHeader().isEmpty === true)
    assert(ReqHeader().isContain === false)
  }

  "requestHeaderインスタンス" should "UAを勝手に設置してくれる" in {
    assert(ReqHeader().getValue.get("User-Agent") === Some("Azuki Crawler"))
  }
}
