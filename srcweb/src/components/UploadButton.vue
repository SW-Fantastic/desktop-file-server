<template>
  <a-dropdown :placement="placement" trigger="click" :arrow="{ pointAtCenter: true }">
    <a-button>
      <template #icon>
        <CloudUploadOutlined />
      </template>
    </a-button>
    <template #overlay>
      <a-card style="width: 400px">
        <a-upload :file-list="[]" :before-upload="beforeUpload">
          <a-button>
            <template #icon>
              <UploadOutlined />
            </template>
            {{ $t('views.main.add-file') }}
          </a-button>
        </a-upload>
        <a-divider />
        <a-flex vertical style="width: 100%; max-height: 200px; overflow-y: auto">
          <div v-for="item in fileList" style="flex-shrink: 0">
            <a-flex align="center" gap="middle">
              <FileOutlined style="font-size: 28px" />
              <a-flex vertical style="width: 80%">
                <div class="lbl-file-name">
                  {{ item.name }}
                </div>
                <div>
                  <a-progress :percent="item.__progress" size="small"></a-progress>
                </div>
              </a-flex>
            </a-flex>
          </div>
        </a-flex>
      </a-card>
    </template>
  </a-dropdown>
</template>
<script>
import { Commons } from '@/networks/Commons'
import { CloudUploadOutlined, UploadOutlined, FileOutlined } from '@ant-design/icons-vue'
export default {
  name: 'UploadButton',
  components: { CloudUploadOutlined, UploadOutlined, FileOutlined },
  emits: ['refresh'],
  props: {
    placement: {
      default: 'bottom',
    },
    client: {
      default: null,
    },
    location: {
      require: true,
    },
  },
  data() {
    return {
      fileList: [],
      chunkSize: 1024 * 1024 * 4,
    }
  },
  methods: {
    beforeUpload(file) {
      if (!this.client) {
        return false
      }
      let reader = new FileReader()
      reader.onload = (e) => {
        this.load(file, e)
      }
      reader.onprogress = (e) => {
        file.__progress = (e.loaded / e.total).toFixed(2) * 100
      }
      file.__progress = 0
      this.fileList.push(file)
      reader.readAsArrayBuffer(file)
      return false
    },

    load(file, e) {
      let prev = file.__readed
      if (prev === undefined || prev == null) {
        prev = 0
      }

      let path = Commons.path(this.location)
      // e.target.result
      this.client
        .putFileContents(path + '/' + file.name, e.target.result)
        .then((resp) => {
          this.$emit('refresh')
        })
        .catch((e) => {
          this.client.deleteFile(path + '/' + file.name).then((resp) => {
            let pos = this.fileList.indexOf(file)
            this.fileList.splice(pos, 1)
          })
        })
    },

    handleRemove() {},
  },
}
</script>
<style scoped>
.lbl-file-name {
  max-width: 100%;
  overflow-x: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
