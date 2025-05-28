<template>
  <div style="display: flex; width: 100vw; height: 100vh; padding: 32px">
    <!-- 各类文件操作的Modal -->
    <RenameModal
      v-model:value="renameVisible"
      :file="editingFile"
      :location="location"
      :client="client"
    />
    <DeleteModal
      v-model:value="deleteVisible"
      :location="location"
      :file="editingFile"
      :client="client"
      @refresh="refreshView"
    />
    <AddFolderModal
      v-model:value="addFolderVisible"
      :location="location"
      :client="client"
      @refresh="refreshView"
    />
    <PreviewModal
      v-model:value="previewVisible"
      :location="location"
      :file="editingFile"
      :client="client"
    />
    <a-flex gap="middle" align="start" justify="center" style="width: 100%">
      <div style="width: 20%">
        <a-affix :offset-top="32">
          <UserCard />
        </a-affix>
      </div>
      <a-card style="width: 68%">
        <template #title>
          <a-flex align="center" gap="middle">
            <a-button @click="backHome">
              <template #icon>
                <HomeOutlined />
              </template>
            </a-button>
            <a-breadcrumb>
              <a-breadcrumb-item v-for="(item, idx) in location">
                <a-button @click="(e) => pathClick(idx)" type="text" size="small">
                  {{ item }}
                </a-button>
              </a-breadcrumb-item>
            </a-breadcrumb>
          </a-flex>
        </template>
        <template #extra>
          <a-affix>
            <a-flex align="center" justify="flex-end" gap="small" class="floating-operations">
              <a-button @click="createFolder">
                <template #icon>
                  <FolderAddOutlined />
                </template>
              </a-button>
              <a-button @click="refreshView">
                <template #icon>
                  <ReloadOutlined />
                </template>
              </a-button>
              <UploadButton
                :client="client"
                :location="location"
                placement="bottomRight"
                @refresh="refreshView"
              />
            </a-flex>
          </a-affix>
        </template>
        <div>
          <a-table
            size="middle"
            sticky
            :customRow="
              (rec) => ({
                onclick: (e) => rowClick(rec),
              })
            "
            :dataSource="contents"
            :pagination="false"
          >
            <a-table-column :width="48" key="type" :title="$t('views.main.type')" data-index="type">
              <template #default="{ record }">
                <div style="font-size: 18px">
                  <FolderOutlined v-if="record.type === 'directory'" />
                  <FileOutlined v-else />
                </div>
              </template>
            </a-table-column>
            <a-table-column key="basename" :title="$t('views.main.name')" data-index="basename">
              <template #default="{ record }">
                <div style="white-space: nowrap; text-overflow: ellipsis; overflow-x: hidden">
                  {{ record.basename }}
                </div>
              </template>
            </a-table-column>
            <a-table-column key="size" :title="$t('views.main.size')" data-index="size">
              <template #default="{ record }">
                <div>{{ getSize(record.size) }}</div>
              </template>
            </a-table-column>
            <a-table-column key="lastmod" :title="$t('views.main.date')" data-index="lastmod">
              <template #default="{ record }">
                <div>{{ getDate(record.lastmod) }}</div>
              </template>
            </a-table-column>
            <a-table-column key="action">
              <template #default="{ record }">
                <a-flex gap="small" justify="flex-end">
                  <a-button v-if="record.type !== 'directory'" @click="(e) => download(record)">
                    <template #icon>
                      <DownloadOutlined />
                    </template>
                  </a-button>
                  <a-button @click="(e) => renameFile(e, record)">
                    <template #icon>
                      <EditOutlined />
                    </template>
                  </a-button>
                  <a-button @click="(e) => deleteFile(e, record)">
                    <template #icon>
                      <DeleteOutlined />
                    </template>
                  </a-button>
                </a-flex>
              </template>
            </a-table-column>
          </a-table>
        </div>
      </a-card>
    </a-flex>
  </div>
</template>
<script>
import UserCard from '@/components/UserCard.vue'
import RenameModal from './RenameModal.vue'
import DeleteModal from './DeleteModal.vue'
import AddFolderModal from './AddFolderModal.vue'
import PreviewModal from './previewers/PreviewModal.vue'
import UploadButton from '@/components/UploadButton.vue'

import { Commons } from '@/networks/Commons'
import { Session } from '@/networks/Session'
import {
  FolderOutlined,
  FileOutlined,
  DeleteOutlined,
  EditOutlined,
  DownloadOutlined,
  HomeOutlined,
  CloudUploadOutlined,
  FolderAddOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'

export default {
  name: 'MainView',

  components: {
    FolderOutlined,
    FileOutlined,
    DeleteOutlined,
    EditOutlined,
    DownloadOutlined,
    HomeOutlined,
    CloudUploadOutlined,
    FolderAddOutlined,
    ReloadOutlined,
    UploadButton,
    AddFolderModal,
    RenameModal,
    DeleteModal,
    PreviewModal,
    UserCard,
  },

  data() {
    return {
      client: null,
      location: [],
      contents: [],
      renameVisible: false,
      deleteVisible: false,
      addFolderVisible: false,
      previewVisible: false,
      editingFile: null,
    }
  },

  created() {
    try {
      this.client = Session.getDAVClient()
      this.refreshView()
    } catch (e) {
      let sessionStore = useSessionStore()
      sessionStore.clearToken()
      this.$router.replace('/login')
    }
  },

  methods: {
    refreshView() {
      if (this.client == null) {
        return
      }
      let target = Commons.path(this.location)
      this.client.getDirectoryContents(target).then((resp) => {
        this.contents = resp
      })
    },
    rowClick(record) {
      if (record.type === 'directory') {
        this.location.push(record.basename)
        this.refreshView()
      } else {
        this.editingFile = record
        this.previewVisible = true
      }
    },
    renameFile(e, rec) {
      e.stopPropagation()
      this.editingFile = rec
      this.renameVisible = true
    },
    deleteFile(e, rec) {
      e.stopPropagation()
      this.editingFile = rec
      this.deleteVisible = true
    },
    download(rec) {
      if (!this.client) {
        return
      }
      let location = Commons.path(this.location)
      this.client.getFileContents(location + '/' + rec.basename).then((data) => {
        let bufferedUrl = URL.createObjectURL(new Blob([data], { type: rec.mime }))
        let link = document.createElement('a')
        link.setAttribute('download', rec.basename)
        link.setAttribute('href', bufferedUrl)
        document.body.appendChild(link)
        link.click()
        link.remove()
        URL.revokeObjectURL(bufferedUrl)
      })
    },
    createFolder() {
      this.editingFile = null
      this.addFolderVisible = true
    },
    backHome() {
      this.location = []
      this.refreshView()
    },
    pathClick(index) {
      let next = []
      for (let idx = 0; idx <= index; idx++) {
        next.push(this.location[idx])
      }
      this.location = next
      this.refreshView()
    },
    getSize(size) {
      if (size == 0) {
        return '/'
      }
      return (size / (1024 * 1024)).toFixed(2) + ' MB'
    },
    getDate(dateStr) {
      if (!dateStr) {
        return '/'
      }
      let date = new Date(dateStr)
      return date.toLocaleString('en', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      })
    },
  },
}
</script>
<style scoped>
.floating-operations {
  background-color: var(--color-background);
  padding: 6px;
  border-style: solid;
  border-radius: 0 0 8px 8px;
  border-width: 0 1px 1px 1px;
  border-color: var(--color-border);
}
</style>
