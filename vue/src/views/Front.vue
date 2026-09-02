
<template>


  <div class="wrapper">
    <!-- 黄色标题区 -->
    <div class="header">

      <div class="logo" @click="router.push({ path: '/front/home', query: {} })">
        <img src="@/assets/imgs/xianyu.png" alt="Logo">
      </div>


      <div class="search">
        <input v-model="keyword" placeholder="搜索你想要的商品" @keyup.enter="toSearch"/>
        <button @click="toSearch">
          <span class="icon">
            <img src="@/assets/imgs/search.png" alt="">
          </span>
          搜索
        </button>
      </div>


      <div class="user">
        <!-- 已登录显示头像 + 下拉菜单 -->
        <template v-if="data.user && data.user.id">
          <el-dropdown trigger="click">
            <div style="display: flex; align-items: center; cursor: pointer;">
              <img
                  class="avatar"
                  :src="data.user.avatar || defaultAvatar"
                  alt="头像"
              />
              <span style="margin-left: 5px">{{ data.user.username || data.user.name }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push({ path: '/front/person', query: { tab: 'profile' } })">个人信息</el-dropdown-item>
                <el-dropdown-item @click="router.push({ path: '/front/person', query: { tab: 'password' } })">修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <!-- 未登录显示图标 + 登录 -->
        <template v-else>
          <div style="display: flex; align-items: center; cursor: pointer;" @click="router.push('/login')">
            <el-icon>
              <User/>
            </el-icon>
            <span style="margin-left: 5px">登录</span>
          </div>
        </template>
      </div>

    </div>

    <!--渲染区域开始 -->
    <div class="main-content">
      <RouterView @updateUser="updateUser"/>
    </div>

    <!--渲染区域结束 -->

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-left">
          <span>© 2025 闲鱼商城</span>
          <span class="divider">|</span>
          <a href="#">关于我们</a>
          <a href="#">联系我们</a>
          <a href="#">帮助中心</a>
        </div>

        <div class="footer-right">
          <span>ICP备案号：京ICP备12345678号</span>
        </div>
      </div>
    </footer>

    <div class="side-bar">
      <div
          class="side-bar-item"
          :class="{ active: route.path === '/front/publish' }"
          @click="checkLogin(() => router.push('/front/publish'))"
      >
        <el-icon>
          <Plus/>
        </el-icon>
        <span>发布</span>
      </div>

      <div
          class="side-bar-item"
          :class="{ active: route.path === '/front/chat' }"
          @click="router.push('/front/chat')"
      >
        <el-icon>
          <ChatDotRound/>
        </el-icon>
        <span>消息</span>
      </div>

      <div
          class="side-bar-item"
          :class="{ active: route.query.tab === 'orders-bought' }"
          @click="checkLogin(() => router.push({
          path: '/front/person',
          query: { tab: 'orders-bought' }
        }))"
      >
        <el-icon>
          <ShoppingCart/>
        </el-icon>
        <span>订单</span>
      </div>

      <div
          class="side-bar-item"
          :class="{ active: route.query.tab === 'collect' }"
          @click="checkLogin(() => router.push({
          path: '/front/person',
          query: { tab: 'collect' }
        }))"
      >
        <el-icon>
          <Collection/>
        </el-icon>
        <span>收藏</span>
      </div>

      <div
          class="side-bar-item"
          :class="{ active: route.query.tab === 'user' }"
          @click="checkLogin(() => router.push({
          path: '/front/person',
          query: { tab: 'user' }
        }))"
      >
        <el-icon>
          <User/>
        </el-icon>
        <span>我的</span>
      </div>

    </div>
  </div>

</template>

<script setup>

import {reactive, ref} from 'vue'
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import { ElMessage } from "element-plus";

const keyword = ref('')

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}"),

})


const router = useRouter();
const route = useRoute();

const defaultAvatar = '@/assets/imgs/OIP.jpg'

const logout = () => {
  localStorage.removeItem('code_user');
  data.user = {}
  router.push('/front/home') // 跳转到登录页
}

watch(
    () => route.query.keyword,
    (val) => {
      keyword.value = val || ''
    },
    {immediate: true}
)

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem("code_user") || '{}')
}

const toSearch = () => {
  router.push({
    path: '/front/search',
    query: {
      categoryId: 0,
      keyword: keyword.value || undefined
    }
  })
}
const checkLogin = (callback) => {
  if (data.user && data.user.id) {
    callback()  // 已登录就执行传入函数
  } else {
    ElMessage.warning("请先登录")
  }
}

</script>

<style scoped>

.wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* 至少一屏 */
  background: #f7f7f7;
  padding: 5px; /* 保留原来的 padding */
  box-sizing: border-box; /* 确保 padding 不影响高度计算 */
}

/* 渲染区域容器撑满剩余空间 */
.wrapper > .main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}


.header {
  display: flex;
  align-items: center;
  background: #ffe200;
  padding: 18px 72px;
  border-radius: 10px 10px 0 0;
}


.header h1 {
  margin: 0;
  font-size: 22px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px; /* 根据你布局调整 */
  cursor: pointer;
  padding-left: 20px;
}

.logo img {
  height: 40px; /* 控制 logo 高度 */
  width: auto;
  object-fit: contain;
}


.search {
  position: relative;
  margin: 0 auto;
  width: 780px;
}

.search input {
  width: 100%;
  height: 42px;
  padding: 0 90px 0 18px; /* 右边留给按钮 */
  border: 2px solid #fff;
  border-radius: 21px;
  font-size: 14px;
  outline: none;
  background: #fff;
}

.search input:focus {
  border-color: black;
}


.search button {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  height: 30px;
  padding: 0 14px;
  border: none;
  border-radius: 15px;
  background: #ffe200;
  color: black;
  font-size: 14px;
  cursor: pointer;

  display: flex;
  align-items: center;
  gap: 4px;
}

.icon {
  display: flex;
  align-items: center;
}

.icon img {
  width: 14px;
  height: 14px;
}

.user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  padding-right: 20px;
}

.user .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user el-icon {
  font-size: 18px;
  color: #333;
}


.footer {

  padding: 20px 0;
  background: #f5f5f5;
  color: #666;
  font-size: 13px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-left a {
  color: #666;
  text-decoration: none;
}

.footer-left a:hover {
  color: #000;
}

.divider {
  color: #ccc;
}

.footer-right {
  color: #999;
}


.side-bar {
  width: 70px;
  height: 370px;
  background: #fff;
  position: fixed;
  right: 8px;
  bottom: 30%;
  border-radius: 20px;
  padding: 10px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.side-bar-item {
  position: relative;
  width: 100%;
  height: 70px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.side-bar-item:not(:last-child) {
  border-bottom: 1px solid #eee;
}

/* 图标 */
.side-bar-item .el-icon {
  font-size: 22px;
  margin-bottom: 6px;
}

/* 鼠标悬停效果 */
.side-bar-item:hover {
  background-color: #ffe610;
}

/* 选中状态 */
.side-bar-item.active {
  background-color: #ffe610;
}

/* 左侧黑色竖线 */
.side-bar-item.active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 15%;
  width: 3px;
  height: 70%;
  background-color: #000;
  border-radius: 2px;
}


</style>