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
      form: {
        name: '',           // 商品名称
        categoryId: null,   // 类别ID
        quality: '全新',    // 商品成色
        shipment: '包邮',   // 发货设置
        price: '',          // 售价
        rePrice: '',        // 原价
        img: '',            // 主图
        imgList: [],        // 多图列表
        content: '',        // 商品详情
        place: [],          // 所在城市
        num: 0,             // 浏览量
        status: 1,          // 状态
        userId: null   // 发布人
      },
      categoryData: [],
      userData: [],
      contentVisible: false,
      rules: {
        name: [
          {required: true, message: '请填写商品名称', trigger: 'blur'}
        ],
        categoryId: [
          {required: true, message: '请选择类别', trigger: 'change'}
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
          {required: true, message: '请填写所在地点', trigger: 'blur'}
        ],
        userId: [
          {required: true, message: '请填写发布人', trigger: 'change'}
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


load()


const add = () =>{
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.post('/goods/add',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('发布成功')

          // 1. 重置 form 到初始状态
          data.form = {
            name: '',
            categoryId: null,
            quality: '全新',
            shipment: '包邮',
            price: '',
            rePrice: '',
            img: '',
            imgList: [],
            content: '',
            place: [],
            num: 0,
            status: 1,
            userId: null
          }

          // 2. 清空多图上传列表
          data.imgList = []

          // 3. 重置表单校验状态
          formRef.value.resetFields()


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

  // 所在城市处理
  if(data.form.place && Array.isArray(data.form.place)){
    data.form.place = data.form.place.join('/');
  } else {
    data.form.place = '';
  }

  data.form.id ? update() : add()
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

<template>
  <div class="card1">

    <!-- 标题 -->
    <div class="card-header">
      <h2 class="card-title">发布闲置</h2>
    </div>

    <!-- 分组标题 -->
    <div class="section-title">
      基础信息
    </div>

    <!-- 分割线 -->
    <el-divider />

    <!-- 表单 -->
    <el-form
        ref="formRef"
        :model="data.form"
        :rules="data.rules"
        label-position="top"
    >

      <!-- 商品主图 -->
      <el-form-item label="商品主图" prop="img" >
        <el-upload
            action="/api/files/upload"
            :headers="{ token: data.user.token }"
            :show-file-list="false"
            list-type="picture"
            class="img-uploader"
            :on-success="handleImgSuccess"
        >
          <div class="img-wrapper">
            <img v-if="data.form.img" :src="data.form.img" class="img" />
            <el-icon
                v-if="data.form.img"
                class="delete-btn"
                @click.stop="handleDeleteImg"
            >
              <Close />
            </el-icon>
            <el-icon v-else class="img-uploader-icon">
              <Plus />
            </el-icon>
          </div>
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
            action="/api/files/upload"
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


      <!-- 商品名称 -->
      <el-form-item label="商品名称" prop="name" >
        <el-input v-model="data.form.name" placeholder="请输入商品名称" />
      </el-form-item>

      <!-- 商品类别 -->
      <el-form-item label="商品类别" prop="categoryId" >
        <el-select
            v-model="data.form.categoryId"
            placeholder="请选择商品类别"
            style="width:100%"
            clearable
        >
          <el-option
              v-for="item in data.categoryData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>

      <!-- 商品成色 -->
      <el-form-item label="商品成色" prop="quality" >
        <el-radio-group v-model="data.form.quality">
          <el-radio value="全新" border>全新</el-radio>
          <el-radio value="九成新" border>九成新</el-radio>
          <el-radio value="八成新" border>八成新</el-radio>
          <el-radio value="七成新及以下" border>七成新及以下</el-radio>
        </el-radio-group>
      </el-form-item>



      <!-- 详细内容 -->
      <el-form-item label="商品详情描述" prop="content" >
        <div style="border:1px solid #ccc;width:100%;overflow-y: hidden;border-radius: 10px">
          <Toolbar
              style="border-bottom:1px solid #ccc"
              :editor="editorRef"
              :mode="mode"
          />
          <Editor
              style="min-height:100px"
              v-model="data.form.content"
              :mode="mode"
              :defaultConfig="editorConfig"
              @onCreated="handleCreated"
          />
        </div>
      </el-form-item>


      <!-- 所在城市 -->
      <el-form-item label="所在城市" prop="place" >
        <el-cascader
            v-model="data.form.place"
            :options="regionData"
            placeholder="请选择城市"
            :props="{ value: 'label' }"
            clearable
            style="width:100%"
        />
      </el-form-item>

      <!-- 发货设置 -->
      <el-form-item label="发货设置" prop="shipment" >
        <el-radio-group v-model="data.form.shipment">
          <el-radio value="包邮" border>包邮</el-radio>
          <el-radio value="邮费到付" border>邮费到付</el-radio>
          <el-radio value="无须邮寄" border>无须邮寄</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 价格信息标题 -->
      <div class="section-title" style="margin-top: 20px;">价格</div>
      <el-divider />

      <!-- 售价和原价在同一行 -->
      <el-row :gutter="20">
        <!-- 售价 -->
        <el-col :span="12">
          <el-form-item label="售价" prop="price">
            <el-input v-model="data.form.price" type="number" />
          </el-form-item>
        </el-col>

        <!-- 原价 -->
        <el-col :span="12">
          <el-form-item label="原价" prop="rePrice">
            <el-input v-model="data.form.rePrice" type="number" />
          </el-form-item>
        </el-col>
      </el-row>


      <!-- 操作按钮 -->
      <el-form-item>
        <div class="button-wrapper">
          <el-button class="btn-publish" type="primary" @click="save">
            发布
          </el-button>
        </div>
      </el-form-item>


    </el-form>
  </div>
</template>



<style scoped>

.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin: 20px auto;
  width: 90%;
}

.card-header {
  margin-bottom: 20px;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin-top: 10px;
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



/* 按钮容器居中 */
.button-wrapper {
  display: flex;
  justify-content: center;  /* 水平居中 */
  width: 100%;
  margin-top: 30px;         /* 与上方表单间距 */
}

/* 发布按钮样式 */
.btn-publish {
  background: #FFBA00;      /* 闲鱼黄色 */
  border-color: #FFBA00;
  color: #fff;
  font-weight: 600;
  font-size: 18px;          /* 文字大一点 */
  padding: 24px 200px;       /* 按钮更大 */
  border-radius: 30px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
  transition: all 0.3s;
}

.btn-publish:hover {
  background: #e0a800;     /* 悬停稍微深一点 */
  border-color: #e0a800;
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.2);
}



</style>
