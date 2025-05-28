<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    ok-type="danger"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
  >
    <template #title>
      <div style="padding-left: 11px">{{ $t('views.main.delete') }}</div>
    </template>
    <a-flex v-if="file" vertical>
      <span style="padding-left: 11px; line-height: 36px">
        {{ $t('views.main.delete-desc') }}
      </span>
      <span style="padding-left: 11px">
        {{ file.type === 'directory' ? $t('views.main.dir') : $t('views.main.file') }}
        {{ file.basename }}
      </span>
    </a-flex>
  </a-modal>
</template>
<script>
import { Commons } from '@/networks/Commons'
export default {
  name: 'DeleteModal',
  emits: ['update:value', 'refresh'],
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

  methods: {
    handleOk() {
      if (!this.file || !this.client) {
        return
      }
      let path = Commons.path(this.location)
      this.client.deleteFile(path + '/' + this.file.basename).then((resp) => {
        this.$emit('refresh')
      })
      this.$emit('update:value', false)
    },
    handleCancel() {
      this.$emit('update:value', false)
    },
  },
}
</script>
