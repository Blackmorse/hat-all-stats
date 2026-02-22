package com.blackmorse.hattid.web.zios

import com.blackmorse.hattid.web.models.web.*
import com.blackmorse.hattid.web.service.HattrickPeriod
import zio.http.Request
import zio.{IO, ZIO}

extension (req: Request)
  def intParam(name: String): IO[BadRequestError, Int] =
    ZIO.attempt(req.url.queryParams(name).mkString.toInt)
      .mapError(_ => BadRequestError(s"Invalid $name"))
      
  def longParam(name: String): IO[BadRequestError, Long] =
    ZIO.attempt(req.url.queryParams(name).mkString.toLong)
      .mapError(_ => BadRequestError(s"Invalid $name"))

  def doubleParam(name: String): IO[BadRequestError, Double] =
    ZIO.attempt(req.url.queryParams(name).mkString.toDouble)
      .mapError(_ => BadRequestError(s"Invalid $name"))
  
  def intParamOpt(name: String): IO[BadRequestError, Option[Int]] =
    if (req.url.queryParams(name).isEmpty) {
      ZIO.succeed(None)
    } else {
      intParam(name).map(Some(_))
    }

  def boolParam(name: String): IO[BadRequestError, Boolean] =
    ZIO.attempt(req.url.queryParams(name).mkString.toBoolean)
      .mapError(_ => BadRequestError(s"Invalid $name"))

  def boolParamWithDefault(name: String, default: Boolean): IO[BadRequestError, Boolean] =
    if (req.url.queryParams(name).isEmpty) {
      ZIO.succeed(default)
    } else {
      boolParam(name: String)
    }
    
  def stringParam(name: String): IO[BadRequestError, String] =
    ZIO.succeed(req.url.queryParams(name).mkString)

  def stringParamOpt(name: String): IO[Nothing, Option[String]] =
    if (req.url.queryParams(name).isEmpty) {
      ZIO.succeed(None)
    } else {
      ZIO.succeed(Some(req.url.queryParams(name).mkString))
    }

  def playersParameters(): IO[BadRequestError, PlayersParameters] = {
    for {
      role        <- req.stringParamOpt("role")
      nationality <- req.intParamOpt("nationality")
      minAge      <- req.intParamOpt("minAge")
      maxAge      <- req.intParamOpt("maxAge")
    } yield PlayersParameters(
      role = role,
      nationality = nationality,
      minAge = minAge,
      maxAge = maxAge
    )
  }

  def restStatisticsParameters(): IO[BadRequestError, RestStatisticsParameters] = {
    for {
      page <- req.intParam("page")
      season <- req.intParam("season")
      pageSize <- req.intParam("pageSize")
      sortBy = req.url.queryParams("sortBy").mkString
      sortDirection <- ZIO.succeed(req.url.queryParams("sortDirection").mkString)
        .flatMap {
          case "asc" => ZIO.succeed(Asc)
          case "desc" => ZIO.succeed(Desc)
          case _ => ZIO.fail(BadRequestError("Unknown sorting direction"))
        }
      statsType <- statsType()
    } yield RestStatisticsParameters(page = page,
      pageSize = pageSize,
      sortBy = sortBy,
      sortingDirection = sortDirection,
      statsType = statsType,
      season = season)
  }

  def statsType(): IO[BadRequestError, StatsType] = {
    val statType = req.url.queryParams("statType").mkString
    statType match {
      case "avg" => ZIO.succeed(Avg)
      case "max" => ZIO.succeed(Max)
      case "accumulate" => ZIO.succeed(Accumulate)
      case "statRound" =>
        for {
          statRoundNumber <- req.intParam("statRoundNumber")
        } yield Round(statRoundNumber)
      case _ => ZIO.fail(BadRequestError("Unable to parse statType"))
    }
  }
  
  def hattrickPeriod(): IO[BadRequestError, HattrickPeriod] = {
    val period = req.url.queryParams("period").mkString
    period match {
      case "round" => ZIO.succeed(com.blackmorse.hattid.web.service.Round)
      case "season" => ZIO.succeed(com.blackmorse.hattid.web.service.Season)
      case "weeks" => req.intParam("weeksNumber").map(com.blackmorse.hattid.web.service.Weeks(_))
      case _ => ZIO.fail(BadRequestError("Unknown period type. Available values: season, round, weeks"))
    }
  }


def restTableData[T](entities: List[T], pageSize: Int): RestTableData[T] = {
  val isLastPage = entities.size <= pageSize

  val entitiesNew = if (!isLastPage) entities.dropRight(1) else entities
  RestTableData(entitiesNew, isLastPage)
}
