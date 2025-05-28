import { Networks } from './Networks'
import { Commons } from './Commons'
import { AuthType, createClient } from 'webdav'
import { useSessionStore } from '@/stores/SessionStore'

export class Session {
  static registerSupported() {
    return Networks.get('/@api/register').then((resp) => {
      return JSON.parse(resp.data)
    })
  }

  static login(username, password) {
    return Networks.post(
      '/@auth/login',
      {
        username,
        password,
      },
      null,
      false,
    ).then((resp) => {
      return resp
    })
  }

  static getUserInfo() {
    let store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      return new Promise((resolve, reject) => {
        throw Error('Please login first.')
      })
    }
    return Networks.get('/@api/user-info', store.accessToken).then((resp) => {
      if (resp.code === 200) {
        return resp.data
      }
      throw Error('failed to load data , code is ' + resp.code)
    })
  }

  static getDAVClient() {
    let store = useSessionStore()
    if (Commons.isStringBlank(store.accessToken)) {
      throw Error('Please login first.')
    }
    return createClient(Networks.getBaseUrl(), {
      authType: AuthType.Token,
      token: {
        access_token: store.accessToken,
        token_type: 'Bearer',
      },
    })
  }
}
