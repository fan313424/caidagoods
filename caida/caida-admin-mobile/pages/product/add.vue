<template>
  <view class="add-product-container">
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
      <button class="cancel-btn" @click="cancel">取消</button>
      <button class="submit-btn" @click="submit" :loading="loading">
        {{ loading ? '提交中...' : '提交' }}
      </button>
    </view>
  </view>
</template>

<script>
import config from '@/utils/config.js'

export default {
  data() {
    return {
      form: {
        name: '',
        category: '',
        price: '',
        stock: '',
        imageUrl: '',
        description: ''
      },
      categories: ['手机', '电脑', '平板', '配件', '数码', '家电', '服饰', '食品', '图书', '其他'],
      loading: false
    }
  },
  
  methods: {
    onCategoryChange(e) {
      this.form.category = this.categories[e.detail.value]
    },
    
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          // 这里应该上传到服务器，暂时使用本地路径
          this.form.imageUrl = tempFilePath
          
          // 实际上传代码（后端实现后启用）
          // uni.uploadFile({
          //   url: config.API_BASE_URL + '/admin/upload',
          //   filePath: tempFilePath,
          //   name: 'file',
          //   header: {
          //     'Authorization': `Bearer ${uni.getStorageSync(config.ADMIN_TOKEN_KEY)}`
          //   },
          //   success: (uploadRes) => {
          //     const data = JSON.parse(uploadRes.data)
          //     if (data.code === 200) {
          //       this.form.imageUrl = data.data.url
          //     }
          //   }
          // })
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
        //   url: '/admin/products',
        //   method: 'POST',
        //   data: {
        //     name: this.form.name,
        //     category: this.form.category,
        //     price: parseFloat(this.form.price),
        //     stock: parseInt(this.form.stock),
        //     imageUrl: this.form.imageUrl,
        //     description: this.form.description
        //   }
        // })
        
        uni.showToast({
          title: '添加成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (error) {
        console.error('添加商品失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    cancel() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.add-product-container {
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

.cancel-btn,
.submit-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  border: none;
}

.cancel-btn {
  background: #F5F5F5;
  color: #666;
}

.submit-btn {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: #FFFFFF;
}

.cancel-btn::after,
.submit-btn::after {
  border: none;
}
</style>
