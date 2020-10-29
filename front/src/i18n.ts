
import i18n from "i18next";
import { initReactI18next } from "react-i18next";


// the translations
// (tip move them in a JSON file and import them)
const resources = {
  en: {
    translation: {
      "menu.statistics": "Statistics",
      "menu.best_teams": "Team ratings",
      "menu.best_league_units": "League ratings",
      "menu.player_stats": "Player season stats",
      "menu.team_state": "Team round stats",
      "menu.player_state": "Player round stats",
      "menu.formal_team_stats": "Team season stats",
      "menu.matches": "Matches",
      "menu.team_rankings": "Team Rankings",
      "menu.search": "Search",
      "menu.team_search": "Team search",
      "menu.team_overview": "Overview",
      "menu.player_goal_games": "Goals/Games",
      "menu.player_cards": "Yellow/Red cards",
      "menu.player_salary_tsi": "Salary/TSI",
      "menu.ratings": "Ratings",
      "menu.injuries": "Injuries",
      "menu.teams": "Teams",
      "menu.players": "Players",
      "menu.leagues": "Leagues",
      "menu.age_injury": "Age/Injuries",
      "menu.goals_points": "Points/goals",
      "menu.fanclub_flags": "Fanclub/flags",
      "menu.streak_trophies": "Winning streaks/Trophies",

      //Table
      "table.position_abbr": "P",
      "table.position": "Position",
      "table.league": "League",
      "table.hatstats": "HatStats",
      "table.midfield": "Midfield",
      "table.defense": "Defense",
      "table.attack": "Attack",
      "table.team": "Team",
      "table.win_abbr": "W",
      "table.win": "Won",
      "table.lose_abbr": "L",
      "table.lose": "Lost",
      "table.draw_abbr": "D",
      "table.draw": "Draw",
      "table.goals_for_abbr": "GF",
      "table.goals_for": "Goals For",
      "table.goals_against_abbr": "GA",
      "table.goals_against": "Goals Against",
      "table.points_abbr": "Pts",
      "table.points": "Points",
      "table.player": "Player",
      "table.age": "Age",
      "table.tsi": "TSI",
      "table.salary": "Salary",
      "table.rating_abbr": "Rat",
      "table.rating": "Rating",
      "table.rating_end_of_match_abbr": "REM",
      "table.rating_end_of_match": "Rating End Of Match",
      "table.injury_abbr": "Inj",
      "table.injury": "Injury",
      "table.red_cards_abbr": "RC",
      "table.red_cards": "Red Cards",
      "table.yellow_cards_abbr": "YC",
      "table.yellow_cards": "Yellow Cards",
      "table.games": "Games",
      "table.games_abbr": "G",
      "table.minutes_abbr": "Min",
      "table.minutes": "Played Minutes",
      "table.scored_abbr": "Sc",
      "table.scored": "Scored",
      "table.minutes_per_goal_abbr": "MPG",
      "table.minutes_per_goal": "Minutes Per Goal",
      "table.total_injury_weeks_abbr": "InjW",
      "table.total_injury_weeks": "Total Injury Weeks",
      "table.total_injury_number_abbr": "InjN",
      "table.total_injury_number": "Total Injury Number",
      "table.average_age_abbr": "AA",
      "table.average_age": "Average age",
      "table.power_rating": "Power rating",
      "table.goals_difference": "+/-",
      "table.fanclub_size": "Fanclub",
      "table.home_flags": "Home flags",
      "table.away_flags": "Away flags",
      "table.all_flags": "All flags",
      "table.trophies": "Trophies",
      "table.victories": "Victories",
      "table.undefeated": "Undefeated",

      //team_rankings
      "team_rankings.ranking": "Ranking at ",

      //matches
      "matches.played_matches": "Played matches",
      "matches.upcoming_matches": "Upcoming matches",
      "matches.result": "Result",
      "matches.home_team": "Home team",
      "matches.away_team": "Away team",
      "matches.ratings": "Ratings",
      "matches.defense_lmr": "Defense L/M/R",
      "matches.midfield": "Midfield",
      "matches.attack_lmr": "Attach L/M/R",
      "matches.summary": "Summary",
      "matches.date": "Date",
      "matches.formation": "Formation",
      "matches.hatstats": "HatStats",

      //filter
      "filter.page_size": "Page:",
      "filter.sorting_direction": "Sorting",
      "filter.asc": "ascending",
      "filter.desc": "descending",
      "filter.season": "Season:",
      "filter.round": "Round:",
      "filter.sort_by": "Sort By:",

      //bot
      "bot.header": "Oops, this is a bot",
      "bot.description": "At the current point this team seems to be a bot. Statistics for bots is not supported :(",

      //error
      "error.header": "Error",
      "error.description": "Seems like page doesn't exists or it is internal server error. Feel free to contact me (HT login: Blackmorse)",
      "error.empty_field": "Required field",

      //promotions
      "menu.promotions": "Promotions and qualifiers",
      "promotions.auto_promotions": "Auto-promotions",
      "promotions.qualifications": "Qualifications",

      //cookies
      "cookies.header": "This site uses cookies.",
      "cookies.content": "We inform you that this site uses own cookies to improve your user experience. By continuing to browse this website, you declare to accept the use of cookies.",
      "cookies.accept": "Accept",

      //overview
      "overview.world_overview": "World overview",
      "overview.surprising_matches": "Surprising matches",
      "overview.top_salary_teams": "Teams with highest salary",
      "overview.top_matches": "Top matches",
      "overview.top_teams": "Top teams",
      "overview.top_salary_players": "Players with highest salary",
      "overview.top_rating_players": "Players with highest rating",
      "overview.average_hatstats": "Average HatStats:",
      "overview.average_spectators": "Average spectator's number:",
      "overview.average_team_goals": "Average goals by team:",
      "overview.average_team_age": "Average team age:",
      "overview.average_team_salary": "Average team salary:",
      "overview.average_team_stars": "Average team stars:",
      "overview.formations": "Formations",
      "overview.country": "Country",
      "overview.goals": "Goals",
      "overview.number_of_teams": "Number of teams:",
      "overview.number_of_players": "Number of players:",
      "overview.number_of_goals": "Number of goals:",
      "overview.number_of_injuried": "Number of injuried players:",
      "overview.number_of_yellow": "Number of yellow cards:",
      "overview.number_of_red": "Number of red cards:",
      "overview.numbers": "Number",
      "overview.averages": "Averages",

      "error.loading": "Error while loading..."
    }
  },
  ru: {
    translation: {
      //Menu
      "menu.statistics": "Статистика",
      "menu.best_teams": "Рейтинги команд",
      "menu.best_league_units": "Рейтинг лиг",
      "menu.player_stats": "Статистика игроков за сезон",
      "menu.team_state": "Показатели команд в туре",
      "menu.player_state": "Показатели игроков",
      "menu.formal_team_stats": "Турнирные показатели команд",
      "menu.matches": "Матчи",
      "menu.team_rankings": "Положение команды",
      "menu.search": "Поиск",
      "menu.team_search": "Поиск команд",
      "menu.team_overview": "Обзор",
      "menu.player_goal_games": "Игры/Голы",
      "menu.player_cards": "Желтые/Красные карточки",
      "menu.player_salary_tsi": "Зарплата/TSI",
      "menu.ratings": "Рейтинги",
      "menu.injuries": "Травмы",
      "menu.teams": "Команды",
      "menu.players": "Игроки",
      "menu.leagues": "Лиги",
      "menu.age_injury": "Возраст/Травмы",
      "menu.goals_points": "Очки/голы",
      "menu.fanclub_flags": "Фанклуб/флаги",
      "menu.streak_trophies": "Победные серии/Трофеи",

      //Table
      "table.position_abbr": "П",
      "table.position": "Позиция",
      "table.league": "Лига",
      "table.hatstats": "HatStats",
      "table.midfield": "Полузащита",
      "table.defense": "Защита",
      "table.attack": "Атака",
      "table.team": "Команда",
      "table.win_abbr": "П",
      "table.win": "Победы",
      "table.lose_abbr": "Пр",
      "table.lose": "Проигрыши",
      "table.draw_abbr": "Н",
      "table.draw": "Ничьи",
      "table.goals_for_abbr": "З",
      "table.goals_for": "Забито",
      "table.goals_against_abbr": "Проп",
      "table.goals_against": "Пропущено",
      "table.points_abbr": "О",
      "table.points": "Очки",
      "table.player": "Игрок",
      "table.age": "Возраст",
      "table.tsi": "TSI",
      "table.salary": "Зарплата",
      "table.rating_abbr": "Рейт",
      "table.rating": "Рейтинг",
      "table.rating_end_of_match_abbr": "РКМ",
      "table.rating_end_of_match": "Рейтинг на Конец Матча",
      "table.injury_abbr": "Трм",
      "table.injury": "Травма",
      "table.red_cards_abbr": "КК",
      "table.red_cards": "Красные Карточки",
      "table.yellow_cards_abbr": "ЖК",
      "table.yellow_cards": "Желтые Карточки",
      "table.games": "Игры",
      "table.games_abbr": "И",
      "table.minutes_abbr": "Мин",
      "table.minutes": "Сыгранные минуты",
      "table.scored_abbr": "Заб",
      "table.scored": "Забито",
      "table.minutes_per_goal_abbr": "М/Г",
      "table.minutes_per_goal": "Минут на Гол",
      "table.total_injury_weeks_abbr": "Травмы",
      "table.total_injury_weeks": "Общий уровень травм",
      "table.total_injury_number_abbr": "Кол-во травм",
      "table.total_injury_number": "Общее количество травм",
      "table.average_age_abbr": "СВ",
      "table.average_age": "Средний возраст",
      "table.power_rating": "Power rating",
      "table.goals_difference": "+/-",
      "table.fanclub_size": "Фанклуб",
      "table.home_flags": "Домашние флаги",
      "table.away_flags": "Гостевые флаги",
      "table.all_flags": "Все флаги",
      "table.trophies": "Трофеи",
      "table.victories": "Побед подряд",
      "table.undefeated": "Без поражений",

      //team_rankings
      "team_rankings.ranking": "Рейтинг в ",

      //matches
      "matches.played_matches": "Сыгранные матчи",
      "matches.upcoming_matches": "Предстоящие матчи",
      "matches.result": "Результат",
      "matches.home_team": "Дома",
      "matches.away_team": "На выезде",
      "matches.ratings": "Рейтинги",
      "matches.defense_lmr": "Защита Л/Ц/П",
      "matches.midfield": "Полузащита",
      "matches.attack_lmr": "Атака Л/Ц/П",
      "matches.summary": "Общая информация",
      "matches.date": "Дата",
      "matches.formation": "Расстановка",
      "matches.hatstats": "HatStats",

      //filter
      "filter.page_size": "Размер страницы:",
      "filter.sorting_direction": "Сортировка:",
      "filter.asc": "по возрастанию",
      "filter.desc": "по убыванию",
      "filter.season": "Сезон:",
      "filter.round": "Тур:",
      "filter.sort_by": "Сортировка:",

      //bot
      "bot.header": "Упс, это бот",
      "bot.description": "В данный момент команда выглядит, как бот. Статистика для ботов недоступна :(",

      //error
      "error.header": "Ошибка",
      "error.description": "Похоже, такой страницы не существует или на сервере произошла ошибка. Пишите (ник в HT: Blackmorse)",
      "error.empty_field": "Поле не заполнено",

      //promotions
      "menu.promotions": "Переходы между лигами",
      "promotions.auto_promotions": "Автоматические переходы",
      "promotions.qualifications": "Квалификации",

      //cookies
      "cookies.header": "Сайти использует куки.",
      "cookies.content": "Информируем Вас, что на данным сайте используются куки для оптимизации взаимодействия пользователя с контентом сайте. Продолжая использовать сайт, вы соглашаетесь на их использование.",
      "cookies.accept": "ОК",

      //overview
      "overview.world_overview": "Обзор",
      "overview.surprising_matches": "Неожиданные матчи",
      "overview.top_salary_teams": "Команды с наибольшей зарплатой",
      "overview.top_matches": "Лучшие матчи",
      "overview.top_teams": "Лучшие команды",
      "overview.top_salary_players": "Самые высооплачиваемые игроки",
      "overview.top_rating_players": "Игроки с самыми высокими рейтингами",
      "overview.average_hatstats": "Средний показатель HatStats:",
      "overview.average_spectators": "Средняя посещаемость:",
      "overview.average_team_goals": "Среднее количество голов одной команды:",
      "overview.average_team_age": "Средний возраст команд:",
      "overview.average_team_salary": "Средняя зарплата команд:",
      "overview.average_team_stars": "Среднее количество звезд:",
      "overview.formations": "Расстановки",
      "overview.country": "Страна",
      "overview.goals": "Голы",
      "overview.number_of_teams": "Количество команд:",
      "overview.number_of_players": "Количество игроков:",
      "overview.number_of_goals": "Количество голов:",
      "overview.number_of_injuried": "Количество травмированных:",
      "overview.number_of_yellow": "Количество желтых карточек:",
      "overview.number_of_red": "Количество красных карточек:",
      "overview.numbers": "Сводка",
      "overview.averages": "Средние",

      "error.loading": "Ошибка загрузки..."
    }
  }
};


i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    lng: "en",

    keySeparator: false, // we do not use keys in form messages.welcome

    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;