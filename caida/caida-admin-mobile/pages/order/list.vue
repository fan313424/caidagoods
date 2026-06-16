<template>
  <view class="order-list-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索订单号/用户名"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>
    
    <!-- 筛选栏 -->
    <scroll-view class="filter-scroll" scroll-x>
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
          :class="{ active: currentStatus === 0 }"
          @click="filterByStatus(0)"
        >
          待付款
        </view>
        <view 
          class="filter-item" 
          :class="{ active: currentStatus === 1 }"
          @click="filterByStatus(1)"
        >
          待发货
        </view>
        <view 
          class="filter-item" 
          :class="{ active: currentStatus === 2 }"
          @click="filterByStatus(2)"
        >
          已发货
        </view>
        <view 
          class="filter-item" 
          :class="{ active: currentStatus === 3 }"
          @click="filterByStatus(3)"
        >
          已完成
        </view>
        <view 
          class="filter-item" 
          :class="{ active: currentStatus === 4 }"
          @click="filterByStatus(4)"
        >
          已取消
        </view>
      </view>
    </scroll-view>
    
    <!-- 订单列表 -->
    <scroll-view 
      class="order-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="order-list">
        <view 
          class="order-item" 
          v-for="item in orderList" 
          :key="item.id"
        >
          <view class="order-header">
            <text class="order-no">订单号: {{ item.orderNo }}</text>
            <view class="order-status" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </view>
          </view>
          
          <view class="order-user">
            <text class="user-name">{{ item.userName }}</text>
            <text class="user-phone">{{ item.userPhone }}</text>
          </view>
          
          <view class="order-products">
            <view class="product-item" v-for="(product, index) in item.products" :key="index">
              <view class="product-image-placeholder" v-if="!product.image">📦</view>
              <image class="product-image" v-else :src="product.image" mode="aspectFill"></image>
              <view class="product-info">
                <text class="product-name">{{ product.name }}</text>
                <text class="product-price">¥{{ product.price }} × {{ product.quantity }}</text>
              </view>
            </view>
          </view>
          
          <view class="order-footer">
            <view class="order-total">
              <text class="total-label">订单总额:</text>
              <text class="total-price">¥{{ item.totalAmount }}</text>
            </view>
            <view class="order-time">{{ item.createTime }}</view>
          </view>
          
          <view class="order-actions" v-if="item.status === 1">
            <view class="action-btn primary" @click="shipOrder(item)">发货</view>
          </view>
        </view>
      </view>
      
      <view class="loading-tip" v-if="loading">
        <text>加载中...</text>
      </view>
      
      <view class="no-more" v-if="!hasMore && orderList.length > 0">
        <text>没有更多了</text>
      </view>
      
      <view class="empty" v-if="!loading && orderList.length === 0">
        <text>暂无订单数据</text>
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
      orderList: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      loading: false,
      isRefreshing: false
    }
  },
  
  onLoad() {
    this.loadOrders()
  },
  
  onPullDownRefresh() {
    this.onRefresh()
  },
  
  methods: {
    async loadOrders(isRefresh = false) {
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
            orderNo: 'ORD202401150001',
            userName: '张三',
            userPhone: '138****8001',
            status: 1,
            totalAmount: 7999,
            createTime: '2024-01-15 10:30:00',
            products: [
              {
                name: '苹果iPhone 15 Pro',
                price: 7999,
                quantity: 1,
                image: 'https://via.placeholder.com/100'
              }
            ]
          },
          {
            id: 2,
            orderNo: 'ORD202401150002',
            userName: '李四',
            userPhone: '138****8002',
            status: 2,
            totalAmount: 6999,
            createTime: '2024-01-15 11:20:00',
            products: [
              {
                name: '华为Mate 60 Pro',
                price: 6999,
                quantity: 1,
                image: 'https://via.placeholder.com/100'
              }
            ]
          },
          {
            id: 3,
            orderNo: 'ORD202401150003',
            userName: '王五',
            userPhone: '138****8003',
            status: 0,
            totalAmount: 5999,
            createTime: '2024-01-15 14:10:00',
            products: [
              {
                name: '小米14 Ultra',
                price: 5999,
                quantity: 1,
                image: 'https://via.placeholder.com/100'
              }
            ]
          },
          {
            id: 4,
            orderNo: 'ORD202401150004',
            userName: '赵六',
            userPhone: '138****8004',
            status: 3,
            totalAmount: 14999,
            createTime: '2024-01-14 16:45:00',
            products: [
              {
                name: 'MacBook Pro 14',
                price: 14999,
                quantity: 1,
                image: 'https://via.placeholder.com/100'
              }
            ]
          },
          {
            id: 5,
            orderNo: 'ORD202401150005',
            userName: '孙七',
            userPhone: '138****8005',
            status: 4,
            totalAmount: 8999,
            createTime: '2024-01-14 09:30:00',
            products: [
              {
                name: 'iPad Pro 12.9',
                price: 8999,
                quantity: 1,
                image: 'https://via.placeholder.com/100'
              }
            ]
          }
        ]
        
        // 模拟筛选
        let filteredData = mockData
        if (this.currentStatus !== 'all') {
          filteredData = mockData.filter(item => item.status === this.currentStatus)
        }
        
        if (this.searchKeyword) {
          filteredData = filteredData.filter(item => 
            item.orderNo.includes(this.searchKeyword) ||
            item.userName.includes(this.searchKeyword)
          )
        }
        
        if (isRefresh) {
          this.orderList = filteredData
        } else {
          this.orderList = [...this.orderList, ...filteredData]
        }
        
        this.hasMore = false
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: '/admin/orders',
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
        //   this.orderList = res.data.list
        // } else {
        //   this.orderList = [...this.orderList, ...res.data.list]
        // }
        // 
        // this.hasMore = res.data.hasMore
      } catch (error) {
        console.error('加载订单列表失败:', error)
      } finally {
        this.loading = false
        this.isRefreshing = false
        uni.stopPullDownRefresh()
      }
    },
    
    handleSearch() {
      this.loadOrders(true)
    },
    
    filterByStatus(status) {
      this.currentStatus = status
      this.loadOrders(true)
    },
    
    onRefresh() {
      this.isRefreshing = true
      this.loadOrders(true)
    },
    
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.page++
        this.loadOrders()
      }
    },
    
    getStatusText(status) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    },
    
    getStatusClass(status) {
      const classMap = {
        0: 'pending',
        1: 'to-ship',
        2: 'shipped',
        3: 'completed',
        4: 'cancelled'
      }
      return classMap[status] || ''
    },
    
    async shipOrder(item) {
      uni.showModal({
        title: '提示',
        content: '确定要发货吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              // 模拟发货
              item.status = 2
              
              // 实际API调用（后端实现后启用）
              // await config.request({
              //   url: `/admin/orders/${item.id}/ship`,
              //   method: 'PUT'
              // })
              
              uni.showToast({
                title: '发货成功',
                icon: 'success'
              })
            } catch (error) {
              console.error('发货失败:', error)
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.order-list-container {
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

.filter-scroll {
  background: #FFFFFF;
  white-space: nowrap;
}

.filter-bar {
  display: inline-flex;
  padding: 20rpx;
  gap: 20rpx;
}

.filter-item {
  padding: 15rpx 30rpx;
  font-size: 28rpx;
  color: #666;
  border-radius: 30rpx;
  background: #F5F5F5;
  flex-shrink: 0;
}

.filter-item.active {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
}

.order-scroll {
  flex: 1;
  height: 0;
}

.order-list {
  padding: 20rpx;
}

.order-item {
  background: #FFFFFF;
  border-radius: 15rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.order-no {
  font-size: 26rpx;
  color: #666;
}

.order-status {
  padding: 5rpx 15rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.order-status.pending {
  background: #FFF3E0;
  color: #FF9800;
}

.order-status.to-ship {
  background: #E3F2FD;
  color: #2196F3;
}

.order-status.shipped {
  background: #F3E5F5;
  color: #9C27B0;
}

.order-status.completed {
  background: #E8F5E9;
  color: #4CAF50;
}

.order-status.cancelled {
  background: #FFEBEE;
  color: #F44336;
}

.order-user {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.user-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.user-phone {
  font-size: 24rpx;
  color: #999;
}

.order-products {
  margin-bottom: 20rpx;
}

.product-item {
  display: flex;
  margin-bottom: 15rpx;
}

.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
}

.product-image-placeholder {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  font-size: 26rpx;
  color: #999;
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #F0F0F0;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.total-label {
  font-size: 26rpx;
  color: #666;
}

.total-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #FF6B35;
}

.order-time {
  font-size: 24rpx;
  color: #999;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #F0F0F0;
}

.action-btn {
  padding: 15rpx 40rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
}

.action-btn.primary {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
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
