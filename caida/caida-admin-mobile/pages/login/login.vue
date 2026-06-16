<template>
  <view class="login-container">
    <view class="login-header">
      <view class="logo">🛍️</view>
      <text class="title">财大优选管理端</text>
      <text class="subtitle">校园电商平台后台管理系统</text>
    </view>
    
    <view class="login-form">
      <view class="form-item">
        <view class="icon">👤</view>
        <input 
          type="text" 
          v-model="form.username" 
          placeholder="请输入管理员账号"
          placeholder-class="placeholder"
        />
      </view>
      
      <view class="form-item">
        <view class="icon">🔒</view>
        <input 
          :type="showPassword ? 'text' : 'password'" 
          v-model="form.password" 
          placeholder="请输入密码"
          placeholder-class="placeholder"
        />
        <view class="eye" @click="showPassword = !showPassword">
          {{ showPassword ? '👁️' : '👁️‍🗨️' }}
        </view>
      </view>
      
      <button class="login-btn" @click="handleLogin" :loading="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      
      <view class="tips">
        <text>默认账号：admin / 123456</text>
      </view>
    </view>
    
    <view class="login-footer">
      <text>© 2024 财大优选 All Rights Reserved</text>
    </view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      showPassword: false,
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.form.username) {
        uni.showToast({
          title: '请输入账号',
          icon: 'none'
        })
        return
      }
      
      if (!this.form.password) {
        uni.showToast({
          title: '请输入密码',
          icon: 'none'
        })
        return
      }
      
      this.loading = true
      
      try {
        const res = await config.request({
          url: '/admin/login',
          method: 'POST',
          data: this.form
        })
        
        if (res.code === 200) {
          // 保存token和管理员信息
          uni.setStorageSync(config.ADMIN_TOKEN_KEY, res.data.token)
          uni.setStorageSync(config.ADMIN_INFO_KEY, res.data.admin)
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          })
          
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            })
          }, 1500)
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 60rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 80rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 30rpx;
  font-size: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #FFFFFF;
  margin-bottom: 20rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.login-form {
  width: 100%;
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  display: flex;
  align-items: center;
  background: #F5F5F5;
  border-radius: 50rpx;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
  height: 90rpx;
}

.icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.form-item input {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.placeholder {
  color: #999;
}

.eye {
  font-size: 40rpx;
  padding: 10rpx;
}

.login-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  border-radius: 50rpx;
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  margin-top: 40rpx;
}

.login-btn::after {
  border: none;
}

.tips {
  text-align: center;
  margin-top: 30rpx;
  font-size: 24rpx;
  color: #999;
}

.login-footer {
  margin-top: auto;
  padding-top: 60rpx;
  text-align: center;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}
</style>
