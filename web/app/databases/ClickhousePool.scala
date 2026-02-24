package databases

import zio.ZPool

import java.sql.Connection

object ClickhousePool {
  type ClickhousePool = ZPool[Nothing, Connection]
}
