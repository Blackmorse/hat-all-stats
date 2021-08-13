import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import langEn from './locales/en.json'
import langRu from './locales/ru.json'
import langIt from './locales/it.json'
import langHr from './locales/hr.json'
import langEs from './locales/es.json'
import langDe from './locales/de.json'
import langTr from './locales/tr.json'
import langFa from './locales/fa.json'

// the translations
// (tip move them in a JSON file and import them)
const resources = {
  en: {
    translation: langEn
  },
  ru: {
    translation: langRu
  },
  it: {
    translation: langIt
  },
  hr: {
    translation: langHr
  },
  es: {
    translation: langEs
  },
  de: {
    translation: langDe
  },
  tr: {
    translation: langTr
  },
  fa: {
    translation: langFa
  }
};


i18n.use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    lng: "en",
    keySeparator: false, // we do not use keys in form messages.welcome

    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;