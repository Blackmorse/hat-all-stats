package chpp


import org.apache.pekko.http.scaladsl.model.headers.RawHeader
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import org.apache.pekko.util.ByteString
import org.apache.commons.codec.binary.Base64

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import scala.util.Random

case class  RequestCreator()

object RequestCreator {
  private val URL = "chpp.hattrick.org"
  private val API_ENDPOINT = "/chppxml.ashx"

  def create(requestParams: Map[String, String])
            (implicit oauthTokens: OauthTokens): HttpRequest = {
    val url = API_ENDPOINT + "?" +
      requestParams.map{case(key, value) => s"$key=$value"}.mkString("&")

    val oauthNonce = System.currentTimeMillis() / 1000 + new Random().nextInt()
    val oauthTimestamp = System.currentTimeMillis() / 1000
    val oauthSignatureMethod = "HMAC-SHA1"
    val oauthVersion = "1.0"

    val oauthParameters = Map[String, String](
      "oauth_nonce" -> oauthNonce.toString,
      "oauth_timestamp" -> oauthTimestamp.toString,
      "oauth_consumer_key" -> oauthTokens.oauthCustomerKey,
      "oauth_token" -> oauthTokens.oauthToken,
      "oauth_signature_method" -> oauthSignatureMethod,
      "oauth_version" -> oauthVersion
    )

    val allParams = (oauthParameters ++ requestParams).toSeq.sortBy(_._1)

    val urlEncoded = URLEncoder.encode(s"https://$URL" + API_ENDPOINT, "UTF-8")
    val paramsStringEncoded = URLEncoder.encode(allParams.map{case (key, value) => s"$key=$value"}.mkString("&"), "UTF-8")

    val key = s"${URLEncoder.encode(oauthTokens.clientSecret, "UTF-8")}&${URLEncoder.encode(oauthTokens.tokenSecret, "UTF-8")}"

    val toSignString = s"GET&$urlEncoded&$paramsStringEncoded"

    val keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(keySpec)
    val bytes = mac.doFinal(toSignString.getBytes("UTF-8"))
    val result = new String(Base64.encodeBase64(bytes)).replace("\r\n", "")

    val header = "OAuth " +
      (oauthParameters + ("oauth_signature" -> URLEncoder.encode(result, "UTF-8"))).map{case(key, value) => s"""$key="$value""""}.mkString(", ");

    HttpRequest(uri = s"https://$URL" + url,
      entity = HttpEntity.Strict(ContentTypes.`text/xml(UTF-8)`, data = ByteString.empty))
      .withHeaders(
        RawHeader("Authorization", header),
      )
  }

  def params(file: String, version: String, params: (String, Option[Any])*): Map[String, String] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    (Seq("file" -> file, "version" -> version) ++ params.filter(_._2.isDefined).map({
      case (key: String, Some(date: Date)) =>
        (key: String, dateFormat.format(date): String)
      case (key, value) =>
        (key, value.get.toString: String)
    }))
      .toMap
  }
}
