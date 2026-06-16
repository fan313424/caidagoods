<template>
  <view class="product-list-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索商品名称"
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
        已上架
      </view>
      <view 
        class="filter-item" 
        :class="{ active: currentStatus === 0 }"
        @click="filterByStatus(0)"
      >
        已下架
      </view>
    </view>
    
    <!-- 商品列表 -->
    <scroll-view 
      class="product-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="product-list">
        <view 
          class="product-item" 
          v-for="item in productList" 
          :key="item.id"
          @click="editProduct(item)"
        >
          <view class="product-image-placeholder" v-if="!item.imageUrl">📦</view>
          <image class="product-image" v-else :src="item.imageUrl" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-category">{{ item.category || '未分类' }}</text>
            <view class="product-price-stock">
              <text class="product-price">¥{{ item.price }}</text>
              <text class="product-stock">库存: {{ item.stock }}</text>
            </view>
          </view>
          <view class="product-actions">
            <view 
              class="action-btn" 
              :class="{ 'off-shelf': item.status === 0 }"
              @click.stop="toggleStatus(item)"
            >
              {{ item.status === 1 ? '下架' : '上架' }}
            </view>
          </view>
        </view>
      </view>
      
      <view class="loading-tip" v-if="loading">
        <text>加载中...</text>
      </view>
      
      <view class="no-more" v-if="!hasMore && productList.length > 0">
        <text>没有更多了</text>
      </view>
      
      <view class="empty" v-if="!loading && productList.length === 0">
        <text>暂无商品数据</text>
      </view>
    </scroll-view>
    
    <!-- 添加按钮 -->
    <view class="add-btn" @click="addProduct">
      <text>+</text>
    </view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      searchKeyword: '',
      currentStatus: 'all',
      productList: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      loading: false,
      isRefreshing: false
    }
  },
  
  onLoad() {
    this.loadProducts()
  },
  
  onPullDownRefresh() {
    this.onRefresh()
  },
  
  methods: {
    async loadProducts(isRefresh = false) {
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
            name: '苹果iPhone 15 Pro',
            description: '最新款苹果手机',
            price: 7999,
            imageUrl: 'https://via.placeholder.com/200',
            category: '手机',
            stock: 50,
            status: 1
          },
          {
            id: 2,
            name: '华为Mate 60 Pro',
            description: '华为旗舰手机',
            price: 6999,
            imageUrl: 'https://via.placeholder.com/200',
            category: '手机',
            stock: 30,
            status: 1
          },
          {
            id: 3,
            name: '小米14 Ultra',
            description: '小米高端旗舰',
            price: 5999,
            imageUrl: 'https://via.placeholder.com/200',
            category: '手机',
            stock: 0,
            status: 0
          },
          {
            id: 4,
            name: 'MacBook Pro 14',
            description: '苹果笔记本电脑',
            price: 14999,
            imageUrl: 'https://via.placeholder.com/200',
            category: '电脑',
            stock: 20,
            status: 1
          },
          {
            id: 5,
            name: 'iPad Pro 12.9',
            description: '苹果平板电脑',
            price: 8999,
            imageUrl: 'https://via.placeholder.com/200',
            category: '平板',
            stock: 15,
            status: 1
          }
        ]
        
        // 模拟分页和筛选
        let filteredData = mockData
        if (this.currentStatus !== 'all') {
          filteredData = mockData.filter(item => item.status === this.currentStatus)
        }
        
        if (this.searchKeyword) {
          filteredData = filteredData.filter(item => 
            item.name.includes(this.searchKeyword)
          )
        }
        
        if (isRefresh) {
          this.productList = filteredData
        } else {
          this.productList = [...this.productList, ...filteredData]
        }
        
        this.hasMore = false // 模拟数据没有更多
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: '/admin/products',
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
        //   this.productList = res.data.list
        // } else {
        //   this.productList = [...this.productList, ...res.data.list]
        // }
        // 
        // this.hasMore = res.data.hasMore
      } catch (error) {
        console.error('加载商品列表失败:', error)
      } finally {
        this.loading = false
        this.isRefreshing = false
        uni.stopPullDownRefresh()
      }
    },
    
    handleSearch() {
      this.loadProducts(true)
    },
    
    filterByStatus(status) {
      this.currentStatus = status
      this.loadProducts(true)
    },
    
    onRefresh() {
      this.isRefreshing = true
      this.loadProducts(true)
    },
    
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.page++
        this.loadProducts()
      }
    },
    
    addProduct() {
      uni.navigateTo({
        url: '/pages/product/add'
      })
    },
    
    editProduct(item) {
      uni.navigateTo({
        url: `/pages/product/edit?id=${item.id}`
      })
    },
    
    async toggleStatus(item) {
      const newStatus = item.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '上架' : '下架'
      
      uni.showModal({
        title: '提示',
        content: `确定要${action}该商品吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              // 模拟更新
              item.status = newStatus
              
              // 实际API调用（后端实现后启用）
              // await config.request({
              //   url: `/admin/products/${item.id}/status`,
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
.product-list-container {
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

.product-scroll {
  flex: 1;
  height: 0;
}

.product-list {
  padding: 20rpx;
}

.product-item {
  display: flex;
  background: #FFFFFF;
  border-radius: 15rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.product-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
}

.product-image-placeholder {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-category {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.product-price-stock {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.product-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #FF6B35;
}

.product-stock {
  font-size: 24rpx;
  color: #999;
}

.product-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  padding: 15rpx 30rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);
  color: #FFFFFF;
  border-radius: 30rpx;
  font-size: 24rpx;
}

.action-btn.off-shelf {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
}

.loading-tip,
.no-more,
.empty {
  text-align: center;
  padding: 40rpx;
  font-size: 28rpx;
  color: #999;
}

.add-btn {
  position: fixed;
  right: 40rpx;
  bottom: 120rpx;
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.4);
}

.add-btn text {
  font-size: 60rpx;
  color: #FFFFFF;
  line-height: 1;
}
</style>
