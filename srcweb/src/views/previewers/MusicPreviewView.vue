<template>
  <div v-if="url">
    <audio style="width: 100%" controls>
      <source :src="url" />
    </audio>
  </div>
</template>
<script>
import { Commons } from '@/networks/Commons'
export default {
  name: 'MusicPreviewView',

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

  data() {
    return {
      url: null,
    }
  },

  beforeUnmount() {
    if (this.url) {
      URL.revokeObjectURL(this.url)
      this.url = null
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
