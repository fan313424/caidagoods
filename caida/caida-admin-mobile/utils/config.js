/**
 * 全局配置文件
 */

// API基础路径配置
const API_BASE_URL = '/api'

// 管理员Token存储键名
const ADMIN_TOKEN_KEY = 'admin_token'
const ADMIN_INFO_KEY = 'admin_info'

// 请求封装
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync(ADMIN_TOKEN_KEY)
    
    uni.request({
      url: API_BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            // 未登录或token过期
            uni.removeStorageSync(ADMIN_TOKEN_KEY)
            uni.removeStorageSync(ADMIN_INFO_KEY)
            uni.reLaunch({
              url: '/pages/login/login'
            })
            reject(res.data)
          } else {
            uni.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 检查登录状态
const checkLogin = () => {
  const token = uni.getStorageSync(ADMIN_TOKEN_KEY)
  const adminInfo = uni.getStorageSync(ADMIN_INFO_KEY)
  return !!(token && adminInfo)
}

// 获取管理员信息
const getAdminInfo = () => {
  return uni.getStorageSync(ADMIN_INFO_KEY) || null
}

// 退出登录
const logout = () => {
  uni.removeStorageSync(ADMIN_TOKEN_KEY)
  uni.removeStorageSync(ADMIN_INFO_KEY)
  uni.reLaunch({
    url: '/pages/login/login'
  })
}

export default {
  API_BASE_URL,
  ADMIN_TOKEN_KEY,
  ADMIN_INFO_KEY,
  request,
  checkLogin,
  getAdminInfo,
  logout
}