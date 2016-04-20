
import scala.io.Source.fromFile
import org.json4s._
import org.json4s.native.JsonMethods._
import Implicits._
import Definition._
import HttpData._
import org.json4s.JsonAST.JValue
import org.jsoup.Jsoup

object Main extends App {
  implicit val formats = DefaultFormats

  val file = fromFile(args(0) + ".json")
  val json = parse(file.mkString)
  file.close()
  val fileE = fromFile(args(0) + "e.json")
  val jsonE = parse(fileE.mkString)
  fileE.close()

  val timeout = (json \ "httpSettings").extract[HttpSettings].timeoutInMs
  val interval = (json \ "httpSettings").extract[HttpSettings].intervalInMs

  val initSettings = (json \ "init").extract[Init]
  val firstUrl = initSettings.url
  val method = initSettings.method


  def layerDetect(target: String, layers: List[LayerInfo]): Int = {
    if (layers == Nil) {
      -1
    } else if (layers.head.pattern.r.findFirstMatchIn(target).isDefined) {
      layers.head.layer
    } else {
      layerDetect(target, layers.tail)
    }
  }

  def rec(urlData: List[httpAccessSettings], json: JValue, dataList: List[Data], acc: Int = 0): List[Data] = {
    val httpSettings = (json \ "httpSettings").extract[HttpSettings]
    val interval = httpSettings.intervalInMs
    if (acc > httpSettings.maxCrawlTimes) {
      println("max crawl reached")
      dataList
    } else {
      println(s"now: $acc")
      val current = urlData.head
      println(s"crawl Url: ${current.url}")
      val layersInfo = (json \ "layers").extract[List[LayerInfo]]
      val layer = layerDetect(current.url, layersInfo)
      println(s"layer: $layer")
      if (layer == -1) {
        println("no one matched")
        Thread.sleep(interval)
        rec(urlData.tail, json, dataList, acc + 1)
      } else {
        val settings = layersInfo(layer)
        val block = settings.block
        val http = HttpAccess(current.url, current.timeoutInMs, current.method, current.postData, current.reqHeader)
        val html = http.responseByte.mkString2()
        val random = new scala.util.Random(new java.security.SecureRandom())
        val blocks:List[(Symbol, String)] = Jsoup.parse(html).select(block).toArray().toList.map(x => {
          (Symbol(random.nextInt().toString), x.toString)
        })
        val widthUrl: Option[httpAccessSettings] = if (settings.width.isDefined) {
          val width = settings.width.get
          val raw = Jsoup.parse(html).select(width.selector).get(0).attr(width.attr)
          Some(httpAccessSettings(
            width.regex.r.replaceAllIn(raw, width.replace),
            httpSettings.timeoutInMs,
            width.method,
            PostData(width.query),
            ReqHeader(httpSettings.header),
            current.publisher
          ))
        } else {
          None
        }
        val depthUrls: Option[List[httpAccessSettings]] = if (settings.depth.isDefined) {
          val depth = settings.depth.get
          Some(blocks.map(x => {
            val raw = Jsoup.parse(x._2).select(depth.selector).attr(depth.attr)
            httpAccessSettings(
              depth.regex.r.replaceAllIn(raw, depth.replace),
              httpSettings.timeoutInMs,
              depth.method,
              PostData(depth.query),
              ReqHeader(httpSettings.header),
              x._1
            )
          }))
        } else {
          None
        }
        val newUrls = (widthUrl.isDefined, depthUrls.isDefined) match {
          case (true, true) => depthUrls.get reverse_::: (widthUrl.get :: urlData.tail)
          case (true, _) => widthUrl.get :: urlData.tail
          case (_, true) => depthUrls.get reverse_::: urlData.tail
          case (_, _) => urlData.tail
        }
        val newDataList: List[Data] = blocks.foldLeft(dataList)((x, y) => {
          Data(current.url, layer, y._2, current.publisher, y._1, settings.exit.getOrElse(false)) :: x
        })
        println("list contains..." + newUrls.map(x => x.url).mkString(", "))
        println("Data Head... ")
        println(newDataList.head.structure)
        http.disconnect
        Thread.sleep(interval)
        rec(newUrls, json, newDataList, acc + 1)
      }
    }
  }

  val first = httpAccessSettings(firstUrl, timeout, method, PostData(Map()), ReqHeader(Map()), 'origin)
  val res = rec(List(first), json, Nil)
  val sorted = res.sortData
  sorted.foreach(x => {
    println("------")
    println(x.head.url)
    println(x.extSymbol)
  })

  case class Data(url: String, layer: Int, html: String, parentSymbol: Symbol, selfSymbol: Symbol, exit: Boolean) {
    // todo: debug用
    def structure = {
      s"url: $url\nlayer: $layer\nparent: $parentSymbol\nself: $selfSymbol\nexit: $exit"
    }
  }

  case class httpAccessSettings(url: String, timeoutInMs: Int, method: String, postData: PostData, reqHeader: ReqHeader, publisher: Symbol)

}
