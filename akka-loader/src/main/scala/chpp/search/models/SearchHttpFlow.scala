package chpp.search.models

import chpp.search.SearchRequest
import flows.AbstractHttpFlow

object SearchHttpFlow extends AbstractHttpFlow[SearchRequest, Search] {
  override def preprocessBody(body: String): String =
  body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
