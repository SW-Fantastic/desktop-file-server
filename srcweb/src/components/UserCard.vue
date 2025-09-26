<template>
  <div v-if="userInfo" style="width: 100%">
    <a-card>
      <a-flex align="center" justify="space-between" vertical style="width: 100%">
        <a-flex justify="center" align="center" style="width: 100%" vertical>
          <img
            style="
              width: 96px;
              height: 96px;
              border-radius: 9999px;
              position: relative;
              box-shadow: 0 0 10px 2px #e2e2e2;
            "
            :src="'data:image/png;base64,' + userInfo.avatar"
          />
          <a-divider />
        </a-flex>
        <div>{{ $t('views.main.welcome') }} {{ userInfo.username }}</div>
        <a-progress :percent="useage" :showInfo="false" />
        <a-flex gap="small" justify="center" align="center" style="padding: 12px">
          <LanguageButton />
          <a-button @click="goesAdminConsole" v-if="userInfo.permissions === 'SUPER_ADMIN'">
            <template #icon>
              <CodeOutlined />
            </template>
          </a-button>
          <a-button @click="logout">
            <template #icon>
              <LogoutOutlined />
            </template>
          </a-button>
        </a-flex>
      </a-flex>
    </a-card>
  </div>
</template>
<script>
import { Session } from '@/networks/Session'
import { LogoutOutlined, CodeOutlined } from '@ant-design/icons-vue'
import { useSessionStore } from '@/stores/SessionStore'
import LanguageButton from './LanguageButton.vue'
export default {
  name: 'UserCard',

  components: { LogoutOutlined, CodeOutlined, LanguageButton },

  data() {
    return {
      userInfo: null,
    }
  },

  computed: {
    useage() {
      return (this.userInfo.usedSize / this.userInfo.totalSize).toFixed(2) * 100
    },
  },

  created() {
    Session.getUserInfo()
      .catch((e) => {
        let sessionStore = useSessionStore()
        sessionStore.clearToken()
        this.$router.replace('/login')
      })
      .then((resp) => {
        this.userInfo = resp
      })
  },

  methods: {
    goesAdminConsole() {
      this.$router.replace('/admin')
    },
    logout() {
      let store = useSessionStore()
      store.clearToken()
      this.$router.replace('/login')
    },
  },
}
</script>
