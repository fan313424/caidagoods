/**
 * 应用全局样式
 */

/* 重置样式 */
page {
  background-color: #F5F5F5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  font-size: 28rpx;
  color: #333;
}

/* 通用样式 */
.container {
  padding: 20rpx;
}

/* 按钮样式 */
button {
  border: none;
  padding: 0;
  margin: 0;
  line-height: normal;
}

button::after {
  border: none;
}

/* 文本省略 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* Flex布局 */
.flex {
  display: flex;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 安全区域 */
.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

/* 卡片样式 */
.card {
  background: #FFFFFF;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 20rpx;
}

/* 渐变背景 */
.gradient-primary {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
}

.gradient-success {
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);
}

.gradient-warning {
  background: linear-gradient(135deg, #FF9800 0%, #FFB74D 100%);
}

.gradient-danger {
  background: linear-gradient(135deg, #F44336 0%, #EF5350 100%);
}

/* 文本颜色 */
.text-primary {
  color: #FF6B35;
}

.text-success {
  color: #4CAF50;
}

.text-warning {
  color: #FF9800;
}

.text-danger {
  color: #F44336;
}

.text-muted {
  color: #999;
}

/* 边框 */
.border-top {
  border-top: 1rpx solid #F0F0F0;
}

.border-bottom {
  border-bottom: 1rpx solid #F0F0F0;
}

/* 间距 */
.mt-10 { margin-top: 10rpx; }
.mt-20 { margin-top: 20rpx; }
.mt-30 { margin-top: 30rpx; }
.mb-10 { margin-bottom: 10rpx; }
.mb-20 { margin-bottom: 20rpx; }
.mb-30 { margin-bottom: 30rpx; }
.ml-10 { margin-left: 10rpx; }
.ml-20 { margin-left: 20rpx; }
.mr-10 { margin-right: 10rpx; }
.mr-20 { margin-right: 20rpx; }
.p-10 { padding: 10rpx; }
.p-20 { padding: 20rpx; }
.p-30 { padding: 30rpx; }

/* 字体大小 */
.font-xs { font-size: 22rpx; }
.font-sm { font-size: 26rpx; }
.font-md { font-size: 28rpx; }
.font-lg { font-size: 32rpx; }
.font-xl { font-size: 36rpx; }
.font-bold { font-weight: bold; }
