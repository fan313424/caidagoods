<template>
  <view class="edit-product-container">
    <view class="form-section">
      <view class="form-item">
        <text class="label">商品名称</text>
        <input 
          type="text" 
          v-model="form.name" 
          placeholder="请输入商品名称"
          placeholder-class="placeholder"
        />
      </view>
      
      <view class="form-item">
        <text class="label">商品分类</text>
        <picker 
          mode="selector" 
          :range="categories" 
          :value="categoryIndex"
          @change="onCategoryChange"
        >
          <view class="picker">
            {{ form.category || '请选择分类' }}
            <text class="arrow">></text>
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="label">商品价格</text>
        <input 
          type="digit" 
          v-model="form.price" 
          placeholder="请输入价格"
          placeholder-class="placeholder"
        />
      </view>
      
      <view class="form-item">
        <text class="label">库存数量</text>
        <input 
          type="number" 
          v-model="form.stock" 
          placeholder="请输入库存"
          placeholder-class="placeholder"
        />
      </view>
      
      <view class="form-item">
        <text class="label">商品状态</text>
        <view class="status-switch">
          <view 
            class="status-item" 
            :class="{ active: form.status === 1 }"
            @click="form.status = 1"
          >
            上架
          </view>
          <view 
            class="status-item" 
            :class="{ active: form.status === 0 }"
            @click="form.status = 0"
          >
            下架
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">商品图片</text>
        <view class="image-upload">
          <view class="image-item" v-if="form.imageUrl">
            <image :src="form.imageUrl" mode="aspectFill"></image>
            <view class="delete-btn" @click="deleteImage">×</view>
          </view>
          <view class="upload-btn" v-else @click="chooseImage">
            <text class="plus">+</text>
            <text class="text">上传图片</text>
          </view>
        </view>
      </view>
      
      <view class="form-item textarea-item">
        <text class="label">商品描述</text>
        <textarea 
          v-model="form.description" 
          placeholder="请输入商品描述"
          placeholder-class="placeholder"
          maxlength="500"
        />
      </view>
    </view>
    
    <view class="form-actions">
      <button class="delete-btn" @click="deleteProduct">删除</button>
      <button class="submit-btn" @click="submit" :loading="loading">
        {{ loading ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      productId: null,
      form: {
        name: '',
        category: '',
        price: '',
        stock: '',
        status: 1,
        imageUrl: '',
        description: ''
      },
      categories: ['手机', '电脑', '平板', '配件', '数码', '家电', '服饰', '食品', '图书', '其他'],
      categoryIndex: 0,
      loading: false
    }
  },
  
  onLoad(options) {
    if (options.id) {
      this.productId = options.id
      this.loadProductDetail()
    }
  },
  
  methods: {
    async loadProductDetail() {
      try {
        // 模拟数据
        this.form = {
          name: '苹果iPhone 15 Pro',
          category: '手机',
          price: '7999',
          stock: '50',
          status: 1,
          imageUrl: 'https://via.placeholder.com/200',
          description: '最新款苹果手机，搭载A17 Pro芯片'
        }
        
        this.categoryIndex = this.categories.indexOf(this.form.category)
        
        // 实际API调用（后端实现后启用）
        // const res = await config.request({
        //   url: `/admin/products/${this.productId}`,
        //   method: 'GET'
        // })
        // this.form = res.data
        // this.categoryIndex = this.categories.indexOf(this.form.category)
      } catch (error) {
        console.error('加载商品详情失败:', error)
      }
    },
    
    onCategoryChange(e) {
      this.categoryIndex = e.detail.value
      this.form.category = this.categories[this.categoryIndex]
    },
    
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          this.form.imageUrl = tempFilePath
        }
      })
    },
    
    deleteImage() {
      this.form.imageUrl = ''
    },
    
    validate() {
      if (!this.form.name) {
        uni.showToast({ title: '请输入商品名称', icon: 'none' })
        return false
      }
      
      if (!this.form.category) {
        uni.showToast({ title: '请选择商品分类', icon: 'none' })
        return false
      }
      
      if (!this.form.price || this.form.price <= 0) {
        uni.showToast({ title: '请输入正确的价格', icon: 'none' })
        return false
      }
      
      if (!this.form.stock || this.form.stock < 0) {
        uni.showToast({ title: '请输入正确的库存', icon: 'none' })
        return false
      }
      
      return true
    },
    
    async submit() {
      if (!this.validate()) return
      
      this.loading = true
      
      try {
        // 模拟提交
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 实际API调用（后端实现后启用）
        // await config.request({
        //   url: `/admin/products/${this.productId}`,
        //   method: 'PUT',
        //   data: {
        //     name: this.form.name,
        //     category: this.form.category,
        //     price: parseFloat(this.form.price),
        //     stock: parseInt(this.form.stock),
        //     status: this.form.status,
        //     imageUrl: this.form.imageUrl,
        //     description: this.form.description
        //   }
        // })
        
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (error) {
        console.error('保存商品失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    deleteProduct() {
      uni.showModal({
        title: '提示',
        content: '确定要删除该商品吗？删除后无法恢复',
        success: async (res) => {
          if (res.confirm) {
            try {
              // 模拟删除
              await new Promise(resolve => setTimeout(resolve, 500))
              
              // 实际API调用（后端实现后启用）
              // await config.request({
              //   url: `/admin/products/${this.productId}`,
              //   method: 'DELETE'
              // })
              
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              })
              
              setTimeout(() => {
                uni.navigateBack()
              }, 1500)
            } catch (error) {
              console.error('删除商品失败:', error)
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.edit-product-container {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 20rpx;
  padding-bottom: 200rpx;
}

.form-section {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 15rpx;
  font-weight: bold;
}

.form-item input {
  width: 100%;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.picker {
  width: 100%;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.arrow {
  color: #999;
}

.status-switch {
  display: flex;
  gap: 20rpx;
}

.status-item {
  flex: 1;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #666;
}

.status-item.active {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
}

.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.image-item image {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background: #FF0000;
  color: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  line-height: 1;
}

.upload-btn {
  width: 200rpx;
  height: 200rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #DDD;
}

.plus {
  font-size: 60rpx;
  color: #999;
  line-height: 1;
}

.upload-btn .text {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.textarea-item textarea {
  width: 100%;
  min-height: 200rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.placeholder {
  color: #999;
}

.form-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #FFFFFF;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.delete-btn,
.submit-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  border: none;
}

.delete-btn {
  background: #FF4444;
  color: #FFFFFF;
}

.submit-btn {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
}

.delete-btn::after,
.submit-btn::after {
  border: none;
}
</style>
