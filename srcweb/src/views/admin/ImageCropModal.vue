<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
  >
    <template #title>
      <div style="padding-left: 11px">修改图像</div>
    </template>
    <div>
      <div style="max-width: 600px; max-height: 600px; overflow: hidden">
        <VueCropperJs ref="cropper" :src="image" :aspect-ratio="1" />
      </div>
    </div>
  </a-modal>
</template>
<script>
import VueCropperJs from 'vue-cropperjs'
import 'cropperjs/dist/cropper.css'

export default {
  name: 'ImageCropModal',
  emits: ['update:value', 'changed'],
  components: {
    VueCropperJs,
  },
  props: {
    value: {
      default: false,
    },
    image: {
      default: '',
    },
  },

  watch: {
    image(val) {
      if (!val) {
        this.$emit('update:value', false)
        return
      }
      this.$nextTick(() => {
        this.$refs.cropper.replace(val)
      })
    },
  },

  data() {
    return {}
  },
  methods: {
    handleOk() {
      const data = this.$refs.cropper.getCroppedCanvas().toDataURL()
      this.$emit('changed', data)
      this.$emit('update:value', false)
    },
    handleCancel() {
      this.$emit('update:value', false)
    },
  },
}
</script>
