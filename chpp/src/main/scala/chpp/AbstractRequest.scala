package chpp

import java.net.URLEncoder
import java.util.Date
import org.apache.commons.codec.binary.Base64

import java.text.SimpleDateFormat
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import scala.util.Random

case class ChppRequestData(uri: String, header: String)

abstract class AbstractRequest[Model](val file: String, val version: String, params: (String, Option[Any])*) {
  private val URL = "chpp.hattrick.org"
  private val API_ENDPOINT = "/chppxml.ashx"
  private val oauthSignatureMethod = "HMAC-SHA1"
  private val oauthVersion = "1.0"

  private def createParams(file: String, version: String, params: (String, Option[Any])*): Map[String, String] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    (Seq("file" -> file, "version" -> version) ++ params.filter(_._2.isDefined).map({
      case (key: String, Some(date: Date)) =>
        (key: String, dateFormat.format(date): String)
      case (key, Some(value)) =>
        (key, value.toString: String)
      case _ => throw new IllegalArgumentException("This should never happen")
    }))
      .toMap
  }

  def requestData(oauthTokens: OauthTokens): ChppRequestData = {
    val requestParams = createParams(file, version, params*)
    val url = API_ENDPOINT + "?" +
      requestParams.map { case (key, value) => s"$key=$value" }.mkString("&")

    val currentSeconds = System.currentTimeMillis() / 1000
    val oauthNonce = currentSeconds + new Random().nextInt()
    val oauthTimestamp = currentSeconds

    val oauthParameters = Map[String, String](
      "oauth_nonce" -> oauthNonce.toString,
      "oauth_timestamp" -> oauthTimestamp.toString,
      "oauth_consumer_key" -> oauthTokens.oauthCustomerKey,
      "oauth_token" -> oauthTokens.oauthToken,
      "oauth_signature_method" -> oauthSignatureMethod,
      "oauth_version" -> oauthVersion
    )

    val allParams = (oauthParameters ++ requestParams).toSeq.sortBy(_._1)
    val paramsStringEncoded = URLEncoder.encode(allParams.map { case (key, value) => s"$key=$value" }.mkString("&"), "UTF-8")

    val urlEncoded = URLEncoder.encode(s"https://$URL" + API_ENDPOINT, "UTF-8")
    val toSignString = s"GET&$urlEncoded&$paramsStringEncoded"


    val key = s"${URLEncoder.encode(oauthTokens.clientSecret, "UTF-8")}&${URLEncoder.encode(oauthTokens.tokenSecret, "UTF-8")}"

    val keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")

    mac.init(keySpec)
    val bytes = mac.doFinal(toSignString.getBytes("UTF-8"))
    val result = new String(Base64.encodeBase64(bytes)).replace("\r\n", "")

    val header = "OAuth " +
      (oauthParameters + ("oauth_signature" -> URLEncoder.encode(result, "UTF-8"))).map { case (key, value) => s"""$key="$value"""" }.mkString(", ");

    ChppRequestData(uri = s"https://$URL" + url,
      header = header)
  }

  def preprocessResponseBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
