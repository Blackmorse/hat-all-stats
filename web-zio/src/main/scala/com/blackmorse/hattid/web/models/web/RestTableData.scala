package com.blackmorse.hattid.web.models.web

import zio.json.*

case class RestTableData[T](entities: Seq[T],
                            isLastPage: Boolean)

object RestTableData {
  implicit def jsonEncoder[T](implicit nested: JsonEncoder[T]): JsonEncoder[RestTableData[T]] = DeriveJsonEncoder.gen[RestTableData[T]]
}