import { useSessionStore } from '@/stores/SessionStore'
import { Networks } from './Networks'
import { Commons } from './Commons'

export class AdminSession {
  static getDefaultAvatar() {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.get('/@api/admin/default-avatar', store.accessToken).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to load groups, code is ' + resp.code)
    })
  }

  static getUsersByPage(groupId, pageNo, keywords, start, end) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }

    return Networks.post(
      '/@api/admin/groups/users',
      {
        id: groupId,
        size: 100,
        pageNo: pageNo,
        keyword: keywords,
        startDate: start ? start.valueOf() : null,
        endDate: end ? end.valueOf() : null,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to load groups, code is ' + resp.code)
    })
  }

  static createUserGroup(name, allowRegister) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }

    return Networks.post(
      '/@api/admin/groups',
      {
        name,
        allowRegister,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to create new group')
    })
  }

  static updateUserGroup(groupId, name, allowRegister) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.put(
      '/@api/admin/groups',
      {
        groupId,
        name,
        allowRegister,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to update group')
    })
  }

  static trashUserGroup(groupId) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.put(
      '/@api/admin/groups',
      {
        groupId,
        trash: true,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to trash group')
    })
  }

  static getUserGroups() {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.get('/@api/admin/groups', store.accessToken).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to load groups, code is ' + resp.code)
    })
  }

  static createUser(groupId, username, nickname, password, avatarBase64, totalSize) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.post(
      '/@api/admin/groups/users/create',
      {
        username,
        nickname,
        password,
        avatarBase64,
        totalSize,
        groupId,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to create a user')
    })
  }

  static trashUser(id, groupId, purge) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.put(
      '/@api/admin/groups/users/update',
      {
        id,
        groupId,
        purge: purge || false,
        trash: true,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to create a user')
    })
  }

  static updateUser(id, groupId, username, nickname, password, avatarBase64, totalSize) {
    const store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.put(
      '/@api/admin/groups/users/update',
      {
        id,
        username,
        nickname,
        password,
        avatarBase64,
        totalSize,
        groupId,
      },
      store.accessToken,
    ).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw new Error('Failed to create a user')
    })
  }
}
