<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";


const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    }
)

const handleFileSuccess = (res) =>{
  data.user.avatar=res.data

}


const handleDeleteAvatar = () => {
  data.user.avatar = ''       // 表单里清空

}

const emit = defineEmits(['updateUser'])

const update = () =>{
  let url
  if(data.user.role === 'ADMIN'){
    url = '/admin/update'
  }
  if(data.user.role === 'USER'){
    url = '/user/update'
  }
  request.put(url,data.user).then(res =>{
    if(res.code === '200'){
      ElMessage.success('更新成功')
      setInterval(()=>{
        localStorage.setItem("code_user",JSON.stringify(data.user))
        emit('updateUser')
      },500)

    }
  })
}

</script>

<template>
  <div class="profile-wrapper">
    <!-- 头部：标题 + 头像 -->
    <div class="profile-header">
      <div class="profile-title">个人信息</div>

      <el-upload
          action="/api/files/upload"
          :show-file-list="false"
          :headers="{ token: data.user.token }"
          :on-success="handleFileSuccess"
          list-type="picture"
          class="avatar-uploader"
      >
        <div class="avatar-container">
          <div class="avatar-wrapper">
            <img
                v-if="data.user.avatar"
                :src="data.user.avatar"
                class="avatar"
            />

            <el-icon
                v-if="data.user.avatar"
                @click.stop="handleDeleteAvatar"
                class="delete-btn"
            >
              <Close />
            </el-icon>

            <el-icon
                v-else
                class="avatar-uploader-icon"
            >
              <Plus />
            </el-icon>
          </div>

          <div class="upload-text">
            {{ data.user.avatar ? '点击更换头像' : '点击上传头像' }}
          </div>
        </div>
      </el-upload>
    </div>

    <!-- 表单 -->
    <el-form
        ref="formRef"
        :model="data.user"
        label-width="80px"
        class="profile-form"
    >
      <el-form-item prop="username" label="账号">
        <el-input v-model="data.user.username" placeholder="请输入账号" />
      </el-form-item>

      <el-form-item prop="name" label="名称">
        <el-input v-model="data.user.name" placeholder="请输入名称" />
      </el-form-item>

      <el-form-item prop="phone" label="电话">
        <el-input v-model="data.user.phone" placeholder="请输入电话" />
      </el-form-item>

      <el-form-item prop="email" label="邮箱">
        <el-input v-model="data.user.email" placeholder="请输入邮箱" />
      </el-form-item>
    </el-form>

    <!-- 保存按钮 -->
    <div class="btn-wrapper">
      <el-button type="primary" size="large" @click="update">
        保存
      </el-button>
    </div>
  </div>
</template>

<style scoped>

.profile-wrapper {
  padding: 20px 30px;
}

/* 头部区域 */
.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
}

/* 标题 */
.profile-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
}

/* 表单 */
.profile-form {
  max-width: 1000px;
  margin: 0 auto;
}

/* 按钮 */
.btn-wrapper {
  text-align: center;
  margin-top: 30px;
}

/* 上传组件 */
.avatar-uploader {
  width: 100px;
  height: 100px;
}

/* 头像区域 */
.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 头像 */
.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

/* 删除按钮 */
.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  color: white;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  padding: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrapper:hover .delete-btn {
  opacity: 1;
}

/* + 图标 */
.avatar-uploader-icon {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 1px dashed #d9d9d9;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px;
  color: #409eff;
}

/* 上传文字 */
.upload-text {
  margin-top: 10px;
  font-size: 14px;
  color: #409eff;
  cursor: pointer;
}

.upload-text:hover {
  text-decoration: underline;
}
</style>
