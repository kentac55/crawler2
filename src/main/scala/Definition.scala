
import org.json4s.DefaultFormats

object Definition {
  implicit val formats = DefaultFormats

  sealed abstract class jsonDef

  sealed abstract class crawler extends jsonDef
  case class NextUrlInfo(method: String, query: Map[String, String], selector: String, attr: String, regex: String, replace: String) extends crawler
  case class LayerInfo(layer: Int, pattern: String, block: String, width: Option[NextUrlInfo], depth: Option[NextUrlInfo], exit: Option[Boolean]) extends crawler
  case class HttpSettings(maxCrawlTimes: Int, header: Map[String, String], timeoutInMs: Int, intervalInMs: Int) extends crawler
  case class Init(url: String, method: String, query: Map[String, String]) extends crawler
  case class CrawlDefinition(siteID: String, httpSettings: HttpSettings, init: Init, layers:List[LayerInfo]) extends crawler

  sealed abstract class analyzer extends jsonDef
  case class Property(propName: String, layer: Int, selector: String, args: String, scripts: Option[List[String]]) extends analyzer
  case class AnalyzeDefinition(siteID: String, props:List[Property]) extends analyzer
}
