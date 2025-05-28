import { createI18n } from 'vue-i18n'
import messages from '@intlify/unplugin-vue-i18n/messages'

const i18n = createI18n({
  globalInjection: true,
  fallbackLocale: 'zh',
  locale: 'zh',
  legacy: false,
  messages,
})

export default i18n
