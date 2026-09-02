
<template>
<div class="card">
  <div style="font-size: 16px">个人信息</div>
  <el-form  ref="formRef" :model="data.user"  label-width="80px" style="padding: 20px 30px 10px 0">
    <el-form-item prop="username" label="账号" >
      <el-input v-model="data.user.username" autocomplete="off" placeholder="请输入账号" />
    </el-form-item>
    <el-form-item prop="name" label="名称" >
      <el-input v-model="data.user.name" autocomplete="off" placeholder="请输入名称" />
    </el-form-item>
    <el-form-item prop="phone" label="电话">
      <el-input v-model="data.user.phone" autocomplete="off" placeholder="请输入电话" />
    </el-form-item>
    <el-form-item prop="email" label="邮箱">
      <el-input v-model="data.user.email" autocomplete="off" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item prop="avatar" label="头像">
      <el-upload
          action="/api/files/upload"
          :show-file-list="false"
          :headers="{token: data.user.token}"
          :on-success="handleFileSuccess"
          list-type="picture"
          class="avatar-uploader"
      >
        <template #default>
          <div class="avatar-wrapper">
            <!-- 显示头像 -->
            <img
                v-if="data.user.avatar"
                :src="data.user.avatar"
                class="avatar"
            />

            <!-- 删除按钮（默认隐藏，hover 显示） -->
            <el-icon
                v-if="data.user.avatar"
                @click.stop="handleDeleteAvatar"
                class="delete-btn"
            >
              <Close />
            </el-icon>

            <!-- 如果没有头像，就显示上传按钮 -->
            <el-icon
                v-else
                class="avatar-uploader-icon"
            >
              <Plus />
            </el-icon>
          </div>
        </template>
      </el-upload>
    </el-form-item>
  </el-form>
  <div style="text-align: center">
    <el-button type="primary" style="padding: 20px 30px" @click="update">保存</el-button>
  </div>
</div>

</template>


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
        location.href = '/manager/home'
      },500)

    }
  })
}

</script>


<style scoped>

/* 固定行高 */
.el-table .avatar-column .avatar-cell {
  width: 40px;
  height: 40px;        /* 固定高度 */
  display: flex;
  justify-content: center;
  align-items: center;  /* 图片居中 */
}

/* el-image 占满父容器 */
.el-table .avatar-column .avatar-cell >>> .el-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}


/* 固定上传组件容器 */
.avatar-uploader {
  width: 100px;
  height: 100px;
  display: inline-block;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 0; /* 消除默认行高 */
}

.avatar-wrapper {
  position: relative;
  display: flex;             /* flex 布局 */
  justify-content: center;    /* 居中 */
  align-items: center;        /* 居中 */
  width: 100px;
  height: 100px;
  overflow: hidden;           /* 防止撑开 */
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;          /* 裁剪填充 */

}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  color: white;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  padding: 2px;
  opacity: 0;               /* 默认隐藏 */
  transition: opacity 0.2s; /* 平滑显示 */
  z-index: 10;  /* 确保在最上层 */
}

.avatar-wrapper:hover .delete-btn {
  opacity: 1;  /* 鼠标移入头像区域时显示 */
}

.avatar-uploader-icon {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px; /* 控制 Plus 图标大小 */
  color: #409EFF;  /* Element Plus 默认主题色 */
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}

</style>
