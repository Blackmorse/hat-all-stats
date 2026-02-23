package com.blackmorse.hattid.web.zios

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import com.blackmorse.hattid.web.service.leagueunit.LeagueUnitCalculatorService
import com.blackmorse.hattid.web.service.*
import com.blackmorse.hattid.web.service.cache.OverviewCache
import com.blackmorse.hattid.web.webclients.{AuthConfig, ChppClient}
import zio.ZPool
import zio.http.{Client, Server}

import java.sql.Connection

type HattidEnv = ChppClient &
  Client &
  AuthConfig &
  ChppService &
  ZPool[Nothing, Connection] &
  RestClickhouseDAO &
  Server & 
  LeagueInfoServiceZIO &
  LeagueUnitCalculatorService &
  TeamsService &
  TranslationsService &
  SimilarMatchesService &
  OverviewCache.CacheType

type CHPPServices = ChppClient & Client & AuthConfig & ChppService
type DBServices = ClickhousePool & RestClickhouseDAO