<template>
  <div class="personal-center">
    <div class="personal-center-row">
      <!-- 左侧菜单卡片 -->
      <div class="left-card card1">

        <!-- 我的闲鱼 -->
        <div class="menu-group">
          <div
              class="menu-title"
              @click="setActive('user')"
              :class="{ active: activeMenu==='user' }"
          >
            <div class="icon-container">
              <!-- 父菜单图标/图片放这里 -->
              <img  src="@/assets/imgs/user.png" alt="" class="menu-icon" />
            </div>
            <span class="menu-text">我的闲鱼</span>
          </div>
        </div>

        <!-- 我的交易 -->
        <div class="menu-group">
          <div
              class="menu-title"
              @click="toggle('orders')"
              :class="{
              active: isParentActive('orders') && !hasChildren('orders')
            }"
          >
            <div class="icon-container">
              <!-- 父菜单图标/图片放这里 -->
              <img  src="@/assets/imgs/transaction.png" alt="" class="menu-icon" />
            </div>
            <span class="menu-text">我的交易</span>
            <span class="arrow">{{ expanded.orders ? '▲' : '▼' }}</span>
          </div>
          <!-- 平滑过渡 -->
          <transition name="submenu">
            <div v-show="expanded.orders" class="menu-children">
              <div
                  class="menu-item"
                  :class="{ active: activeMenu==='orders-sold' }"
                  @click="setActive('orders-sold')"
              >
                <span class="menu-text1">我卖出的</span>
              </div>
              <div
                  class="menu-item"
                  :class="{ active: activeMenu==='orders-bought' }"
                  @click="setActive('orders-bought')"
              >
                <span class="menu-text1">我买到的</span>
              </div>
            </div>
          </transition>

        </div>

        <!-- 我的收藏 -->
        <div class="menu-group">
          <div
              class="menu-title"
              @click="setActive('collect')"
              :class="{ active: activeMenu==='collect' }"
          >
            <div class="icon-container">
              <!-- 父菜单图标/图片放这里 -->
              <img  src="@/assets/imgs/collect.png" alt="" class="menu-icon" />
            </div>
            <span class="menu-text">我的收藏</span>
          </div>
        </div>

        <!-- 账号设置 -->
        <div class="menu-group">
          <div
              class="menu-title"
              @click="toggle('account')"
              :class="{
              active: isParentActive('account') && !hasChildren('account')
            }"
          >
            <div class="icon-container">
              <!-- 父菜单图标/图片放这里 -->
              <img  src="@/assets/imgs/setting.png" alt="" class="menu-icon" />
            </div>
            <span class="menu-text">账号设置</span>
            <span class="arrow">{{ expanded.account ? '▲' : '▼' }}</span>
          </div>
          <transition name="submenu">
            <div v-show="expanded.account" class="menu-children">
              <div
                  class="menu-item"
                  :class="{ active: activeMenu==='profile' }"
                  @click="setActive('profile')"
              >
                <span class="menu-text1">个人资料</span>
              </div>
              <div
                  class="menu-item"
                  :class="{ active: activeMenu==='password' }"
                  @click="setActive('password')"
              >
                <span class="menu-text1">修改密码</span>
              </div>
            </div>
          </transition>
        </div>
      </div>

      <!-- 右侧内容卡片 -->
      <div class="right-card card1">
        <component :is="componentMap[activeMenu]" :side-bar="sideBar" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 右侧内容组件
import OrdersSold from '@/views/front/orders/OrdersSold.vue'
import OrdersBought from '@/views/front/orders/OrdersBought.vue'
import Collect from '@/views/front/Collect.vue'
import User from '@/views/front/User.vue'
import UserProfile from '@/views/front/user/UserProfile.vue'
import ChangePassword from '@/views/front/user/ChangePassword.vue'

const route = useRoute()
const router = useRouter()

const sideBar = ref('BUY')

const expanded = reactive({
  orders: true,
  account: true
})

const toggle = (menu) => {
  expanded[menu] = !expanded[menu]
}

const activeMenu = ref()

const setActive = (menu) => {
  activeMenu.value = menu

  if(menu === 'orders-bought') sideBar.value = 'BUY'
  if(menu === 'orders-sold') sideBar.value = 'SELL'

  router.push({ path: '/front/person', query: { tab: menu } })
}

const hasChildren = (parent) => {
  if(parent==='orders') return true
  if(parent==='user') return false
  if(parent==='account') return true
  if(parent==='collect') return false
  return false
}

const isParentActive = (parent) => {
  if(parent==='orders') return ['orders-sold','orders-bought'].includes(activeMenu.value)
  if(parent==='account') return ['profile','password'].includes(activeMenu.value)
  if(parent==='collect') return activeMenu.value==='collect'
  if(parent==='user') return activeMenu.value==='user'
  return false
}

const componentMap = {
  'orders-sold': OrdersSold,
  'orders-bought': OrdersBought,
  collect: Collect,
  user: User,
  profile: UserProfile,
  password: ChangePassword
}

watchEffect(() => {
  activeMenu.value = route.query.tab
})
</script>

<style scoped>
.personal-center {
  padding: 20px;
}

.personal-center-row {
  display: flex;
  gap: 15px; /* 左右卡片间距 */
  align-items: flex-start;
}

.left-card {
  width: 15%;
  margin-left: 50px;
  padding: 20px;
  border-radius: 20px;
  background: #fff;
  box-sizing: border-box;
}

.right-card {
  width: 77%;
  padding: 20px;
  border-radius: 20px;
  background: #fff;
  box-sizing: border-box;
  overflow-y: visible;
  min-height: 476px;
}

/* 菜单间距 */
.menu-group {
  margin-bottom: 10px;
}

/* 父菜单 */
.menu-title {
  font-weight: 600;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  justify-content: flex-start;
  align-items: center; /* 垂直居中 */
  transition: background 0.2s, font-weight 0.2s;
}

.menu-title:hover {
  background: #f0f0f0;
  font-weight: 600;
}

.menu-title.active {
  font-weight: 700;
  background: #f0f0f0;
}

/* 父菜单左侧图片容器 */
.icon-container {
  width: 24px;
  height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
}

/* 子菜单 */
.menu-children {
  margin-top: 8px;


}

.menu-item {
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  margin-bottom: 6px;
  transition: background 0.2s, font-weight 0.2s;
}

.menu-text1 {
  padding-left: 32px;           /* 文字右移32px */
  flex: 1;
}

.menu-item:hover {
  background: #f0f0f0;
  font-weight: 600;
}

.menu-item.active {
  background: #f0f0f0;
  font-weight: 700;
  color: #333;
}

/* 箭头 */
.arrow {
  font-size: 12px;
  margin-left: auto;
}

/* 父菜单文字 */
.menu-text {
  flex: 1;
}

/* 子菜单展开/收起动画 */
.submenu-enter-from,
.submenu-leave-to {
  max-height: 0;
  opacity: 0;
}

.submenu-enter-to,
.submenu-leave-from {
  max-height: 500px; /* 最大高度根据子菜单内容调整 */
  opacity: 1;
}

.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.3s ease;
}

</style>
