<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
  >
    <template #title>
      <div style="padding-left: 11px">{{ title }}</div>
    </template>
    <a-flex vertical>
      <div v-if="isEditing">
        <span style="padding-left: 11px; line-height: 36px">
          {{ $t('views.admin.groups.create-desc') }}
        </span>
        <a-input v-model:value="editGroup.groupName"></a-input>
        <div style="padding: 11px">
          <a-checkbox v-model:checked="editGroup.allowRegister">{{
            $t('views.admin.groups.allow-reg')
          }}</a-checkbox>
        </div>
      </div>
      <div v-else>
        <span style="padding-left: 11px; line-height: 36px">
          {{ $t('views.admin.groups.delete-desc') }} {{ editGroup.groupName }} ?
        </span>
      </div>
    </a-flex>
  </a-modal>
</template>
<script>
import { Commons } from '@/networks/Commons'
import { AdminSession } from '@/networks/AdminSession'
import { message } from 'ant-design-vue'

export default {
  name: 'EditGroupModal',

  emits: ['update:value', 'refresh'],

  props: {
    group: {
      default: null,
    },
    value: {
      default: false,
    },
    operation: {
      default: 'NEW',
    },
  },

  watch: {
    group() {
      this.editGroup = this.group
    },
  },

  data() {
    return {
      editGroup: {
        id: null,
        groupName: null,
        allowRegister: false,
      },
    }
  },

  created() {},

  computed: {
    title() {
      switch (this.operation) {
        case 'NEW':
          return this.$t('views.admin.groups.create-title')
        case 'EDIT':
          return this.$t('views.admin.groups.edit-title')
        case 'TRASH':
          return this.$t('views.admin.groups.delete-title')
        default:
          return '参数错误'
      }
    },
    isEditing() {
      return this.operation === 'NEW' || this.operation === 'EDIT'
    },
  },

  methods: {
    handleException(e) {
      let sessionStore = useSessionStore()
      sessionStore.clearToken()
      this.$router.replace('/login')
    },
    handleOk() {
      if (this.operation === 'NEW') {
        if (Commons.isStringBlank(this.editGroup.groupName)) {
          message.error('分组名称不可为空。')
          return
        }
        AdminSession.createUserGroup(this.editGroup.groupName, this.editGroup.allowRegister)
          .catch(this.handleException)
          .then((resp) => {
            this.$emit('refresh')
          })
      } else if (this.operation === 'EDIT') {
        if (Commons.isStringBlank(this.editGroup.groupName)) {
          message.error('分组名称不可为空。')
          return
        }
        AdminSession.updateUserGroup(
          this.editGroup.id,
          this.editGroup.groupName,
          this.editGroup.allowRegister,
        )
          .catch(this.handleException)
          .then((resp) => {
            this.$emit('refresh')
          })
      } else if (this.operation === 'TRASH') {
        if (typeof this.editGroup.id !== 'number') {
          message.error('选择的分组无效。')
          return
        }
        AdminSession.trashUserGroup(this.editGroup.id)
          .catch(this.handleException)
          .then((resp) => {
            this.$emit('refresh')
          })
      }
      this.$emit('update:value', false)
    },
    handleCancel() {
      this.$emit('update:value', false)
    },
  },
}
</script>
