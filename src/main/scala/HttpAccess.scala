
import java.io._
import java.net.URL
import java.net.HttpURLConnection
import java.net.URLConnection
import HttpData._

sealed trait httpAccessT {
  def disconnect: Unit
}

object HttpAccess {
  def apply(url: String, timeoutInMs: Int, method: String, postData: PostData, reqHeader: ReqHeader):HttpAccess = {
    new HttpAccess(url, timeoutInMs, method, postData, reqHeader)
  }
}

class HttpAccess(url: String, timeoutInMs: Int, method: String, postData: PostData, reqHeader: ReqHeader) extends httpAccessT{
  private val urlObj = new URL(url)
  private val urlConnection = urlObj.openConnection()
  private val httpURLConnection = urlConnection.asInstanceOf[HttpURLConnection]
  httpURLConnection.setConnectTimeout(timeoutInMs)
  httpURLConnection.setRequestMethod(method.toUpperCase)

  if(reqHeader.isContain) {
    reqHeader.getValue.foreach(kv => {
      val (k, v) = kv
      httpURLConnection.setRequestProperty(k, v)
    })
  }

  if(postData.isContain){
    httpURLConnection.setDoOutput(true)
    val printStream = new PrintStream(httpURLConnection.getOutputStream)
    printStream.print(postData.getValue)
    printStream.close()
  }

  lazy val responseByte: Array[Byte] = {
    val inputStream = urlConnection.getInputStream
    Stream.continually(inputStream.read.byteValue).takeWhile(-1 !=).toArray
  }

  lazy val responseCode: Int = {
    httpURLConnection.getResponseCode
  }

  def disconnect:Unit = {
    httpURLConnection.disconnect()
  }
}
