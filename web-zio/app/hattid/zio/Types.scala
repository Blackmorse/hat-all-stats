package hattid.zio

import databases.ClickhousePool.ClickhousePool
import databases.dao.RestClickhouseDAO
import service.{ChppService, RestOverviewStatsService, SimilarMatchesService, TeamsService, TranslationsService}
import service.leagueinfo.LeagueInfoServiceZIO
import service.leagueunit.LeagueUnitCalculatorService
import webclients.{AuthConfig, ChppClient}
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
  RestOverviewStatsService &
  SimilarMatchesService

type CHPPServices = ChppClient & Client & AuthConfig & ChppService
type DBServices = ClickhousePool & RestClickhouseDAO