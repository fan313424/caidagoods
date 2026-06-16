<script>
import config from '@/utils/config.js'

export default {
  onLaunch: function() {
    console.log('财大优选管理端启动')
    
    // 检查登录状态
    if (!config.checkLogin()) {
      uni.reLaunch({
        url: '/pages/login/login'
      })
    }
    
    // 配置网络请求
    uni.addInterceptor('request', {
      fail: (err) => {
        console.error('网络请求失败:', err)
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
      }
    })
  },
  
  onShow: function() {
    console.log('App Show')
  },
  
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style>
/* 引入全局样式 */
@import './common/uni.css';

/* 应用级样式 */
page {
  background-color: #F5F5F5;
}
</style>
