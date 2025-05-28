<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
  >
    <template #title>
      <div style="padding-left: 11px">{{ $t('views.main.rename') }}</div>
    </template>
    <a-flex vertical>
      <span style="padding-left: 11px; line-height: 36px">{{ $t('views.main.rename-desc') }}</span>
      <a-input v-model:value="fileName"></a-input>
    </a-flex>
  </a-modal>
</template>
<script>
import { Commons } from '@/networks/Commons'
export default {
  name: 'RenameModal',
  emits: ['update:value'],
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

  watch: {
    file(next, old) {
      if (next) {
        this.fileName = next.basename
      } else {
        this.fileName = ''
      }
    },
  },

  data() {
    return {
      fileName: '',
    }
  },

  methods: {
    handleOk() {
      if (!this.client) {
        return
      }
      let basePath = Commons.path(this.location)
      this.$emit('update:value', false)
      this.client
        .moveFile(basePath + '/' + this.file.basename, basePath + '/' + this.fileName)
        .catch((e) => {})
        .then((resp) => {
          this.file.basename = this.fileName
        })
    },
    handleCancel() {
      this.$emit('update:value', false)
    },
  },
}
</script>
