<template>
  <view class="employee-list-container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索员工姓名/编号"
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
    
    <!-- 员工列表 -->
    <scroll-view 
      class="employee-scroll" 
      scroll-y 
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="employee-list">
        <view 
          class="employee-item" 
          v-for="item in employeeList" 
          :key="item.id"
        >
          <view class="employee-header">
            <view class="employee-info">
              <view class="name-row">
                <text class="employee-name">{{ item.name }}</text>
                <view class="employee-status" :class="{ disabled: item.status === 0 }">
                  {{ item.status === 1 ? '正常' : '已禁用' }}
                </view>
              </view>
              <text class="employee-no">员工编号: {{ item.employeeNo }}</text>
            </view>
          </view>
          
          <view class="employee-details">
            <view class="detail-item">
              <text class="detail-label">部门:</text>
              <text class="detail-value">{{ item.department || '未分配' }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">职位:</text>
              <text class="detail-value">{{ item.position || '未分配' }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">手机:</text>
              <text class="detail-value">{{ item.phone || '未绑定' }}</text>
            </view>
            <view class="detail-item">
              <text class="detail-label">邮箱:</text>
              <text class="detail-value">{{ item.email || '未绑定' }}</text>
            </view>
          </view>
          
          <view class="employee-actions">
            <view 
              class="action-btn" 
              :class="{ 'enable-btn': item.status === 0 }"
              @click="toggleEmployeeStatus(item)"
            >
              {{ item.status === 1 ? '禁用' : '启用' }}
            </view>
          </view>
        </view>
      </view>
      
      <view class="loading-tip" v-if="loading">
        <text>加载中...</text>
      </view>
      
      <view class="no-more" v-if="!hasMore && employeeList.length > 0">
        <text>没有更多了</text>
      </view>
      
      <view class="empty" v-if="!loading && employeeList.length === 0">
        <text>暂无员工数据</text>
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
      employeeList: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      loading: false,
      isRefreshing: false
    }
  },
  
  onLoad() {
    this.loadEmployees()
  },
  
  onPullDownRefresh() {
    this.onRefresh()
  },
  
  methods: {
    async loadEmployees(isRefresh = false) {
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
            employeeNo: 'EMP001',
            name: '张经理',
            department: '运营部',
            position: '运营经理',
            phone: '13800138001',
            email: 'zhangmanager@example.com',
            status: 1,
            createTime: '2024-01-10 09:00:00'
          },
          {
            id: 2,
            employeeNo: 'EMP002',
            name: '李主管',
            department: '销售部',
            position: '销售主管',
            phone: '13800138002',
            email: 'lisupervisor@example.com',
            status: 1,
            createTime: '2024-01-11 10:30:00'
          },
          {
            id: 3,
            employeeNo: 'EMP003',
            name: '王专员',
            department: '客服部',
            position: '客服专员',
            phone: '13800138003',
            email: 'wangspecialist@example.com',
            status: 0,
            createTime: '2024-01-12 14:20:00'
          },
          {
            id: 4,
            employeeNo: 'EMP004',
            name: '赵助理',
            department: '行政部',
            position: '行政助理',
            phone: '13800138004',
            email: 'zhaoassistant@example.com',
            status: 1,
            createTime: '2024-01-13 11:15:00'
          },
          {
            id: 5,
            employeeNo: 'EMP005',
            name: '孙财务',
            department: '财务部',
            position: '财务专员',
            phone: '13800138005',
            email: 'sunfinance@example.com',
            status: 1,
            createTime: '2024-01-14 16:45:00'
          }
        ]
        
        // 模拟筛选
        let filteredData = mockData
        if (this.currentStatus !== 'all') {
          filteredData = mockData.filter(item => item.status === this.currentStatus)
        }
        
        if (this.searchKeyword) {
          filteredData = filteredData.filter(item => 
            item.name.includes(this.searchKeyword) ||
            item.employeeNo.includes(this.searchKeyword)
          )
        }
        
        if (isRefresh) {
          this.employeeList = filteredData
        } else {
          this.employeeList = [...this.employeeList, ...filteredData]
        }
        
        this.hasMore = false
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: '/admin/employees',
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
        //   this.employeeList = res.data.list
        // } else {
        //   this.employeeList = [...this.employeeList, ...res.data.list]
        // }
        // 
        // this.hasMore = res.data.hasMore
      } catch (error) {
        console.error('加载员工列表失败:', error)
      } finally {
        this.loading = false
        this.isRefreshing = false
        uni.stopPullDownRefresh()
      }
    },
    
    handleSearch() {
      this.loadEmployees(true)
    },
    
    filterByStatus(status) {
      this.currentStatus = status
      this.loadEmployees(true)
    },
    
    onRefresh() {
      this.isRefreshing = true
      this.loadEmployees(true)
    },
    
    loadMore() {
      if (this.hasMore && !this.loading) {
        this.page++
        this.loadEmployees()
      }
    },
    
    async toggleEmployeeStatus(item) {
      const newStatus = item.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '启用' : '禁用'
      
      uni.showModal({
        title: '提示',
        content: `确定要${action}该员工吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              // 模拟更新
              item.status = newStatus
              
              // 实际API调用（后端实现后启用）
              // await config.request({
              //   url: `/admin/employees/${item.id}/status`,
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
.employee-list-container {
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

.employee-scroll {
  flex: 1;
  height: 0;
}

.employee-list {
  padding: 20rpx;
}

.employee-item {
  background: #FFFFFF;
  border-radius: 15rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.employee-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.employee-info {
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 15rpx;
  margin-bottom: 10rpx;
}

.employee-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.employee-status {
  padding: 5rpx 15rpx;
  background: #E8F5E9;
  color: #4CAF50;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.employee-status.disabled {
  background: #FFEBEE;
  color: #F44336;
}

.employee-no {
  font-size: 24rpx;
  color: #999;
}

.employee-details {
  margin-bottom: 20rpx;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 10rpx;
}

.detail-label {
  font-size: 26rpx;
  color: #999;
  width: 100rpx;
}

.detail-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.employee-actions {
  display: flex;
  justify-content: flex-end;
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
