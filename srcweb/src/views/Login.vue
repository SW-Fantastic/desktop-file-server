<template>
  <div
    style="
      width: 100vw;
      height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    "
  >
    <a-card style="width: 40%; min-width: 500px">
      <template #title>
        <span class="offset-field">{{ $t('views.login.title') }}</span>
      </template>
      <template #extra>
        <LanguageButton placement="bottom" />
      </template>
      <a-flex gap="middle" style="max-height: 300px; overflow-y: hidden">
        <a-flex gap="small" style="width: 58%" vertical justify="space-between">
          <a-flex gap="small" vertical>
            <div>
              <span class="offset-field" style="line-height: 34px">
                {{ $t('views.login.username') }}
              </span>
              <a-input
                style="width: 100%"
                v-model:value="username"
                :placeholder="$t('views.login.placeholder-username')"
              />
            </div>
            <div>
              <span class="offset-field" style="line-height: 34px">
                {{ $t('views.login.password') }}
              </span>
              <a-input
                style="width: 100%"
                v-model:value="password"
                type="password"
                :placeholder="$t('views.login.placeholder-password')"
              />
            </div>
          </a-flex>
          <a-flex gap="middle" vertical>
            <span class="offset-field">
              {{ $t('views.login.placeholder-reg') }}
              <a-button type="link" size="small">
                <span v-if="regEnabled">{{ $t('views.login.reg') }}</span>
                <span v-else>{{ $t('views.login.reg-off') }}</span>
              </a-button>
            </span>
            <a-button @click="login" style="width: 100%" type="primary" size="large">
              {{ $t('views.login.login') }}
            </a-button>
          </a-flex>
        </a-flex>
        <a-flex style="width: 48%" justify="start" items="center" vertical>
          <img :src="sideImg" />
        </a-flex>
      </a-flex>
    </a-card>
  </div>
</template>
<script>
import { Commons } from '@/networks/Commons'
import { Session } from '@/networks/Session'
import { useSessionStore } from '@/stores/SessionStore'
import { message } from 'ant-design-vue'
import LanguageButton from '@/components/LanguageButton.vue'
export default {
  name: 'LoginView',

  components: { LanguageButton },

  data() {
    return {
      username: '',
      password: '',
      sideImg: '',
      regEnabled: false,
    }
  },

  created() {
    Session.registerSupported().then((resp) => {
      this.regEnabled = resp
    })
  },

  mounted() {
    this.sideImg = new URL('../assets/side.png', import.meta.url).href
  },

  methods: {
    login() {
      if (Commons.isStringBlank(this.username) || Commons.isStringBlank(this.password)) {
        message.error(this.$t('views.login.not-blank'))
        return
      }
      Session.login(this.username, this.password)
        .catch((err) => {
          message.error(this.$t('views.login.login-failed'))
        })
        .then((resp) => {
          if (Commons.isStringBlank(resp)) {
            return
          }
          let sessionStore = useSessionStore()
          sessionStore.updateToken(resp)
          this.$router.replace('/')
        })
    },
    changeLanguage(locale) {
      let store = useSessionStore()
      store.updateLanguage(locale)
    },
  },
}
</script>
<style scoped>
.offset-field {
  padding-left: 11px;
}
</style>
