export class Networks {
  static BASE_URL = ''

  static BASE_URL_DEV = 'http://localhost:8080/'

  static getURL(url) {
    if (url === null || url === undefined) {
      throw Error('Url can not be blank')
    }
    url = url.replaceAll(/[\/+]+/g, '/')
    if (import.meta.env.MODE === 'development') {
      return new URL(url, this.BASE_URL_DEV).href
    }
    return new URL(url, location.protocol + '//' + location.host).href
  }

  static getBaseUrl() {
    if (import.meta.env.MODE === 'development') {
      return this.BASE_URL_DEV
    }
    return new URL(location.protocol + '//' + location.host).href
  }

  static request(url, data, method, token, isJson = true) {
    let options = {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: data ? JSON.stringify(data) : null,
    }
    if (token) {
      options.headers['Authorization'] = 'Bearer ' + token
    }
    return fetch(url, options).then((resp) => {
      if (!resp.ok) {
        throw new Error('Network response was not ok')
      }
      return isJson ? resp.json() : resp.text()
    })
  }

  static get(url, token, json = true) {
    let theUrl = Networks.getURL(url)
    return Networks.request(theUrl, null, 'GET', token, json)
  }

  static post(url, data, token, json = true) {
    let theUrl = Networks.getURL(url)
    return Networks.request(theUrl, data, 'POST', token, json)
  }
}
