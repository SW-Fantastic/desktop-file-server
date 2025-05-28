<template>
  <div style="height: 600px">
    <a-textarea readonly :value="value" style="height: 100%; width: 100%; resize: none" />
  </div>
</template>
<script>
import { Commons } from '@/networks/Commons'
export default {
  name: 'TextPreviewView',
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

  data() {
    return {
      value: '',
    }
  },

  mounted() {
    if (this.file && this.client) {
      this.reload()
    }
  },

  methods: {
    reload() {
      let path = Commons.path(this.location) + '/' + this.file.basename
      this.client.getFileContents(path).then((data) => {
        let texeDecoder = new TextDecoder('UTF8')
        this.value = texeDecoder.decode(data)
      })
    },
  },
}
</script>
