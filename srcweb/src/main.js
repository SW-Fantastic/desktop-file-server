import 'ant-design-vue/dist/reset.css'
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import i18n from './i18n'
import Antd from 'ant-design-vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(i18n)
app.use(Antd)
app.use(router)

app.mount('#app')
