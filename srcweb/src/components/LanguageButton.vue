<template>
  <a-dropdown :placement="placement" trigger="click">
    <a-button :size="size">
      <template #icon>
        <GlobalOutlined />
      </template>
    </a-button>
    <template #overlay>
      <a-menu>
        <a-menu-item @click="(e) => changeLanguage('zh')">简体中文</a-menu-item>
        <a-menu-item @click="(e) => changeLanguage('en')">English</a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>
<script>
import { useSessionStore } from '@/stores/SessionStore'
import { GlobalOutlined } from '@ant-design/icons-vue'
import { placements } from 'ant-design-vue/es/vc-tour/placements'

export default {
  name: 'LanguageButton',
  components: { GlobalOutlined },
  props: {
    placement: {
      default: 'bottom',
    },
    size: {
      default: 'default',
    },
  },
  data() {
    return {}
  },

  methods: {
    changeLanguage(locale) {
      let store = useSessionStore()
      store.updateLanguage(locale)
    },
  },
}
</script>
