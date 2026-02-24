package com.blackmorse.hattid.web.databases

import zio.ZPool

import java.sql.Connection

object ClickhousePool {
  type ClickhousePool = ZPool[Nothing, Connection]
}
