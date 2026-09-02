<template>
  <div class="card" style="margin-bottom: 5px">
    <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.name"
              placeholder="请输入联系人查询" :prefix-icon="Search"></el-input>
    <el-button type="primary" @click="load">查 询</el-button>
    <el-button type="info" @click="reset">重 置</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN'">
    <el-button type="primary" @click="handleAdd">新增</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px">
    <el-table :data="data.tableData" style="width: 100%"
              :header-cell-style="{color: '#333',backgroundColor:'#8a9ef1'}">
      <el-table-column prop="name" label="联系人"/>
      <el-table-column prop="address" label="所在地区"/>

      <el-table-column prop="info" label="详细地址"/>

      <el-table-column prop="phone" label="联系电话"/>

      <el-table-column prop="userName" label="用户名">
        <template #default="scope">
          <span v-if="scope.row.userId">{{data.userData.find(item => item.id === scope.row.userId).username}}</span>
        </template>
      </el-table-column>


      <el-table-column label="操作" width="200px">
        <template #default="scope">
          <el-button type="primary" icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
          <el-button type="danger" icon="Delete" circle @click="del(scope.row.id)"></el-button>
        </template>
      </el-table-column>

    </el-table>
  </div>

  <div class="card">
    <el-pagination
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        layout="total,sizes, prev, pager, next,jumper"
        :page-sizes="[2, 4, 6]"
        :total="data.total"
        @current-change="load"
        @size-change="load"
    />
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


      <el-form-item prop="userId" label="用户名">
        <el-select
            v-model="data.form.userId"
            placeholder="请选择用户"
            style="width: 100%">
          <el-option
              v-for="item in data.userData"
              :key="item.id"
              :label="item.username"
              :value="item.id"
          />
        </el-select>
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


<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {regionData} from "element-china-area-data";

const formRef = ref()

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      name: null,
      pageNum: 1,
      pageSize: 6,
      total: 0,
      tableData: [],
      userData: [],
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
  request.get('/address/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data.list
      data.total = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const reset = () =>{
  data.name=null
  load()
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

const loadUser = () =>{
  request.get('/user/selectAll').then(res =>{
    if (res.code === '200'){
      data.userData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}
loadUser()


</script>

<style scoped>

/* 固定行高 */
.el-table .img-column .img-cell {
  width: 40px;
  height: 40px;        /* 固定高度 */
  display: flex;
  justify-content: center;
  align-items: center;  /* 图片居中 */
}

/* el-image 占满父容器 */
.el-table .img-column .img-cell >>> .el-image {
  width: 40px;
  height: 40px;
  border-radius: 5px;
  object-fit: cover;
}


/* 固定上传组件容器 */
.img-uploader {
  width: 100px;
  height: 100px;
  display: inline-block;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 0; /* 消除默认行高 */
}

.img-wrapper {
  position: relative;
  display: flex;             /* flex 布局 */
  justify-content: center;    /* 居中 */
  align-items: center;        /* 居中 */
  width: 100px;
  height: 100px;
  overflow: hidden;           /* 防止撑开 */
}

.img {
  width: 100%;
  height: 100%;
  border-radius: 5px;
  object-fit: cover;          /* 裁剪填充 */

}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  cursor: pointer;
  color: white;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 5px;
  padding: 2px;
  opacity: 0;               /* 默认隐藏 */
  transition: opacity 0.2s; /* 平滑显示 */
  z-index: 10;  /* 确保在最上层 */
}

.img-wrapper:hover .delete-btn {
  opacity: 1;  /* 鼠标移入头像区域时显示 */
}

.img-uploader-icon {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px; /* 控制 Plus 图标大小 */
  color: #409EFF;  /* Element Plus 默认主题色 */
  border: 1px dashed #d9d9d9;
  border-radius: 5px;
  cursor: pointer;
}
</style>