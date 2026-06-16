<template>
  <view class="user-list-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索用户名/手机号"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>
    
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view 
        class="filter-item" 
        :class="{ active: currentStatus === 'all' }"
        @click="filterByStatus('all')"
      >
        全部
      </view>
      <view 
        class="filter-item" 
        :class="{ active: currentStatus === 1 }"
        @click="filterByStatus(1)"
      >
        正常
      </view>
      <view 
        class="filter-item" 
        :class="{ active: currentStatus === 0 }"
        @click="filterByStatus(0)"
      >
        已禁用
      </view>
    </view>
    
    <!-- 用户列表 -->
    <scroll-view 
      class="user-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="user-list">
        <view 
          class="user-item" 
          v-for="item in userList" 
          :key="item.id"
        >
          <view class="user-avatar-placeholder" v-if="!item.avatar">👤</view>
          <image class="user-avatar" v-else :src="item.avatar" mode="aspectFill"></image>
          <view class="user-info">
            <view class="user-name-row">
              <text class="user-name">{{ item.nickname || item.username }}</text>
              <view class="user-status" :class="{ disabled: item.status === 0 }">
                {{ item.status === 1 ? '正常' : '已禁用' }}
              </view>
            </view>
            <text class="user-phone">{{ item.phone || '未绑定手机' }}</text>
            <text class="user-email">{{ item.email || '未绑定邮箱' }}</text>
            <text class="user-time">注册时间: {{ item.createTime }}</text>
          </view>
          <view class="user-actions">
            <view 
              class="action-btn" 
              :class="{ 'enable-btn': item.status === 0 }"
              @click="toggleUserStatus(item)"
            >
              {{ item.status === 1 ? '禁用' : '启用' }}
            </view>
          </view>
        </view>
      </view>
      
      <view class="loading-tip" v-if="loading">
        <text>加载中...</text>
      </view>
      
      <view class="no-more" v-if="!hasMore && userList.length > 0">
        <text>没有更多了</text>
      </view>
      
      <view class="empty" v-if="!loading && userList.length === 0">
        <text>暂无用户数据</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      searchKeyword: '',
      currentStatus: 'all',
      userList: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      loading: false,
      isRefreshing: false
    }
  },
  
  onLoad() {
    this.loadUsers()
  },
  
  onPullDownRefresh() {
    this.onRefresh()
  },
  
  methods: {
    async loadUsers(isRefresh = false) {
      if (this.loading) return
      
      this.loading = true
      
      if (isRefresh) {
        this.page = 1
        this.hasMore = true
      }
      
      try {
        // 模拟数据
        const mockData = [
          {
            id: 1,
            username: 'zhangsan',
            nickname: '张三',
            phone: '13800138001',
            email: 'zhangsan@example.com',
            status: 1,
            createTime: '2024-01-15 10:30:00',
            avatar: 'https://via.placeholder.com/100'
          },
          {
            id: 2,
            username: 'lisi',
            nickname: '李四',
            phone: '13800138002',
            email: 'lisi@example.com',
            status: 1,
            createTime: '2024-01-14 15:20:00',
            avatar: 'https://via.placeholder.com/100'
          },
          {
            id: 3,
            username: 'wangwu',
            nickname: '王五',
            phone: '13800138003',
            email: 'wangwu@example.com',
            status: 0,
            createTime: '2024-01-13 09:10:00',
            avatar: 'https://via.placeholder.com/100'
          },
          {
            id: 4,
            username: 'zhaoliu',
            nickname: '赵六',
            phone: '13800138004',
            email: 'zhaoliu@example.com',
            status: 1,
            createTime: '2024-01-12 16:45:00',
            avatar: 'https://via.placeholder.com/100'
          },
          {
            id: 5,
            username: 'sunqi',
            nickname: '孙七',
            phone: '13800138005',
            email: 'sunqi@example.com',
            status: 1,
            createTime: '2024-01-11 11:30:00',
            avatar: 'https://via.placeholder.com/100'
          }
        ]
        
        // 模拟筛选
        let filteredData = mockData
        if (this.currentStatus !== 'all') {
          filteredData = mockData.filter(item => item.status === this.currentStatus)
        }
        
        if (this.searchKeyword) {
          filteredData = filteredData.filter(item => 
            item.username.includes(this.searchKeyword) ||
            item.phone.includes(this.searchKeyword) ||
            (item.nickname && item.nickname.includes(this.searchKeyword))
          )
        }
        
        if (isRefresh) {
          this.userList = filteredData
        } else {
          this.userList = [...this.userList, ...filteredData]
        }
        
        this.hasMore = false
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: '/admin/users',
        //   method: 'GET',
        //   data: {
        //     page: this.page,
        //     pageSize: this.pageSize,
        //     status: this.currentStatus === 'all' ? '' : this.currentStatus,
        //     keyword: this.searchKeyword
        //   }
        // })
        // 
        // if (isRefresh) {
        //   this.userList = res.data.list
        // } else {
        //   this.userList = [...this.userList, ...res.data.list]
        // }
        // 
        // this.hasMore = res.data.hasMore
      } catch (error) {
        console.error('加载用户列表失败:', error)
      } finally {
        this.loading = false
        this.isRefreshing = false
        uni.stopPullDownRefresh()
      }
    },
    
    handleSearch() {
      this.loadUsers(true)
    },
    
    filterByStatus(status) {
      this.currentStatus = status
      this.loadUsers(true)
    },
    
    onRefresh() {
      this.isRefreshing = true
      this.loadUsers(true)
    },
    
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.page++
        this.loadUsers()
      }
    },
    
    async toggleUserStatus(item) {
      const newStatus = item.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '启用' : '禁用'
      
      uni.showModal({
        title: '提示',
        content: `确定要${action}该用户吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              // 模拟更新
              item.status = newStatus
              
              // 实际API调用（后端实现后启用）
              // await config.request({
              //   url: `/admin/users/${item.id}/status`,
              //   method: 'PUT',
              //   data: { status: newStatus }
              // })
              
              uni.showToast({
                title: `${action}成功`,
                icon: 'success'
              })
            } catch (error) {
              console.error('操作失败:', error)
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.user-list-container {
  min-height: 100vh;
  background: #F5F5F5;
  display: flex;
  flex-direction: column;
}

.search-bar {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  padding: 20rpx;
  gap: 20rpx;
}

.search-bar input {
  flex: 1;
  height: 70rpx;
  background: #F5F5F5;
  border-radius: 35rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.search-btn {
  width: 140rpx;
  height: 70rpx;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
  border-radius: 35rpx;
  font-size: 28rpx;
  border: none;
  line-height: 70rpx;
}

.search-btn::after {
  border: none;
}

.filter-bar {
  display: flex;
  background: #FFFFFF;
  padding: 20rpx;
  gap: 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.filter-item {
  flex: 1;
  text-align: center;
  padding: 15rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 30rpx;
  background: #F5F5F5;
}

.filter-item.active {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
}

.user-scroll {
  flex: 1;
  height: 0;
}

.user-list {
  padding: 20rpx;
}

.user-item {
  display: flex;
  background: #FFFFFF;
  border-radius: 15rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.user-avatar-placeholder {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 15rpx;
  margin-bottom: 10rpx;
}

.user-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.user-status {
  padding: 5rpx 15rpx;
  background: #E8F5E9;
  color: #4CAF50;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.user-status.disabled {
  background: #FFEBEE;
  color: #F44336;
}

.user-phone,
.user-email,
.user-time {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 5rpx;
}

.user-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  padding: 15rpx 30rpx;
  background: #FF4444;
  color: #FFFFFF;
  border-radius: 30rpx;
  font-size: 24rpx;
}

.action-btn.enable-btn {
  background: #4CAF50;
}

.loading-tip,
.no-more,
.empty {
  text-align: center;
  padding: 40rpx;
  font-size: 28rpx;
  color: #999;
}
</style>
