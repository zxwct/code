<script setup >
import {computed, reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import {regionData} from "element-china-area-data";


const router = useRouter();
const route = useRoute();
const formRef = ref()



const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      tableData: [],
      categoryData: [],
      goodsData: {},
      form: {},
      formVisible: false,
      rules: {
        name: [
          {required: true, message: '请填写联系人', trigger: 'blur'}
        ],
        address: [
          {required: true, message: '请选择所在地区', trigger: 'blur'}
        ],
        info: [
          {required: true, message: '请填写详细地址', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请填写手机号', trigger: 'blur'},
          {
            pattern: /^1\d{10}$/,
            message: "请输入正确的11位手机号",
            trigger: "blur"
          }
        ],
        userId: [
          {required: true, message: '请选择用户', trigger: 'blur'}
        ],
      },

    }
)

const load = () => {
  request.get('/address/selectByUserId').then(res => {
    if (res.code === '200') {
      data.tableData = res.data

    } else {
      ElMessage.error(res.msg)
    }
  })
}

load()

const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) =>{
  data.form=JSON.parse(JSON.stringify(row))

  if(data.form.address){
    data.form.address= data.form.address.split('/');
  }
  else {
    data.form.address=[];
  }

  data.formVisible=true
}

const add = () =>{
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.post('/address/add',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('新增成功')
          data.formVisible=false
          load()
        }else{
          ElMessage.error(res.msg)
        }
      })
    }
  })

}

const update = () => {
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.put('/address/update',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('修改成功')
          data.formVisible=false

          load()
        }else{
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const save = () =>{

  if(data.form.address){
    data.form.address =data.form.address.join('/');
  }
  else {
    data.form.address = '';
  }

  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？','删除确认',{type :'warning'}).then(res =>{
    request.delete('/address/delete/' + id).then(res =>{
      if(res.code === '200'){
        ElMessage.success('删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err =>{})
}

</script>

<template>
  <!-- 顶部卡片 -->
  <div class="card top-card">
    <div class="top-card-content">
      <div class="title">收货地址管理</div>
      <button class="add-btn" @click="handleAdd">+新增地址</button>
    </div>
  </div>

  <!-- 地址列表 -->
  <div class="address-container">
    <div
        v-for="address in data.tableData"
        :key="address.id"
        class="card1 address-card"
    >
      <!-- 第一行：联系人/电话 & 编辑删除 -->
      <div class="address-row first-row">
        <div class="contact">
          <span class="name">{{ address.name }}</span>
          <span class="phone">{{ address.phone }}</span>
        </div>
        <div class="actions">
          <button class="edit-btn" @click="handleEdit(address)">编辑</button>
          <button class="delete-btn" @click="del(address.id)">删除</button>
        </div>
      </div>

      <!-- 第二行：图标 + 地区 + 详细地址 -->
      <div class="address-row second-row">
        <div class="icon"><img src="@/assets/imgs/address.png"></div>
        <div class="region">{{ address.address }}</div>
        <div class="detail">{{ address.info }}</div>
      </div>
    </div>
  </div>


  <el-dialog v-model="data.formVisible" title="收货地址信息" width="30%" destroy-on-close>

    <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">
      <el-form-item prop="name" label="联系人">
        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入姓名"/>
      </el-form-item>

      <el-form-item prop="address" label="所在地区">
        <el-cascader
            v-model="data.form.address"
            :options="regionData"
            :props="{value: 'label'}"
            placeholder="请选择省市区"
            clearable
            style="width: 100%"
        />
      </el-form-item>

      <el-form-item prop="info" label="详细地址">
        <el-input v-model="data.form.info" autocomplete="off" placeholder="请输入小区、写字楼、门牌号等"/>
      </el-form-item>

      <el-form-item prop="phone" label="联系电话">
        <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入手机号"/>
      </el-form-item>

    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="data.formVisible = false">取消</el-button>
        <el-button type="primary" @click="save">
          保 存
        </el-button>
      </div>
    </template>
  </el-dialog>

</template>

<style scoped>
.card.top-card {
  background: #fff;
  border-radius: 20px;
  padding: 30px 20px;  /* 上下 padding 加大，整体更高 */
  margin: 20px auto 10px;
  width: 80%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin: 10px auto 10px;
  width: 80%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 顶部卡片布局 */
.top-card .top-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-card .title {
  font-size: 24px;
  font-weight: bold;
}

.add-btn {
  padding: 10px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.add-btn:hover {
  background-color: #66b1ff;
}

/* 地址列表 */
.address-container {
  display: flex;
  flex-direction: column;

}

/* 地址卡片内部布局 */
.address-card .address-row {
  display: flex;
  align-items: center;

}
.first-row {
  display: flex;
  justify-content: space-between;  /* 左右两端对齐 */
  align-items: center;             /* 垂直居中 */
}

.first-row .contact {
  display: flex;
  gap: 12px;

  align-items: center;
}
.name {
  font-weight: bold;
  font-size: 16px; /* 可选，稍微大一点 */
}

/* 手机号灰色 */
.phone {
  color: #909399; /* Element Plus 默认灰色 */
  font-size: 12px; /* 可选 */


}

.actions button {
  margin-left: 8px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.edit-btn {
  color: #409eff;
}

.edit-btn:hover {
  text-decoration: underline;
}

.delete-btn {
  color: red;
}

.delete-btn:hover {
  text-decoration: underline;
}

/* 第二行 */
.second-row {
  display: flex;
  align-items: center; /* 垂直居中 */
  color: #606266;
  font-size: 14px;
  margin-top: 8px;
  gap: 10px; /* 图标和文字之间间距10px */
}

.second-row .icon {
  display: flex;
  align-items: center;             /* 让图标 img 垂直居中 */
}

.second-row .icon img {
  width: 20px;   /* 图片宽度调整小一点 */
  height: 20px;  /* 高度跟宽度一致 */
  object-fit: contain;
}


</style>