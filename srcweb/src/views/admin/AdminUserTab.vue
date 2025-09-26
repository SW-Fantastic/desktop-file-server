<template>
  <div style="display: flex; flex-direction: row; column-gap: 12px">
    <EditGroupModal
      v-model:value="groupEdit.visible"
      :group="groupEdit.current"
      :operation="groupEdit.operation"
      @refresh="reloadGroups"
    />
    <EditUserModal
      v-model:value="userEdit.visible"
      :operation="userEdit.operation"
      :user="userEdit.current"
      :groups="this.groups"
      @refresh="refreshUserTable"
    ></EditUserModal>
    <div style="display: flex; flex-direction: column">
      <div class="row-wrapper">
        <div>
          <a-button @click="createGroup">
            <template #icon>
              <PlusOutlined />
            </template>
          </a-button>
        </div>
        <div style="display: flex; column-gap: 4px">
          <a-button @click="updateGroup">
            <template #icon>
              <EditOutlined />
            </template>
          </a-button>
          <a-button @click="deleteGroup">
            <template #icon>
              <DeleteOutlined />
            </template>
          </a-button>
        </div>
      </div>
      <a-menu v-model:selectedKeys="selectGroupKeys" @click="groupClicked" style="width: 300px">
        <a-menu-item :key="group.id" v-for="group in groups">
          {{ group.groupName }}
        </a-menu-item>
      </a-menu>
    </div>
    <div style="display: flex; width: 100%; flex-direction: column">
      <div
        style="
          display: flex;
          flex-direction: row;
          justify-content: space-between;
          padding-bottom: 12px;
        "
      >
        <div>
          <a-button @click="createUser">
            <template #icon>
              <PlusOutlined />
            </template>
          </a-button>
        </div>
        <div style="display: flex; flex-direction: row; justify-content: flex-end; column-gap: 8px">
          <a-input
            style="width: 40%"
            v-model:value="keywords"
            :placeholder="$t('views.admin.search-placeholder')"
          />
          <a-range-picker style="width: 50%" v-model:value="dateRange"></a-range-picker>
          <a-button @click="(e) => reloadUsers(this.selectedGroup ? this.selectedGroup.id : null)">
            <template #icon>
              <SearchOutlined />
            </template>
          </a-button>
        </div>
      </div>
      <a-table
        style="width: 100%"
        :columns="userColumns"
        :dataSource="users"
        :Pagination="usersPagenation"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <img class="avatar-wrapper" :src="'data:image/png;base64,' + record.avatar" />
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.state === 'NORMAL' ? 'green' : 'red'">
              {{ record.state }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'manage'">
            <div v-if="record.state === 'NORMAL'" class="manage-cell">
              <a-button @click="(e) => updateUser(record)">
                <template #icon>
                  <EditOutlined />
                </template>
              </a-button>
              <a-button @click="(e) => trashUser(record, false)">
                <template #icon>
                  <DeleteOutlined />
                </template>
              </a-button>
            </div>
            <div v-else>
              <a-button @click="(e) => trashUser(record, true)">
                <template #icon>
                  <DeleteOutlined />
                </template>
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>
<script>
import { Session } from '@/networks/Session'
import { useSessionStore } from '@/stores/SessionStore'
import { AdminSession } from '@/networks/AdminSession'
import { PlusOutlined, DeleteOutlined, EditOutlined, SearchOutlined } from '@ant-design/icons-vue'

import EditGroupModal from './EditGroupModal.vue'
import EditUserModal from './EditUserModal.vue'
import { message } from 'ant-design-vue'

export default {
  name: 'AdminUserTab',

  props: {
    userInfo: {
      required: true,
    },
  },

  components: {
    PlusOutlined,
    DeleteOutlined,
    EditOutlined,
    SearchOutlined,
    EditGroupModal,
    EditUserModal,
  },

  computed: {
    userColumns() {
      return [
        {
          title: this.$t('views.admin.usertable.avatar'),
          dataIndex: 'avatar',
          key: 'avatar',
        },
        {
          title: 'ID',
          dataIndex: 'id',
          key: 'id',
        },
        {
          title: this.$t('views.admin.usertable.username'),
          dataIndex: 'username',
          key: 'username',
        },
        {
          title: this.$t('views.admin.usertable.nickname'),
          key: 'nickname',
          dataIndex: 'nickname',
        },
        {
          title: this.$t('views.admin.usertable.state'),
          key: 'status',
        },
        {
          title: this.$t('views.admin.usertable.manage'),
          align: 'right',
          key: 'manage',
        },
      ]
    },
  },

  data() {
    return {
      // 用户组
      groupEdit: {
        operation: 'NEW',
        visible: false,
        current: {
          id: null,
          groupName: '',
          allowRegister: false,
        },
      },
      groups: [],
      selectGroupKeys: [],
      selectedGroup: null,
      // 用户列表
      users: [],
      userEdit: {
        operation: 'NEW',
        visible: false,
        current: null,
      },
      usersPagenation: {
        total: 0,
        current: 1,
        pageSize: 100,
      },
      // 用户列表 - Filter
      keywords: '',
      dateRange: [],
      defaultAvatar: '',
    }
  },

  created() {
    this.reloadGroups()
    AdminSession.getDefaultAvatar()
      .catch(this.handleException)
      .then((resp) => {
        this.defaultAvatar = resp
      })
  },

  watch: {
    keywords() {
      this.reloadUsers(this.selectedGroup ? this.selectedGroup.id : null)
    },
    dateRange() {
      this.reloadUsers(this.selectedGroup ? this.selectedGroup.id : null)
    },
    'usersPagenation.current'() {
      this.reloadUsers(this.selectedGroup ? this.selectedGroup.id : null)
    },
  },

  methods: {
    reloadGroups() {
      this.selectedGroup = null
      this.selectGroupKeys = []
      this.users = []
      AdminSession.getUserGroups()
        .catch(this.handleException)
        .then((list) => {
          this.groups = list
        })
    },
    reloadUsers(key) {
      const group = this.getGroup(key)
      if (group) {
        this.selectedGroup = group
        AdminSession.getUsersByPage(
          group.id,
          this.usersPagenation.current,
          this.keywords,
          this.dateRange ? this.dateRange[0] : null,
          this.dateRange ? this.dateRange[1] : null,
        )
          .catch(this.handleException)
          .then((resp) => {
            this.users = resp.content
            this.usersPagenation.pageSize = resp.size
            this.usersPagenation.total = resp.totalElements
          })
      } else {
        this.selectedGroup = null
        this.users = []
      }
    },
    groupClicked({ item, key, keyPath }) {
      this.reloadUsers(key)
    },
    handleException(e) {
      let sessionStore = useSessionStore()
      sessionStore.clearToken()
      this.$router.replace('/login')
    },
    refreshUserTable() {
      if (!this.selectGroupKeys.length) {
        this.users = []
        return
      }
      this.selectedGroup = this.getGroup(this.selectGroupKeys[0])
      this.reloadUsers(this.selectedGroup.id)
    },
    createUser() {
      this.userEdit.current = {
        username: '',
        nickname: '',
        password: '',
        repeated: '',
        totalSize: 0,
        groupId: null,
        avatar: this.defaultAvatar,
      }
      this.userEdit.operation = 'NEW'
      this.userEdit.visible = true
    },
    updateUser(user) {
      this.userEdit.current = {
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        password: '',
        repeated: '',
        totalSize: user.totalSize,
        groupId: user.group.id,
        avatar: user.avatar,
      }
      this.userEdit.operation = 'EDIT'
      this.userEdit.visible = true
    },
    trashUser(user, purge) {
      this.userEdit.current = {
        id: user.id,
        groupId: user.group.id,
        username: user.username,
        nickname: user.nickname,
        avatar: user.avatar,
      }
      this.userEdit.operation = purge ? 'PURGE' : 'TRASH'
      this.userEdit.visible = true
    },
    createGroup() {
      const groupEdit = this.groupEdit
      if (groupEdit.visible) {
        return
      }

      groupEdit.current = {
        id: null,
        name: '',
        allowRegister: false,
      }
      groupEdit.operation = 'NEW'
      groupEdit.visible = true
    },
    updateGroup() {
      if (!this.selectedGroup) {
        message.warn('请选择一个用户分组。')
        return
      }
      this.groupEdit.current = {
        groupName: this.selectedGroup.groupName,
        id: this.selectedGroup.id,
        allowRegister: this.selectedGroup.registrable,
      }
      this.groupEdit.operation = 'EDIT'
      this.groupEdit.visible = true
    },
    deleteGroup() {
      if (!this.selectedGroup) {
        message.warn('请选择一个用户分组。')
        return
      }
      this.groupEdit.current = {
        groupName: this.selectedGroup.groupName,
        id: this.selectedGroup.id,
        allowRegister: this.selectedGroup.registrable,
      }
      this.groupEdit.operation = 'TRASH'
      this.groupEdit.visible = true
    },
    getGroup(key) {
      if (!this.groups || this.groups.length <= 0) {
        return null
      }
      for (const gp of this.groups) {
        if (gp.id === key) {
          return gp
        }
      }
      return null
    },
  },
}
</script>
<style scoped>
.row-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-right: 16px;
  padding-left: 16px;
  padding-bottom: 4px;
}
.avatar-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 0 10px 2px #e2e2e2;
}
.manage-cell {
  display: flex;
  flex-direction: row;
  column-gap: 8px;
  justify-content: flex-end;
}
</style>
