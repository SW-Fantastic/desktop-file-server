<template>
  <div v-if="file && client">
    <a-flex gap="middle" align="center" justify="center" vertical style="padding: 32px">
      <div><AppstoreOutlined style="font-size: 64px" /></div>
      <div>{{ $t('views.main.not-previewable') }}</div>
      <a-button @click="download" type="primary">
        {{ $t('views.main.download') }} {{ file.basename }}
      </a-button>
    </a-flex>
  </div>
</template>
<script>
import { Commons } from '@/networks/Commons'
import { AppstoreOutlined } from '@ant-design/icons-vue'
export default {
  name: 'CanNotPreviewView',

  components: { AppstoreOutlined },

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

  data() {
    return {}
  },

  methods: {
    download() {
      if (!this.client) {
        return
      }
      let location = Commons.path(this.location)
      this.client.getFileContents(location + '/' + this.file.basename).then((data) => {
        let bufferedUrl = URL.createObjectURL(new Blob([data], { type: this.file.mime }))
        let link = document.createElement('a')
        link.setAttribute('download', this.file.basename)
        link.setAttribute('href', bufferedUrl)
        document.body.appendChild(link)
        link.click()
        link.remove()
        URL.revokeObjectURL(bufferedUrl)
      })
    },
  },
}
</script>
