<template>
  <div >
    <RouterView />
  </div>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router'
export default {

  name: "App",

  components: [RouterLink, RouterView],
  
  created() {

  },

  mounted() {
    document.body.style['backgroundImage'] =  'url(' + new URL('assets/bg-default.png',import.meta.url).href + ")"
    document.title=this.$t('system.name');
  }

}
</script>

<style scoped>

</style>
