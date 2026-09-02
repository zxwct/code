<template>
  <div class="bg">
    <div style="width: 350px; background-color:#5e7ced;border-radius: 5px;box-shadow: 0 0 10px rgba(0,0,0,0.2); padding: 20px">
      <el-form status-icon  ref="formRef" :model="data.form" :rules="data.rules" >
        <div style="margin-bottom: 20px;text-align: center;font-weight: bold;font-size: 24px">欢迎注册</div>
        <el-form-item prop="username" >
          <el-input size="large" v-model="data.form.username" autocomplete="off" prefix-icon="User" placeholder="请输入账号"/>
        </el-form-item>
        <el-form-item prop="password" >
          <el-input size="large" show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock" placeholder="请输入密码"/>
        </el-form-item>
        <el-form-item prop="confirmPassword" >
          <el-input size="large" show-password v-model="data.form.confirmPassword" autocomplete="off" prefix-icon="Lock" placeholder="请确认密码"/>
        </el-form-item>
        <div style="margin-bottom: 20px">
          <el-button style="width: 100%" size="large" type="success" @click="register">注册</el-button>
        </div>
        <div style="text-align: right">
          还没有账号？请<a style="color: #67C23AFF" href="/login">登录</a>
        </div>

        </el-form>
    </div>

  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";


const validatePass = (rule, value, callback) => {
  if (value !== data.form.password) {
    callback(new Error("两次输入密码不匹配"))
  } else {
    callback()
  }
}


const formRef =ref()
const data =reactive({
  form:{},
  rules:{
    username:[
      {required: true ,message: '请输入账号',trigger: 'blur'},
    ],
    password:[
      {required: true ,message: '请输入密码',trigger: 'blur'}
    ],
    confirmPassword:[
      {required: true ,message: '请再次确认密码',trigger: 'blur'},
      { validator: validatePass ,trigger: 'blur'}
    ],
  },
})

const register =() =>{
  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/register', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

</script>

<style scoped>
.bg{
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg2.jpg");
  background-size: cover;
}
</style>