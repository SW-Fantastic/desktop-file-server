export class Commons {
  /**
   * 字符串是否为空
   * @param {*} str 字符串
   * @returns boolean
   */
  static isStringBlank(str) {
    if (typeof str !== 'string') {
      return true
    }
    return !/[\S]+/g.test(str)
  }

  static path(location) {
    let target = '/'
    let first = true
    for (const item of location) {
      if (first) {
        first = false
      } else {
        target = target + '/'
      }
      target = target + item
    }
    return target
  }
}
