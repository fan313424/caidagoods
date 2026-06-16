<template>
  <view class="index-container">
    <!-- 顶部欢迎 -->
    <view class="header">
      <view class="welcome">
        <text class="hello">你好，</text>
        <text class="name">{{ adminInfo.realName || adminInfo.username }}</text>
      </view>
      <view class="role">{{ adminInfo.role === 1 ? '超级管理员' : '普通管理员' }}</view>
    </view>
    
    <!-- 数据统计 -->
    <view class="stats-container">
      <view class="stats-title">今日数据</view>
      <view class="stats-grid">
        <view class="stats-item">
          <text class="stats-value">{{ stats.userCount }}</text>
          <text class="stats-label">用户总数</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{ stats.productCount }}</text>
          <text class="stats-label">商品总数</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{ stats.orderCount }}</text>
          <text class="stats-label">订单总数</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{ stats.employeeCount }}</text>
          <text class="stats-label">员工总数</text>
        </view>
      </view>
    </view>
    
    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="section-title">快捷功能</view>
      <view class="actions-grid">
        <view class="action-item" @click="navigateTo('/pages/product/add')">
          <view class="action-icon" style="background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);">📦</view>
          <text class="action-text">添加商品</text>
        </view>
        
        <view class="action-item" @click="navigateTo('/pages/product/list')">
          <view class="action-icon" style="background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);">📋</view>
          <text class="action-text">商品列表</text>
        </view>
        
        <view class="action-item" @click="navigateTo('/pages/user/list')">
          <view class="action-icon" style="background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);">👥</view>
          <text class="action-text">用户管理</text>
        </view>
        
        <view class="action-item" @click="navigateTo('/pages/employee/list')">
          <view class="action-icon" style="background: linear-gradient(135deg, #9C27B0 0%, #BA68C8 100%);">👔</view>
          <text class="action-text">员工管理</text>
        </view>
        
        <view class="action-item" @click="navigateTo('/pages/order/list')">
          <view class="action-icon" style="background: linear-gradient(135deg, #FF9800 0%, #FFB74D 100%);">🛒</view>
          <text class="action-text">订单管理</text>
        </view>
        
        <view class="action-item" @click="handleLogout">
          <view class="action-icon" style="background: linear-gradient(135deg, #607D8B 0%, #78909C 100%);">🚪</view>
          <text class="action-text">退出登录</text>
        </view>
      </view>
    </view>
    
    <!-- 最近动态 -->
    <view class="recent-activity">
      <view class="section-title">最近动态</view>
      <view class="activity-list">
        <view class="activity-item" v-for="(item, index) in activities" :key="index">
          <view class="activity-dot"></view>
          <view class="activity-content">
            <text class="activity-text">{{ item.text }}</text>
            <text class="activity-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      adminInfo: {},
      stats: {
        userCount: 0,
        productCount: 0,
        orderCount: 0,
        employeeCount: 0
      },
      activities: [
        { text: '用户 张三 完成注册', time: '刚刚' },
        { text: '商品 苹果手机 上架成功', time: '5分钟前' },
        { text: '订单 #12345 已发货', time: '10分钟前' },
        { text: '员工 李四 登录系统', time: '30分钟前' }
      ]
    }
  },
  
  onLoad() {
    this.checkAuth()
    this.loadAdminInfo()
    this.loadStats()
  },
  
  onShow() {
    this.loadStats()
  },
  
  methods: {
    checkAuth() {
      if (!config.checkLogin()) {
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
    },
    
    loadAdminInfo() {
      this.adminInfo = config.getAdminInfo() || {}
    },
    
    async loadStats() {
      try {
        // 模拟数据，实际应该调用API
        this.stats = {
          userCount: 128,
          productCount: 356,
          orderCount: 89,
          employeeCount: 12
        }
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: '/admin/stats',
        //   method: 'GET'
        // })
        // this.stats = res.data
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    
    navigateTo(url) {
      if (url.includes('add')) {
        uni.navigateTo({ url })
      } else {
        uni.switchTab({ url })
      }
    },
    
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            config.logout()
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.index-container {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 20rpx;
}

.header {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  padding: 40rpx 30rpx;
  color: #FFFFFF;
}

.welcome {
  margin-bottom: 10rpx;
}

.hello {
  font-size: 32rpx;
}

.name {
  font-size: 36rpx;
  font-weight: bold;
}

.role {
  font-size: 26rpx;
  opacity: 0.9;
}

.stats-container {
  background: #FFFFFF;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.stats-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30rpx;
}

.stats-item {
  text-align: center;
  padding: 30rpx 0;
  background: #F8F8F8;
  border-radius: 15rpx;
}

.stats-value {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #FF6B35;
  margin-bottom: 10rpx;
}

.stats-label {
  display: block;
  font-size: 26rpx;
  color: #666;
}

.quick-actions {
  background: #FFFFFF;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-bottom: 15rpx;
}

.action-text {
  font-size: 26rpx;
  color: #666;
}

.recent-activity {
  background: #FFFFFF;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.activity-list {
  margin-top: 20rpx;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #FF6B35;
  margin-top: 8rpx;
  margin-right: 20rpx;
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-text {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.activity-time {
  font-size: 24rpx;
  color: #999;
}
</style>
