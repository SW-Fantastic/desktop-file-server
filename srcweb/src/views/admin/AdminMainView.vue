<template>
  <div class="wrapper">
    <div style="width: 80%">
      <a-card>
        <template #title>
          <div
            style="
              display: flex;
              flex-direction: row;
              justify-content: space-between;
              align-items: center;
            "
          >
            <div class="avatar-row">
              <img
                v-if="userInfo.avatar"
                class="avatar-wrapper"
                :src="'data:image/png;base64,' + userInfo.avatar"
              />
              <div style="display: flex; flex-direction: column; padding: 12px">
                <div>{{ userInfo.nickname }}</div>
                <div style="color: rgba(0, 0, 0, 0.45)">
                  {{ userInfo.username }}
                </div>
              </div>
            </div>
            <div style="display: flex; flex-direction: row; column-gap: 4px">
              <LanguageButton size="large" />
              <a-button size="large" @click="backHome">
                <template #icon>
                  <HomeOutlined />
                </template>
              </a-button>
            </div>
          </div>
        </template>
        <a-tabs v-model:activeKey="activeKey">
          <a-tab-pane key="GroupAndUsers" :tab="$t('views.admin.user-and-group')">
            <AdminUserTab :user-info="userInfo" />
          </a-tab-pane>
          <a-tab-pane key="Registers" :tab="$t('views.admin.register-requests')">
            <AdminRegTab :user-info="userInfo" />
          </a-tab-pane>
          <a-tab-pane key="Logs" :tab="$t('views.admin.system-logs')">
            <AdminLogTab :user-info="userInfo" />
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </div>
  </div>
</template>
<script>
import { HomeOutlined } from '@ant-design/icons-vue'

import LanguageButton from '@/components/LanguageButton.vue'
import AdminUserTab from './AdminUserTab.vue'
import AdminRegTab from './AdminRegTab.vue'
import AdminLogTab from './AdminLogTab.vue'
import { useSessionStore } from '@/stores/SessionStore'
import { Session } from '@/networks/Session'
import { AdminSession } from '@/networks/AdminSession'
export default {
  name: 'AdminMainView',

  components: {
    HomeOutlined,
    LanguageButton,
    AdminUserTab,
    AdminRegTab,
    AdminLogTab,
  },

  data() {
    return {
      userInfo: {},
      activeKey: 'GroupAndUsers',
      groups: [],
    }
  },

  created() {
    Session.getUserInfo()
      .catch((e) => {
        let sessionStore = useSessionStore()
        sessionStore.clearToken()
        this.$router.replace('/login')
      })
      .then((resp) => {
        if (resp.permissions && resp.permissions != 'SUPER_ADMIN') {
          this.$router.replace('/')
        } else {
          this.userInfo = resp
        }
      })
  },

  methods: {
    backHome() {
      this.$router.replace('/')
    },
  },
}
</script>
<style scoped>
body {
  padding: 0;
  margin: 0;
}
.wrapper {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  padding: 24px;
  justify-content: center;
}
.avatar-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 0 10px 2px #e2e2e2;
}
.avatar-row {
  display: flex;
  flex-direction: row;
  column-gap: 16px;
  justify-content: center;
  align-items: center;
}
</style>
