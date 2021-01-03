
import models.worlddetails.WorldDetails

import scala.xml.XML

object XmlTest extends App {

  //<?xml version="1.0" encoding="utf-8"?>
 val s =
   """
<HattrickData>
  <FileName>worlddetails.xml</FileName>
  <Version>1.8</Version>
  <UserID>4040806</UserID>
  <FetchedDate>2021-01-02 10:04:27</FetchedDate>
  <LeagueList>
    <League>
      <LeagueID>1</LeagueID>
      <LeagueName>Швеция</LeagueName>
      <Season>76</Season>
      <SeasonOffset>0</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Sverige</ShortName>
      <Continent>Europe</Continent>
      <ZoneName>Northern Europe</ZoneName>
      <EnglishName>Sweden</EnglishName>
      <Country Available=True>
        <CountryID>1</CountryID>
        <CountryName>Sverige</CountryName>
        <CurrencyName>kr</CurrencyName>
        <CurrencyRate>1</CurrencyRate>
        <CountryCode>SE</CountryCode>
        <DateFormat>YYYY-MM-DD</DateFormat>
        <TimeFormat>hh:mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>1</CupID>
          <CupName>Sverigecupen</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>512</CupID>
          <CupName>Smaragdcupen</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>641</CupID>
          <CupName>Rubincupen</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>770</CupID>
          <CupName>Safircupen</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>899</CupID>
          <CupName>Tröstcupen</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1328</CupID>
          <CupName>Division VI-cupen</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>0</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1341</CupID>
          <CupName>Smaragdcupen Division VI</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>0</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1354</CupID>
          <CupName>Rubincupen Division VI </CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>0</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1367</CupID>
          <CupName>Safircupen Division VI</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>0</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1380</CupID>
          <CupName>Tröstcupen Division VI</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>0</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3000</NationalTeamId>
      <U20TeamId>3041</U20TeamId>
      <ActiveTeams>7457</ActiveTeams>
      <ActiveUsers>7157</ActiveUsers>
      <WaitingUsers>4</WaitingUsers>
      <TrainingDate>2021-01-07 22:00:00</TrainingDate>
      <EconomyDate>2021-01-03 00:10:00</EconomyDate>
      <CupMatchDate>2020-12-16 19:00:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 10:00:00</SeriesMatchDate>
      <NumberOfLevels>6</NumberOfLevels>
    </League>
    <League>
      <LeagueID>2</LeagueID>
      <LeagueName>Англия</LeagueName>
      <Season>76</Season>
      <SeasonOffset>0</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>England</ShortName>
      <Continent>Europe</Continent>
      <ZoneName>Western Europe</ZoneName>
      <EnglishName>England</EnglishName>
      <Country Available=True>
        <CountryID>2</CountryID>
        <CountryName>England</CountryName>
        <CurrencyName>£</CurrencyName>
        <CurrencyRate>15</CurrencyRate>
        <CountryCode>GB</CountryCode>
        <DateFormat>DD/MM/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>3</CupID>
          <CupName>English Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>513</CupID>
          <CupName>Craig Wozniak Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>642</CupID>
          <CupName>George Carey Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>771</CupID>
          <CupName>Conspirator Memorial Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>10</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>900</CupID>
          <CupName>TomDHT Memorial Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3001</NationalTeamId>
      <U20TeamId>3042</U20TeamId>
      <ActiveTeams>3405</ActiveTeams>
      <ActiveUsers>3067</ActiveUsers>
      <WaitingUsers>7</WaitingUsers>
      <TrainingDate>2021-01-07 21:00:00</TrainingDate>
      <EconomyDate>2021-01-08 21:00:00</EconomyDate>
      <CupMatchDate>2020-12-08 21:00:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 18:00:00</SeriesMatchDate>
      <NumberOfLevels>6</NumberOfLevels>
    </League>
    <League>
      <LeagueID>3</LeagueID>
      <LeagueName>Германия</LeagueName>
      <Season>76</Season>
      <SeasonOffset>0</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Deutschland</ShortName>
      <Continent>Europe</Continent>
      <ZoneName>Central Europe</ZoneName>
      <EnglishName>Germany</EnglishName>
      <Country Available=True>
        <CountryID>3</CountryID>
        <CountryName>Deutschland</CountryName>
        <CurrencyName>€</CurrencyName>
        <CurrencyRate>10</CurrencyRate>
        <CountryCode>DE</CountryCode>
        <DateFormat>DD.MM.YYYY</DateFormat>
        <TimeFormat>hh:mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>4</CupID>
          <CupName>Deutschland-Pokal</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>14</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>514</CupID>
          <CupName>Smaragd-Pokal</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>14</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>643</CupID>
          <CupName>Rubin-Pokal</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>772</CupID>
          <CupName>Saphir-Pokal</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>901</CupID>
          <CupName>Goldene-Ananas-Pokal</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1330</CupID>
          <CupName>Liga-Pokal (Liga VII)</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1343</CupID>
          <CupName>Smaragd-Pokal (Liga VII)</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>13</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1356</CupID>
          <CupName>Rubin-Pokal (Liga VII)</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1369</CupID>
          <CupName>Saphir-Pokal (Liga VII)</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1382</CupID>
          <CupName>Goldene-Ananas-Pokal (Liga VII)</CupName>
          <CupLeagueLevel>7</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>12</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1393</CupID>
          <CupName>Liga-Pokal (Liga VIII)</CupName>
          <CupLeagueLevel>8</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>10</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1399</CupID>
          <CupName>Smaragd-Pokal (Liga VIII)</CupName>
          <CupLeagueLevel>8</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>10</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1405</CupID>
          <CupName>Rubin-Pokal (Liga VIII)</CupName>
          <CupLeagueLevel>8</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>9</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1411</CupID>
          <CupName>Saphir-Pokal (Liga VIII)</CupName>
          <CupLeagueLevel>8</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1417</CupID>
          <CupName>Goldene-Ananas-Pokal (Liga VIII)</CupName>
          <CupLeagueLevel>8</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>9</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3002</NationalTeamId>
      <U20TeamId>3043</U20TeamId>
      <ActiveTeams>17634</ActiveTeams>
      <ActiveUsers>17042</ActiveUsers>
      <WaitingUsers>10</WaitingUsers>
      <TrainingDate>2021-01-08 00:30:00</TrainingDate>
      <EconomyDate>2021-01-09 00:10:00</EconomyDate>
      <CupMatchDate>2020-12-22 18:00:00</CupMatchDate>
      <SeriesMatchDate>2021-01-02 18:00:00</SeriesMatchDate>
      <NumberOfLevels>7</NumberOfLevels>
    </League>

    <League>
      <LeagueID>107</LeagueID>
      <LeagueName>Гватемала</LeagueName>
      <Season>52</Season>
      <SeasonOffset>-24</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Guatemala</ShortName>
      <Continent>North America</Continent>
      <ZoneName>North America</ZoneName>
      <EnglishName>Guatemala</EnglishName>
      <Country Available=True>
        <CountryID>102</CountryID>
        <CountryName>Guatemala</CountryName>
        <CurrencyName>Q</CurrencyName>
        <CurrencyRate>10</CurrencyRate>
        <CountryCode>GT</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>167</CupID>
          <CupName>Copa Gran Jaguar</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>604</CupID>
          <CupName>Copa Esmeralda Eterna Primavera</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>733</CupID>
          <CupName>Copa Rubí Marimba</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>862</CupID>
          <CupName>Copa Zafiro Maya</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>991</CupID>
          <CupName>Copa Consolación DxHT</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3184</NationalTeamId>
      <U20TeamId>3185</U20TeamId>
      <ActiveTeams>152</ActiveTeams>
      <ActiveUsers>87</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-08 04:30:00</TrainingDate>
      <EconomyDate>2021-01-03 03:20:00</EconomyDate>
      <CupMatchDate>2020-11-12 01:10:00</CupMatchDate>
      <SeriesMatchDate>2021-01-04 01:10:00</SeriesMatchDate>
      <NumberOfLevels>4</NumberOfLevels>
    </League>
    <League>
      <LeagueID>110</LeagueID>
      <LeagueName>Тринидад и Тобаго</LeagueName>
      <Season>51</Season>
      <SeasonOffset>-25</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Trinidad/T.</ShortName>
      <Continent>North America</Continent>
      <ZoneName>North America</ZoneName>
      <EnglishName>Trinidad &amp; Tobago</EnglishName>
      <Country Available=True>
        <CountryID>105</CountryID>
        <CountryName>Trinidad &amp; Tobago</CountryName>
        <CurrencyName>TTD</CurrencyName>
        <CurrencyRate>1</CurrencyRate>
        <CountryCode>TT</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>170</CupID>
          <CupName>The Pro Bowl</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>605</CupID>
          <CupName>Trinidad &amp; Tobago Emerald Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>734</CupID>
          <CupName>Trinidad &amp; Tobago Ruby Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>863</CupID>
          <CupName>Trinidad &amp; Tobago Sapphire Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>992</CupID>
          <CupName>Trinidad &amp; Tobago Consolation Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3194</NationalTeamId>
      <U20TeamId>3195</U20TeamId>
      <ActiveTeams>104</ActiveTeams>
      <ActiveUsers>19</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-08 02:15:00</TrainingDate>
      <EconomyDate>2021-01-03 03:44:00</EconomyDate>
      <CupMatchDate>2020-11-04 22:20:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 22:20:00</SeriesMatchDate>
      <NumberOfLevels>3</NumberOfLevels>
    </League>
    <League>
      <LeagueID>111</LeagueID>
      <LeagueName>Никарагуа</LeagueName>
      <Season>51</Season>
      <SeasonOffset>-25</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Nicaragua</ShortName>
      <Continent>North America</Continent>
      <ZoneName>North America</ZoneName>
      <EnglishName>Nicaragua</EnglishName>
      <Country Available=True>
        <CountryID>121</CountryID>
        <CountryName>Nicaragua</CountryName>
        <CurrencyName>NIO</CurrencyName>
        <CurrencyRate>0,5</CurrencyRate>
        <CountryCode>NI</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>171</CupID>
          <CupName>Copa Nacional Rubén Darío</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>606</CupID>
          <CupName>Copa Esmeralda Cocibolca</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>735</CupID>
          <CupName>Copa Rubí Xolotlán</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>864</CupID>
          <CupName>Copa Zafiro Guardabarranco</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>993</CupID>
          <CupName>Copa Consolación Sacuanjoche</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3190</NationalTeamId>
      <U20TeamId>3191</U20TeamId>
      <ActiveTeams>116</ActiveTeams>
      <ActiveUsers>30</ActiveUsers>
      <WaitingUsers>1</WaitingUsers>
      <TrainingDate>2021-01-08 01:15:00</TrainingDate>
      <EconomyDate>2021-01-03 03:26:00</EconomyDate>
      <CupMatchDate>2020-11-04 23:40:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 23:40:00</SeriesMatchDate>
      <NumberOfLevels>3</NumberOfLevels>
    </League>
    <League>
      <LeagueID>112</LeagueID>
      <LeagueName>Казахстан</LeagueName>
      <Season>51</Season>
      <SeasonOffset>-25</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Kazakhstan</ShortName>
      <Continent>Asia</Continent>
      <ZoneName>Asia,  Africa and Oceania</ZoneName>
      <EnglishName>Kazakhstan</EnglishName>
      <Country Available=True>
        <CountryID>122</CountryID>
        <CountryName>Kazakhstan</CountryName>
        <CurrencyName>tenge</CurrencyName>
        <CurrencyRate>0,1</CurrencyRate>
        <CountryCode>KZ</CountryCode>
        <DateFormat>DD.MM.YYYY</DateFormat>
        <TimeFormat>hh:mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>172</CupID>
          <CupName>Kazakhstan Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>607</CupID>
          <CupName>Kazakhstan Emerald Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>736</CupID>
          <CupName>Kazakhstan Ruby Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>865</CupID>
          <CupName>Kazakhstan Sapphire Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>994</CupID>
          <CupName>Kazakhstan Consolation Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3188</NationalTeamId>
      <U20TeamId>3189</U20TeamId>
      <ActiveTeams>119</ActiveTeams>
      <ActiveUsers>87</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-08 06:04:00</TrainingDate>
      <EconomyDate>2021-01-09 02:24:00</EconomyDate>
      <CupMatchDate>2020-11-04 12:10:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 08:00:00</SeriesMatchDate>
      <NumberOfLevels>4</NumberOfLevels>
    </League>
    <League>
      <LeagueID>113</LeagueID>
      <LeagueName>Суринам</LeagueName>
      <Season>51</Season>
      <SeasonOffset>-25</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Suriname</ShortName>
      <Continent>South America</Continent>
      <ZoneName>South America</ZoneName>
      <EnglishName>Suriname</EnglishName>
      <Country Available=True>
        <CountryID>123</CountryID>
        <CountryName>Suriname</CountryName>
        <CurrencyName>SRD</CurrencyName>
        <CurrencyRate>5</CurrencyRate>
        <CountryCode>SR</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>173</CupID>
          <CupName>SVB Beker</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>608</CupID>
          <CupName>Smaragden Beker</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>737</CupID>
          <CupName>Robijnen Beker</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>866</CupID>
          <CupName>Saffieren Beker</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>995</CupID>
          <CupName>Troosttrofee</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3192</NationalTeamId>
      <U20TeamId>3193</U20TeamId>
      <ActiveTeams>94</ActiveTeams>
      <ActiveUsers>9</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-07 23:30:00</TrainingDate>
      <EconomyDate>2021-01-03 03:37:00</EconomyDate>
      <CupMatchDate>2020-11-04 23:10:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 23:10:00</SeriesMatchDate>
      <NumberOfLevels>3</NumberOfLevels>
    </League>
    <League>
      <LeagueID>117</LeagueID>
      <LeagueName>Лихтенштейн</LeagueName>
      <Season>50</Season>
      <SeasonOffset>-26</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Liechtenst.</ShortName>
      <Continent>Europe</Continent>
      <ZoneName>Central Europe</ZoneName>
      <EnglishName>Liechtenstein</EnglishName>
      <Country Available=True>
        <CountryID>125</CountryID>
        <CountryName>Liechtenstein</CountryName>
        <CurrencyName>CHF</CurrencyName>
        <CurrencyRate>5</CurrencyRate>
        <CountryCode>LI</CountryCode>
        <DateFormat>DD.MM.YYYY</DateFormat>
        <TimeFormat>hh:mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>177</CupID>
          <CupName>Liechtenstein Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>609</CupID>
          <CupName>Hochegg-Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>738</CupID>
          <CupName>Täli-Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>867</CupID>
          <CupName>Sareis-Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>996</CupID>
          <CupName>Schneeflucht-Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3204</NationalTeamId>
      <U20TeamId>3205</U20TeamId>
      <ActiveTeams>234</ActiveTeams>
      <ActiveUsers>58</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-07 05:18:00</TrainingDate>
      <EconomyDate>2021-01-09 01:59:00</EconomyDate>
      <CupMatchDate>2020-11-10 19:50:00</CupMatchDate>
      <SeriesMatchDate>2021-01-02 19:50:00</SeriesMatchDate>
      <NumberOfLevels>4</NumberOfLevels>
    </League>
    <League>
      <LeagueID>118</LeagueID>
      <LeagueName>Алжир</LeagueName>
      <Season>50</Season>
      <SeasonOffset>-26</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Algérie</ShortName>
      <Continent>Africa</Continent>
      <ZoneName>Asia,  Africa and Oceania</ZoneName>
      <EnglishName>Algeria</EnglishName>
      <Country Available=True>
        <CountryID>126</CountryID>
        <CountryName>Algérie</CountryName>
        <CurrencyName>DZD</CurrencyName>
        <CurrencyRate>0,1</CurrencyRate>
        <CountryCode>DZ</CountryCode>
        <DateFormat>DD-MM-YYYY</DateFormat>
        <TimeFormat>hh:mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>178</CupID>
          <CupName>Coupe d'Algérie</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>610</CupID>
          <CupName>Trophée Émeraude</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>739</CupID>
          <CupName>Trophée Rubis</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>868</CupID>
          <CupName>Trophée Saphir</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>997</CupID>
          <CupName>Consolante</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>3196</NationalTeamId>
      <U20TeamId>3197</U20TeamId>
      <ActiveTeams>87</ActiveTeams>
      <ActiveUsers>52</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-07 13:11:00</TrainingDate>
      <EconomyDate>2021-01-03 01:12:00</EconomyDate>
      <CupMatchDate>2020-11-04 20:10:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 19:25:00</SeriesMatchDate>
      <NumberOfLevels>5</NumberOfLevels>
    </League>
    <League>
      <LeagueID>163</LeagueID>
      <LeagueName>Сан-Марино</LeagueName>
      <Season>1</Season>
      <SeasonOffset>-75</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>San Marino</ShortName>
      <Continent>Europe</Continent>
      <ZoneName>Southwestern Europe</ZoneName>
      <EnglishName>San Marino</EnglishName>
      <Country Available=True>
        <CountryID>191</CountryID>
        <CountryName>San Marino</CountryName>
        <CurrencyName>€</CurrencyName>
        <CurrencyRate>10</CurrencyRate>
        <CountryCode>SM</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>1503</CupID>
          <CupName>Coppa Titano</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>9</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1504</CupID>
          <CupName>Coppa Cesta</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>9</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1505</CupID>
          <CupName>Coppa Guaita</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1506</CupID>
          <CupName>Coppa Montale</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1507</CupID>
          <CupName>Coppa Crescentini</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>0</NationalTeamId>
      <U20TeamId>3294</U20TeamId>
      <ActiveTeams>371</ActiveTeams>
      <ActiveUsers>31</ActiveUsers>
      <WaitingUsers>2</WaitingUsers>
      <TrainingDate>2021-01-08 04:25:00</TrainingDate>
      <EconomyDate>2021-01-09 02:00:00</EconomyDate>
      <CupMatchDate>2020-11-18 17:15:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 19:15:00</SeriesMatchDate>
      <NumberOfLevels>4</NumberOfLevels>
    </League>
    <League>
      <LeagueID>164</LeagueID>
      <LeagueName>Гаити</LeagueName>
      <Season>1</Season>
      <SeasonOffset>-75</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Haiti</ShortName>
      <Continent>North America</Continent>
      <ZoneName>North America</ZoneName>
      <EnglishName>Haiti</EnglishName>
      <Country Available=True>
        <CountryID>188</CountryID>
        <CountryName>Haiti</CountryName>
        <CurrencyName>gourde</CurrencyName>
        <CurrencyRate>0,1</CurrencyRate>
        <CountryCode>HT</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>1508</CupID>
          <CupName>Haiti Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1509</CupID>
          <CupName>Haiti Emerald Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1510</CupID>
          <CupName>Haiti Ruby Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1511</CupID>
          <CupName>Haiti Sapphire Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>5</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1512</CupID>
          <CupName>Haiti Consolation Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>0</NationalTeamId>
      <U20TeamId>3295</U20TeamId>
      <ActiveTeams>91</ActiveTeams>
      <ActiveUsers>7</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-08 00:50:00</TrainingDate>
      <EconomyDate>2021-01-03 03:25:00</EconomyDate>
      <CupMatchDate>2020-11-04 23:15:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 23:15:00</SeriesMatchDate>
      <NumberOfLevels>3</NumberOfLevels>
    </League>
    <League>
      <LeagueID>165</LeagueID>
      <LeagueName>Пуэрто-Рико</LeagueName>
      <Season>1</Season>
      <SeasonOffset>-75</SeasonOffset>
      <MatchRound>15</MatchRound>
      <ShortName>Puerto Rico</ShortName>
      <Continent>North America</Continent>
      <ZoneName>North America</ZoneName>
      <EnglishName>Puerto Rico</EnglishName>
      <Country Available=True>
        <CountryID>190</CountryID>
        <CountryName>Puerto Rico</CountryName>
        <CurrencyName>$</CurrencyName>
        <CurrencyRate>10</CurrencyRate>
        <CountryCode>PR</CountryCode>
        <DateFormat>MM/DD/YYYY</DateFormat>
        <TimeFormat>hh.mm</TimeFormat>
      </Country>
      <Cups>
        <Cup>
          <CupID>1513</CupID>
          <CupName>Puerto Rico Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1514</CupID>
          <CupName>Puerto Rico Emerald Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>8</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1515</CupID>
          <CupName>Puerto Rico Ruby Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1516</CupID>
          <CupName>Puerto Rico Sapphire Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>6</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1517</CupID>
          <CupName>Puerto Rico Consolation Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>7</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>0</NationalTeamId>
      <U20TeamId>3296</U20TeamId>
      <ActiveTeams>302</ActiveTeams>
      <ActiveUsers>10</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-08 02:15:00</TrainingDate>
      <EconomyDate>2021-01-03 03:44:00</EconomyDate>
      <CupMatchDate>2020-11-11 22:20:00</CupMatchDate>
      <SeriesMatchDate>2021-01-03 22:20:00</SeriesMatchDate>
      <NumberOfLevels>4</NumberOfLevels>
    </League>
    <League>
      <LeagueID>1000</LeagueID>
      <LeagueName>Hattrick International</LeagueName>
      <Season>13</Season>
      <SeasonOffset>-63</SeasonOffset>
      <MatchRound>16</MatchRound>
      <ShortName>International</ShortName>
      <Continent>World</Continent>
      <ZoneName>World</ZoneName>
      <EnglishName>Hattrick International</EnglishName>
      <Country Available=False />
      <Cups>
        <Cup>
          <CupID>1433</CupID>
          <CupName>Hattrick International Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>1</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1434</CupID>
          <CupName>Hattrick International Emerald Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>11</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1435</CupID>
          <CupName>Hattrick International Ruby Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>2</CupLevelIndex>
          <MatchRound>10</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1436</CupID>
          <CupName>Hattrick International Sapphire Challenger Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>2</CupLevel>
          <CupLevelIndex>3</CupLevelIndex>
          <MatchRound>9</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
        <Cup>
          <CupID>1437</CupID>
          <CupName>Hattrick International Consolation Cup</CupName>
          <CupLeagueLevel>0</CupLeagueLevel>
          <CupLevel>3</CupLevel>
          <CupLevelIndex>1</CupLevelIndex>
          <MatchRound>10</MatchRound>
          <MatchRoundsLeft>0</MatchRoundsLeft>
        </Cup>
      </Cups>
      <NationalTeamId>0</NationalTeamId>
      <U20TeamId>0</U20TeamId>
      <ActiveTeams>1946</ActiveTeams>
      <ActiveUsers>0</ActiveUsers>
      <WaitingUsers>0</WaitingUsers>
      <TrainingDate>2021-01-07 10:15:00</TrainingDate>
      <EconomyDate>2021-01-08 13:00:00</EconomyDate>
      <CupMatchDate>2020-12-01 16:00:00</CupMatchDate>
      <SeriesMatchDate>2021-01-08 17:00:00</SeriesMatchDate>
      <NumberOfLevels>6</NumberOfLevels>
    </League>
  </LeagueList>
</HattrickData>


     """

  import com.lucidchart.open.xtract.{XmlReader, __}
  import com.lucidchart.open.xtract.XmlReader._

  import cats.syntax.all._

  case class HattrickData(fileName: String, version: Double, users: Seq[String])

  object HattrickData {
    implicit val reader: XmlReader[HattrickData]  =(
      (__ \ "FileName").read[String],
      (__ \ "Version").read[Double],
      (__ \ "UserID").read(seq[String])
    ).mapN(apply _)
  }

  private val elem = XML.loadString(s.replace("<Country Available=False />", "")
    .replace("Available=True", "Available=\"True\""))
  val v = XmlReader.of[WorldDetails].read(elem)

//  XmlReader.of[WorldDetails].read

  println(v)
}
