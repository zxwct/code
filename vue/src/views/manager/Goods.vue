<template>
  <div class="card" style="margin-bottom: 5px">
    <el-input clearable @clear="load" style="width: 300px;margin-right: 5px" v-model="data.name"
              placeholder="请输入商品名称查询" :prefix-icon="Search"></el-input>
    <el-button type="primary" @click="load">查 询</el-button>
    <el-button type="info" @click="reset">重 置</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN'">
    <el-button type="primary" @click="handleAdd">新增</el-button>
  </div>

  <div class="card" style="margin-bottom: 5px">
    <el-table :data="data.tableData" style="width: 100%;font-size: 12px"
              :header-cell-style="{color: '#333',backgroundColor:'#8a9ef1'}">

      <el-table-column prop="name" label="商品名称"/>

      <el-table-column prop="categoryName" label="商品类别">
        <template #default="scope">
          <span v-if="scope.row.categoryId">{{data.categoryData.find(item => item.id === scope.row.categoryId).name}}</span>
        </template>
      </el-table-column>


      <el-table-column label="商品图片"  width="120px" class-name="img-column" label-class-name="img-column-header">
        <template #default="scope">
          <div class="img-cell">
            <el-image v-if="scope.row.img" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"
                      style="width: 200px;height: 100px; border-radius: 5px; display: block"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="多张图片" width="120px" class-name="img-column" label-class-name="img-column-header">
        <template #default="scope">
          <div class="img-cell">
            <el-image v-if="scope.row.img" :src="getImgList(scope.row.imgList)[0]" :preview-src-list="getImgList(scope.row.imgList)" :preview-teleported="true"
                      style="width: 200px;height: 100px; border-radius: 5px; display: block"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="price" label="售价" />

      <el-table-column prop="rePrice" label="原价" />

      <el-table-column prop="content" label="详细内容">
        <template v-slot="scope">
          <el-button type="primary" @click="viewContent(scope.row.content)">查看</el-button>
        </template>
      </el-table-column>

      <el-table-column prop="place" label="地点" />

      <el-table-column prop="shipment" label="发货设置" width="110px">
        <template v-slot="scope">
          <el-tag  type="primary">{{scope.row.shipment}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="userName" label="发布用户">
        <template #default="scope">
          <span v-if="scope.row.userId">{{data.userData.find(item => item.id === scope.row.userId).username}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="num" label="浏览量" />

      <el-table-column prop="status" label="状态">
        <template v-slot="scope">
          <el-tag  type="success">{{scope.row.status}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="quality" label="商品成色">
        <template v-slot="scope">
          <el-tag  type="warning">{{scope.row.quality}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="date" label="发布时间"/>


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

  <el-dialog v-model="data.formVisible" title="商品管理" width="60%" destroy-on-close>

    <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">

      <el-form-item prop="name" label="商品名称">
        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入商品名称"/>
      </el-form-item>

      <el-form-item prop="categoryId" label="商品类别">
        <el-select
            v-model="data.form.categoryId"
            placeholder="请选择商品类别"
            style="width: 100%">
          <el-option
              v-for="item in data.categoryData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>


      <el-form-item prop="img" label="商品图片">
        <el-upload
            action="http://localhost:9999/files/upload"
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
                  v-if="data.form.img"
                  :src="data.form.img"
                  class="img"
              />

              <!-- 删除按钮（默认隐藏，hover 显示） -->
              <el-icon
                  v-if="data.form.img"
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

      <el-form-item prop="imgList" label="多张图片">
        <div class="upload-container">
          <div class="img-list" v-if="data.imgList.length">
            <div
                v-for="(item, index) in data.imgList"
                :key="index"
                class="img-item"
            >
              <img :src="item" class="img1" />

              <el-icon
                  class="delete-btn1"
                  @click="handleDeleteImgList(index)"
              >
                <Close />
              </el-icon>
            </div>
          </div>
        </div>

        <el-upload
            action="http://localhost:9999/files/upload"
            :show-file-list="false"
            :headers="{token: data.user.token}"
            :on-success="handleImgListSuccess"
            list-type="picture"
            class="img-uploader1"
            multiple
        >
          <el-button type="primary" :icon="UploadFilled" style="margin-top: 10px">上传图片</el-button>

        </el-upload>
      </el-form-item>

      <el-form-item prop="price" label="售价">
        <el-input v-model="data.form.price" type="number" autocomplete="off" placeholder="请输入商品售价"/>
      </el-form-item>
      <el-form-item prop="rePrice" label="原价">
        <el-input v-model="data.form.rePrice" type="number"  autocomplete="off" placeholder="请输入商品原价"/>
      </el-form-item>

      <el-form-item prop="content" label="详细内容">
        <div style="border: 1px solid #ccc;width:100%;">
          <Toolbar
              style="border-bottom: 1px solid #ccc;"
              :editor="editorRef"
              :mode="mode"
          />
          <Editor
              style="height: 500px;overflow-y: hidden;"
              v-model="data.form.content"
              :mode="mode"
              :defaultConfig="editorConfig"
              @onCreated="handleCreated"
          />
        </div>
      </el-form-item>

      <el-form-item prop="place" label="所在城市">
        <el-cascader
          v-model="data.form.place"
          :options="regionData"
          :props="{value: 'label'}"
          placeholder="请选择省市区"
          clearable
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item prop="shipment" label="发货设置">
        <el-radio-group v-model="data.form.shipment">
          <el-radio value="包邮">包邮</el-radio>
          <el-radio value="邮费到付">邮费到付</el-radio>
          <el-radio value="无须邮寄">无须邮寄</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item prop="userId" label="发布用户">
        <el-select
            v-model="data.form.userId"
            placeholder="请选择发布用户"
            style="width: 100%">
          <el-option
              v-for="item in data.userData"
              :key="item.id"
              :label="item.username"
              :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item prop="num" label="浏览量">
        <el-input v-model="data.form.num" autocomplete="off" />
      </el-form-item>

      <el-form-item prop="status" label="状态">
        <el-radio-group v-model="data.form.status">
          <el-radio value="已上架" size="large" border>已上架</el-radio>
          <el-radio value="已卖出" size="large" border>已卖出</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item prop="quality" label="商品成色">
        <el-radio-group v-model="data.form.quality">
          <el-radio value="全新" >全新</el-radio>
          <el-radio value="九成新" >九成新</el-radio>
          <el-radio value="八成新" >八成新</el-radio>
          <el-radio value="七成新及以下" >七成新及以下</el-radio>
        </el-radio-group>
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
      name: null,
      pageNum: 1,
      pageSize: 6,
      total: 0,
      tableData: [],
      imgList:[],
      form: {},
      formVisible: false,
      categoryData: [],
      userData: [],
      contentVisible: false,
      rules: {
        name: [
          {required: true, message: '请填写商品名称', trigger: 'blur'}
        ],
        categoryId: [
          {required: true, message: '请选择类别', trigger: 'blur'}
        ],
        img: [
          {required: true, message: '请上传商品图片', trigger: 'blur'}
        ],
        imgList: [
          {required: true, message: '请上传多张图片', trigger: 'blur'}
        ],
        price: [
          {required: true, message: '请填写商品售价', trigger: 'blur'}
        ],
        rePrice: [
          {required: true, message: '请填写商品原价', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '请填写详细内容', trigger: 'blur'}
        ],
        place: [
          {required: true, message: '请选择所在地点', trigger: 'blur'}
        ],
        userId: [
          {required: true, message: '请选择发布人', trigger: 'blur'}
        ],
        shipment: [
          {required: true, message: '请选择发货设置', trigger: 'blur'}
        ],
        num: [
          {required: true, message: '请填写浏览量', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '请选择状态', trigger: 'blur'}
        ],
        quality: [
          {required: true, message: '请选择商品成色', trigger: 'blur'}
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
  server: 'http://localhost:9999/files/wang/upload', //服务端图片上传接口
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
  request.get('/goods/selectPage', {
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
  data.imgList= []
  data.formVisible = true
}

const handleEdit = (row) =>{
  data.form=JSON.parse(JSON.stringify(row))

  if(data.form.imgList){
    data.imgList= data.form.imgList.split(',');
  }
  else {
    data.imgList=[];
  }

  if(data.form.place){
    data.form.place= data.form.place.split('/');
  }
  else {
    data.form.place=[];
  }


  data.formVisible=true
}

const add = () =>{
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.post('/goods/add',data.form).then(res =>{
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
      request.put('/goods/update',data.form).then(res =>{
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

  if(data.imgList.length > 0){
    data.form.imgList=data.imgList.join(",");
  }
  else{
    data.form.imgList = '';
  }

  if(data.form.place){
    data.form.place =data.form.place.join('/');
  }
  else {
    data.form.place = '';
  }

  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？','删除确认',{type :'warning'}).then(res =>{
    request.delete('/goods/delete/' + id).then(res =>{
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
  data.form.img=res.data
}

const handleDeleteImg = () => {
  data.form.img = ''       // 表单里清空

}

const handleImgListSuccess = (res) =>{
  data.imgList.push(res.data)
}

const handleDeleteImgList = (index) => {
  data.imgList.splice(index,1);
}

const getImgList = (imgString) => {
  if(!imgString) return [];
  return imgString.split(',');
}

const loadCategory = () =>{
  request.get('/category/selectAll').then(res =>{
    if (res.code === '200'){
      data.categoryData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}
loadCategory()

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
