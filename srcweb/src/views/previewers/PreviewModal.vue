<template>
  <a-modal :footer="null" :width="800" v-if="file && client" :open="value" @cancel="handleCancel">
    <template #title>
      <span style="padding-left: 11px">
        {{ file.basename }}
      </span>
    </template>
    <div style="max-width: 800px; max-height: 600px; overflow-y: auto">
      <component :is="viewType" :location="location" :file="file" :client="client"></component>
    </div>
  </a-modal>
</template>
<script>
import ImagePreviewView from './ImagePreviewView.vue'
import CanNotPreviewView from './CanNotPreviewView.vue'
import TextPreviewView from './TextPreviewView.vue'
import MusicPreviewView from './MusicPreviewView.vue'

export default {
  name: 'PreviewModal',

  emits: ['update:value'],

  components: {
    ImagePreviewView,
    CanNotPreviewView,
    TextPreviewView,
    MusicPreviewView,
  },

  props: {
    value: {
      default: false,
    },
    file: {
      default: null,
    },
    location: {
      require: true,
    },
    client: {
      default: null,
    },
  },

  data() {
    return {}
  },

  computed: {
    viewType() {
      let indexSubfix = this.file.basename.indexOf('.')
      if (indexSubfix < 0) {
        return 'CanNotPreviewView'
      } else {
        const views = {
          ImagePreviewView: ['png', 'jpg', 'jpeg', 'tiff', 'webp', 'gif', 'bmp'],
          TextPreviewView: ['txt', 'ini', 'inf', 'cfg', 'conf'],
          MusicPreviewView: ['mp3', 'wma', 'ogg', 'm4a'],
        }
        let subfix = this.file.basename.substring(indexSubfix + 1)

        for (let key of Object.getOwnPropertyNames(views)) {
          if (views[key] instanceof Array && views[key].indexOf(subfix) >= 0) {
            return key
          }
        }

        return 'CanNotPreviewView'
      }
    },
  },

  methods: {
    handleCancel() {
      this.$emit('update:value', false)
    },
  },
}
</script>
