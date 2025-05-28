<template>
  <div v-if="file && client" style="max-width: 100%">
    <img :src="url" style="width: 100%" />
  </div>
</template>
<script>
import { Commons } from '@/networks/Commons'
export default {
  name: 'ImagePreview',

  props: {
    location: {
      require: true,
    },
    file: {
      default: null,
    },
    client: {
      default: null,
    },
  },

  watch: {
    file(next, old) {
      this.reload()
    },
  },

  mounted() {
    if (this.file && this.client) {
      this.reload()
    }
  },

  beforeUnmount() {
    if (this.url) {
      URL.revokeObjectURL(this.url)
      this.url = null
    }
  },

  data() {
    return {
      url: null,
    }
  },

  methods: {
    reload() {
      if (this.url) {
        URL.revokeObjectURL(this.url)
      }
      let path = Commons.path(this.location) + '/' + this.file.basename
      this.client.getFileContents(path).then((data) => {
        this.url = URL.createObjectURL(new Blob([data], { type: this.file.mime }))
      })
    },
  },
}
</script>
