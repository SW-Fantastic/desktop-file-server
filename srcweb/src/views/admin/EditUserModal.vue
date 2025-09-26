<template>
  <a-modal
    @ok="handleOk"
    @cancel="handleCancel"
    :open="value"
    :ok-text="$t('views.main.ok')"
    :cancel-text="$t('views.main.cancel')"
    width="800px"
  >
    <template #title>
      <div style="padding-left: 11px">{{ title }}</div>
    </template>
    <ImageCropModal @changed="avatarCropped" :image="this.selectedAvatarData" v-model:value="avatarEditorVisible" />
    <a-flex style="padding: 0 24px;">
      <form style="display: none;" ref="fileForm">
        <input ref="fileTrigger" @change="onAvatarSelected" accept="image/png"  type="file" />
      </form>
      <div v-if="isEditing" @click="uploadAvatar" style="display: flex;flex-direction: row;justify-content: center;align-items: center;">
          <img :class="{ 'avatar-wrapper': isEditing, 'avatar-trash': !isEditing }" :src="'data:image/png;base64,' + editUser.avatar"></img>
      </div>  
      <div v-if="isEditing" style="padding-left: 11px;padding-top: 34px;width: 100%;">
        <a-form
          ref="userForm"
          :rules="rules"
          :label-col="{ span: 7 }"
          :model="editUser"
          autocomplete="off"
        >
          <a-form-item :label="$t('views.admin.users.group')" name="groupId">
            <a-select v-model:value="editUser.groupId">
                <a-select-option v-for="group in groups" :value="group.id">
                  {{ group.groupName }}
                </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :label="$t('views.admin.users.username')" name="username">
            <a-input v-model:value="editUser.username" />
          </a-form-item>
          <a-form-item :label="$t('views.admin.users.nickname')" name="nickname">
            <a-input v-model:value="editUser.nickname" />
          </a-form-item>
          <a-form-item :label="$t('views.admin.users.password')" name="password">
            <a-input-password v-model:value="editUser.password" />
          </a-form-item>
          <a-form-item :label="$t('views.admin.users.repeat')" name="repeated">
            <a-input-password v-model:value="editUser.repeated" />
          </a-form-item>
          <a-form-item name="totalSize" no-style>
            <a-form-item name="totalSize" :label="$t('views.admin.users.total-space')">
              <a-slider
                style="margin-left: 8px;"
                v-model:value="editUser.totalSize"
                :max="oneGB * 10"
                :min="oneGB"
                :marks="sizeMarks"
              />
            </a-form-item>
          </a-form-item>
        </a-form>
      </div>
      <div v-else style="display: flex;flex-direction: column;justify-content: center;align-items: center;margin-left: -11px;padding-top: 12px;">
        <div>{{ $t('views.admin.users.trash-user-desc') }} {{ editUser.username }} ？</div>
        <div v-if="operation === 'PURGE'">{{ $t('views.admin.users.purge-user-desc') }}</div>
      </div>
    </a-flex>
  </a-modal>
</template>
<script>


import { Commons } from '@/networks/Commons'
import ImageCropModal from './ImageCropModal.vue'
import { AdminSession } from '@/networks/AdminSession'

export default {
  name: 'EditUserModal',

  emits: ['update:value', 'refresh'],
  components: {
    ImageCropModal
  },

  props: {
    value: {
      default: false,
    },
    operation: {
      default: 'NEW',
    },
    groups: {
      default: null,
    },
    user: {
      default: null,
    },
  },

  watch: {
    user() {
      if (this.user) {
        this.editUser = this.user
      } else {
        this.editUser = {
          username: '',
          nickname: '',
          password: '',
          repeated: '',
          totalSize: 0,
        }
      }
    },
  },

  data() {
    return {
      selectedAvatarData: '',
      avatarLock: false,  
      avatarEditorVisible: false,
      editUser: null,
      validateRulesEdit: {
        username: [{ required: true, message: 'Please input your username!' }],
        nickname: [{ required: true, message: 'Please input your nickname!' }],
        groupId:  [{ required: true, message: 'Please select your group!'}],
      },
      validateRules: {
        username: [{ required: true, message: 'Please input your username!' }],
        nickname: [{ required: true, message: 'Please input your nickname!' }],
        password: [{ required: true, message: 'Please input your password!' }],
        groupId:  [{ required: true, message: 'Please select your group!'}],
        repeated: [
          {
            required: true,
            validator: (rule, value, cb) => {
              return new Promise((res, rej) => {
                if (Commons.isStringBlank(value)) {
                  rej('请重复密码。')
                  return
                }
                if (value !== this.editUser.password) {
                  rej('两次输入的密码不匹配。')
                  return
                }
                res()
              })
            },
          },
        ],
      },
    }
  },

  computed: {
    title() {
      switch (this.operation) {
        case 'NEW':
          return this.$t("views.admin.users.new-user-title")
        case 'EDIT':
          return this.$t("views.admin.users.edit-user-title")
        case 'TRASH':
          return this.$t("views.admin.users.trash-user-title")
        case 'PURGE':
          return this.$t("views.admin.users.purge-user-title")
        default:
          return '参数错误'
      }
    },
    isEditing() {
      return this.operation === 'NEW' || this.operation === 'EDIT'
    },
    sizeMarks() {
      let masks = {}
      for (let i = 1; i <= 10; i++) {
        masks[i * this.oneGB] = i + ' G'
      }
      return masks
    },
    oneGB() {
      return 1024 * 1024 * 1024
    },
     rules() {
      if(this.operation === 'EDIT') {
        return this.validateRulesEdit;
      } else if( this.operation === 'NEW') {
        return this.validateRules;
      } else {
        return []
      }
    }
  },

  methods: {
    handleOk() {
      if(this.isEditing) {

        this.$refs.userForm.validate().then(() => {
          // 关闭Modal
          this.$emit('update:value', false)
          // 发送数据
          if(this.operation === 'NEW') {
            AdminSession.createUser(
              this.editUser.groupId,
              this.editUser.username,
              this.editUser.nickname,
              this.editUser.password,
              this.editUser.avatar,
              this.editUser.totalSize
            ).catch(this.handleError)
            .then(e => {
              this.$emit("refresh")
            })
          } else {
            AdminSession.updateUser(
              this.editUser.id,
              this.editUser.groupId,
              this.editUser.username,
              this.editUser.nickname,
              this.editUser.password,
              this.editUser.avatar,
              this.editUser.totalSize
            ).catch(this.handleError)
            .then(e => {
              this.$emit("refresh")
            })
          }
        })

      } else {
        this.$emit('update:value', false)
        AdminSession.trashUser(this.editUser.id, this.editUser.groupId, this.operation === 'PURGE')
          .catch(this.handleError)
          .then(e => {
              this.$emit("refresh")
          })
      }
      
    },
    uploadAvatar() {
        if(this.isEditing) {
            this.$refs.fileTrigger.click()
        }
    },
    onAvatarSelected(e) {
        
        if(this.avatarLock) {
            return
        }
        this.avatarLock = true;
        const file = e.target.files[0];
        const reader = new FileReader();
        reader.onload = (rs) => {
          this.selectedAvatarData = rs.target.result;
          this.avatarLock = false;
          this.avatarEditorVisible = true;
        };
        reader.readAsDataURL(file);

    },
    avatarCropped(value) {
      this.editUser.avatar = value.replace("data:image/png;base64,", "")
    },
    handleCancel() {
      this.$emit('update:value', false)
    },
    handleError(e) {},
  },
}
</script>
<style scoped>
.avatar-wrapper {
  width: 94px;
  height: 94px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 0 10px 2px #e2e2e2;
}
.avatar-trash {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 0 10px 2px #e2e2e2;
}
</style>
