<template>
  <div class="card" style="margin-bottom: 5px">
    <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.no"
              placeholder="请输入订单号查询" :prefix-icon="Search"></el-input>
    <el-button type="primary" @click="load">查 询</el-button>
    <el-button type="info" @click="reset">重 置</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN'">
    <el-button type="primary" @click="handleAdd">新增</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px">
    <el-table :data="data.tableData" style="width: 100%;font-size: 12px"
              :header-cell-style="{color: '#333',backgroundColor:'#8a9ef1'}">

      <el-table-column prop="no" label="订单号"/>

      <el-table-column prop="goodsName" label="商品名称"/>


      <el-table-column label="商品图片"  width="120px" class-name="img-column" label-class-name="img-column-header">
        <template #default="scope">
          <div class="img-cell">
            <el-image v-if="scope.row.goodsImg" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]" :preview-teleported="true"
                      style="width: 200px;height: 100px; border-radius: 5px; display: block"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="fromId" label="卖家">
        <template #default="scope">
          <span v-if="scope.row.fromId">{{data.userData.find(item => item.id === scope.row.fromId).name}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="toId" label="买家">
        <template #default="scope">
          <span v-if="scope.row.toId">{{data.userData.find(item => item.id === scope.row.toId).name}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="price" label="价格" />

      <el-table-column prop="time" label="支付时间"/>

      <el-table-column prop="status" label="状态">
        <template v-slot="scope">
          <el-tag  type="success">{{scope.row.status}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="toRate" label="买家评分" width="150px" >
        <template v-slot="scope">
          <el-rate v-model="scope.row.toRate" disabled/>
        </template>
      </el-table-column>

      <el-table-column prop="toReview" label="买家评价"/>

      <el-table-column prop="address" label="所在地区"/>

      <el-table-column prop="info" label="详细地址"/>


      <el-table-column prop="name" label="收货人姓名">
        <template #default="scope">
          <span v-if="scope.row.userId">{{data.userData.find(item => item.id === scope.row.userId).username}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="phone" label="联系电话"/>
      


      <el-table-column label="操作" width="120px">
        <template #default="scope" v-if="data.user.role === 'ADMIN'">
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

  <el-dialog v-model="data.formVisible" title="订单管理" width="60%" destroy-on-close>

    <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">

      <el-form-item prop="no" label="订单号">
        <el-input v-model="data.form.no" autocomplete="off" placeholder="请输入订单号"/>
      </el-form-item>

      <el-form-item prop="name" label="商品名称">
        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入商品名称"/>
      </el-form-item>
      

      <el-form-item prop="goodsImg" label="商品图片">
        <el-upload
            action="/api/files/upload"
            :show-file-list="false"
            :headers="{token: data.user.token}"
            :on-success="handleImgSuccess"
            list-type="picture"
            class="img-uploader"
        >
          <template #default>
            <div class="img-wrapper">
              <!-- 显示头像 -->
              <img
                  v-if="data.form.goodsImg"
                  :src="data.form.goodsImg"
                  class="img"
              />

              <!-- 删除按钮（默认隐藏，hover 显示） -->
              <el-icon
                  v-if="data.form.goodsImg"
                  @click.stop="handleDeleteImg"
                  class="delete-btn"
              >
                <Close />
              </el-icon>
              <!-- 如果没有头像，就显示上传按钮 -->
              <el-icon
                  v-else
                  class="img-uploader-icon"
              >
                <Plus />
              </el-icon>
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item prop="fromId" label="卖家">
        <el-select
            v-model="data.form.fromId"
            placeholder="请选择卖家"
            style="width: 100%">
          <el-option
              v-for="item in data.userData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item prop="toId" label="买家">
        <el-select
            v-model="data.form.toId"
            placeholder="请选择买家"
            style="width: 100%">
          <el-option
              v-for="item in data.userData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>


      <el-form-item prop="price" label="价格">
        <el-input v-model="data.form.price" type="number" autocomplete="off" placeholder="请输入商品价格"/>
      </el-form-item>

      <el-form-item prop="time" label="支付时间">
        <el-date-picker v-model="data.form.time" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请输入支付时间"/>
      </el-form-item>

      <el-form-item prop="status" label="状态">
        <el-radio-group v-model="data.form.status">
          <el-radio value="待付款" size="large" border>待支付</el-radio>
          <el-radio value="待发货" size="large" border>待发货</el-radio>
          <el-radio value="待收货" size="large" border>待收货</el-radio>
          <el-radio value="待评价" size="large" border>待评价</el-radio>
          <el-radio value="已完成" size="large" border>已完成</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item prop="toRate" label="买家评分">
        <el-rate v-model="data.form.toRate" />
      </el-form-item>

      <el-form-item prop="toReview" label="买家评价">
        <el-input v-model="data.form.toReview" type="textarea" autocomplete="off" placeholder="请输入买家评价"/>
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

      <el-form-item prop="name" label="收货人">
        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入收货人姓名"/>
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

  <el-dialog title="详细内容" v-model="data.contentVisible" width="60%" destroy-on-close>
    <div v-html="data.content" style="padding: 0 20px"></div>
  </el-dialog>

</template>


<script setup>
import {Delete, Search,UploadFilled} from "@element-plus/icons-vue";
import {reactive, ref,onBeforeUnmount,shallowRef} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {regionData} from 'element-china-area-data'

import '@wangeditor/editor/dist/css/style.css'
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";


const formRef = ref()

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      no: null,
      pageNum: 1,
      pageSize: 6,
      total: 0,
      tableData: [],
      form: {},
      formVisible: false,
      categoryData: [],
      userData: [],
      contentVisible: false,
      rules: {
        no: [
          {required: true, message: '请填写订单号', trigger: 'blur'}
        ],
        goodsName: [
          {required: true, message: '请填写商品名称', trigger: 'blur'}
        ],
        goodsImg: [
          {required: true, message: '请上传商品图片', trigger: 'blur'}
        ],
        fromId: [
          {required: true, message: '请填写卖家', trigger: 'blur'}
        ],
        toId: [
          {required: true, message: '请填写买家', trigger: 'blur'}
        ],
        price: [
          {required: true, message: '请填写商品价格', trigger: 'blur'}
        ],
        time: [
          {required: true, message: '请选择支付时间', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '请选择状态', trigger: 'blur'}
        ],
        toRate: [
          {required: true, message: '请选择评分', trigger: 'blur'}
        ],
        toReview: [
          {required: true, message: '请填写评价', trigger: 'blur'}
        ],
        address: [
          {required: true, message: '请选择所在地区', trigger: 'blur'}
        ],
        info: [
          {required: true, message: '请填写详细地址', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请填写收货人', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请填写手机号', trigger: 'blur'},
          {
            pattern: /^1\d{10}$/,
            message: "请输入正确的11位手机号",
            trigger: "blur"
          }
        ],

      },


    }
)

/* wangEditor5 初始化开始 */
const editorRef = shallowRef(); //编辑器实例，必须用 shallowRef
const mode = 'default'
const editorConfig = {MENU_CONF: {} }
//图片上传配置
editorConfig.MENU_CONF['uploadImage'] ={
  headers:{
    token: data.user.token,
  },
  server: '/api/files/wang/upload', //服务端图片上传接口
  fieldName: 'file', //服务端图片上传接口参数
}
//组件销毁时，也及时销毁编辑器，否则可能会造成内存泄漏
onBeforeUnmount(() => {
  const editor= editorRef.value
  if (editor == null) return
  editor.destroy()
})
//记录 editor 实例，重要！
const handleCreated = (editor) => {
  editorRef.value =editor
}
/* wangEditor5 初始化结束 */

const viewContent = (content) =>{
  data.content=content
  data.contentVisible=true
}


const load = () => {
  request.get('/orders/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      no: data.no,
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
  data.no=null
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
      request.post('/orders/add',data.form).then(res =>{
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
      request.put('/orders/update',data.form).then(res =>{
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
    request.delete('/orders/delete/' + id).then(res =>{
      if(res.code === '200'){
        ElMessage.success('删除成功')
        load()
      }else{
        ElMessage.error(res.msg)
      }
    })
  }).catch(err =>{})
}

const handleImgSuccess = (res) =>{
  data.form.goodsImg=res.data

}

const handleDeleteImg = () => {
  data.form.goodsImg = ''       // 表单里清空

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
  width: 100px;
  height: 100px;        /* 固定高度 */
  display: flex;
  justify-content: center;
  align-items: center;  /* 图片居中 */
}

/* el-image 占满父容器 */
.el-table .img-column .img-cell >>> .el-image {
  width: 100px;
  height: 100px;
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

/* 上传整体容器 */
.upload-container {
  width: 100%;
}

.img-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 单个图片块 */
.img-item {
  width: 100px;
  height: 100px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;
  border: 1px solid #ebeef5;
}

/* 图片 */
.img1 {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 删除按钮 */
.delete-btn1 {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  font-size: 14px;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;

  opacity: 0;
  transition: opacity 0.2s;
}

/* hover 显示删除 */
.img-item:hover .delete-btn1 {
  opacity: 1;
}



</style>
