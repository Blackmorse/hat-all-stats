package flows.http

import flows.AbstractHttpFlow
import models.chpp.search.Search
import requests.SearchRequest

object SearchFlow extends AbstractHttpFlow[SearchRequest, Search] {
  override def preprocessBody(body: String): String =
  body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
