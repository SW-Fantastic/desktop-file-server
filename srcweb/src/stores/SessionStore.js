import { defineStore } from 'pinia'
import i18n from '@/i18n'

export const useSessionStore = defineStore('session', {
  state: () => {
    let lang = localStorage.getItem('sessionLang') || 'zh'
    i18n.global.locale.value = lang
    return {
      accessToken: sessionStorage.getItem('sessionId') || null,
      language: lang,
    }
  },
  actions: {
    updateToken(token) {
      this.accessToken = token
      sessionStorage.setItem('sessionId', token)
    },
    clearToken() {
      this.accessToken = null
      sessionStorage.removeItem('sessionId')
    },
    updateLanguage(lang) {
      this.language = lang
      localStorage.setItem('sessionLang', lang)
      i18n.global.locale.value = lang
    },
  },
})
