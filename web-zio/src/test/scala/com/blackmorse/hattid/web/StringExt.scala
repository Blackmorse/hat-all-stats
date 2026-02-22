package com.blackmorse.hattid.web

import zio.ZIO
import zio.*
import zio.test.Assertion.*
import zio.test.{test, *}

implicit class StringExt(v: String) {
  def normalize(): String =
    v.toLowerCase
      .replace(" ", "")
      .replace("\n", "")
    
  def normalizeEqualTo(other: String): ZIO[Any, Nothing, TestResult] =
    ZIO.succeed(v.normalize()).map(str => assert(str)(equalTo(other.stripMargin.normalize())))
}
