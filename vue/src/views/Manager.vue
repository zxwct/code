<template>
  <div style="height: 100vh; display: flex; flex-direction: column;"> <!-- 改动 START：整个顶层撑满屏幕 -->
    <!-- 头部区域开始 -->
    <div style="height: 60px; display: flex; flex-shrink: 0;"> <!-- 改动 START：加 flex-shrink -->
      <div style="width: 200px; display: flex; align-items: center; padding-left: 20px; background-color: #92a6ed;">
        <img style="width: 40px;height: 40px;border-radius: 50%" src="@/assets/imgs/logo.png" alt="">
        <span style="font-size: 20px;font-weight: bold;color: #f9f9fa;margin-left: 5px">管理系统</span>
      </div>
      <div style="flex: 1; border-bottom: 1px solid #ddd; display: flex; align-items: center; padding-left: 20px;">
        <span style="margin-right: 5px; cursor: pointer" @click="router.push('/manager/home')">首页</span> /
        <span style="margin-left: 5px">{{ router.currentRoute.value.meta.name }}</span>
      </div>
      <div
          style="width: fit-content; display: flex; align-items: center; padding-right: 20px; border-bottom: 1px solid #ddd;">
        <el-dropdown>
          <div style="display: flex; align-items: center;">
            <img v-if="data.user?.avatar" :src="data.user.avatar" style="width: 40px; height: 40px; border-radius: 50%">
            <img v-else style="width: 40px; height: 40px; border-radius: 50%" src="@/assets/imgs/OIP.jpg">
            <span style="margin-left: 5px">{{ data.user?.name }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- 头部区域结束 -->

    <!-- 下方区域开始 -->
    <div style="flex: 1; display: flex; overflow: hidden;"> <!-- 改动 START：flex 布局撑满剩余空间 -->
      <!-- 菜单区域开始 -->
      <div style="width: 200px; height: 100%; overflow-y: auto; border-right: 1px solid #ddd;">
        <!-- 改动 START：菜单撑满高度 + 可滚动 -->
        <el-menu
            router
            :default-openeds="['1','2']"
            :default-active="router.currentRoute.value.path"
            style="height: 100%;" 
        >
        <el-menu-item index="/manager/home">
          <el-icon>
            <House/>
          </el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <Monitor/>
            </el-icon>
            <span>信息管理</span>
          </template>
          <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">系统公告</el-menu-item>
          <el-menu-item index="/manager/notice" v-else>公告信息</el-menu-item>
          <el-menu-item index="/manager/category">商品分类</el-menu-item>
          <el-menu-item index="/manager/banner">轮播图管理</el-menu-item>
          <el-menu-item index="/manager/goods">闲置物品管理</el-menu-item>
          <el-menu-item index="/manager/address">收货地址管理</el-menu-item>
          <el-menu-item index="/manager/orders">订单管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
          <template #title>
            <el-icon>
              <User/>
            </el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
          <el-menu-item index="/manager/user">普通用户信息</el-menu-item>
        </el-sub-menu>
        </el-menu>
      </div>
      <!-- 菜单区域结束 -->

      <!-- 渲染区域开始 -->
      <div style="flex: 1; padding: 10px; background-color: #fbfbfb; overflow-y: auto;">
        <!-- 改动 START：右侧区域 flex + 可滚动 -->
        <RouterView @updateUser="updateUser"/>
      </div>
      <!-- 渲染区域结束 -->
    </div>
    <!-- 下方区域结束 -->
  </div> <!-- 改动 END：顶层 flex 布局 -->
</template>


<script setup>

import router from "@/router/index.js";
import {reactive} from "vue";


const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}")
})


const logout = () => {
  localStorage.removeItem('code_user');
  location.href = '/login'  // 跳转到登录页
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem("code_user") || '{}')
}

// if(!data.user?.id) {
//   location.href = '/login'
// }


</script>

<style>
.el-menu {
  background-color: #92a6ed;
  border: none;
}

.el-sub-menu__title {
  color: #ddd;
  background-color: #92a6ed;
}

.el-menu-item {
  height: 50px;
  color: #ddd;
}

.el-menu .is-active {
  background-color: #3758d3;
  color: #fff;
}

.el-sub-menu__title:hover {
  background-color: #92a6ed;
}

.el-menu-item:not(.is-active):hover {
  background-color: #5bdd51;
  color: #333;
}

.el-dropdown {
  cursor: pointer;
}

.el-tooltip__trigger {
  outline: none;
}

.el-menu--inline .el-menu-item {
  padding-left: 50px !important;
}

</style>