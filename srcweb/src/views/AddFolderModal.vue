<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
  >
    <template #title>
      <div style="padding-left: 11px">{{ $t('views.main.add-folder') }}</div>
    </template>
    <a-flex vertical>
      <span style="padding-left: 11px; line-height: 36px">
        {{ $t('views.main.add-folder-desc') }}
      </span>
      <a-input v-model:value="fileName"></a-input>
    </a-flex>
  </a-modal>
</template>
<script>
import { message } from 'ant-design-vue'
import { Commons } from '@/networks/Commons'
export default {
  name: 'AddFolderModal',

  emits: ['update:value', 'refresh'],

  props: {
    value: {
      default: false,
    },
    location: {
      require: true,
    },
    client: {
      default: null,
    },
  },

  data() {
    return {
      fileName: '',
    }
  },

  methods: {
    handleCancel() {
      this.$emit('update:value', false)
    },
    handleOk() {
      if (Commons.isStringBlank(this.fileName)) {
        return
      }
      let path = Commons.path(this.location)
      this.client.createDirectory(path + '/' + this.fileName).then((resp) => {
        this.fileName = ''
        this.$emit('refresh')
      })
      this.$emit('update:value', false)
    },
  },
}
</script>
