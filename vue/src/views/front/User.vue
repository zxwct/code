<template>
  <div class="profile-page">
    <!-- Header -->
    <div class="profile-header">
      <div class="left">
        <img class="avatar1" :src="data.user.avatar" />
        <div class="info">
          <div class="username1">{{ data.user.username  }}</div>
          <div class="meta">{{ data.user.tag }}</div>
        </div>
      </div>
      <el-button round type="primary" @click="goUserProfile">编辑资料</el-button>
    </div>


    <!-- Tabs -->
    <div class="custom-tabs">
      <div
          class="tab"
          :class="{ active: activeTab === 'goods' }"
          @click="activeTab = 'goods'"
      >
        宝贝
        <span v-if="data.goodsData.length" class="count">
      {{ data.goodsData.length }}
    </span>
      </div>

      <div
          class="tab"
          :class="{ active: activeTab === 'credit' }"
          @click="activeTab = 'credit'"
      >
        历史评价
        <span v-if="data.commentData.length" class="count">
      {{ data.commentData.length }}
    </span>
      </div>
    </div>

    <!-- 宝贝 -->
    <div v-if="activeTab === 'goods'">
      <div v-if="data.goodsData.length" class="goods">
        <div
            v-for="item in data.goodsData"
            :key="item.id"
            class="good"
            @click="goToGoodsDetail(item)"
        >

              <div class="img-wrapper">
                <img :src="item.img" class="good-img"/>

                <!-- 已卖出 overlay -->
                <div v-if="item.status === '已卖出'" class="overlay">
                </div>
                <div v-if="item.status === '已卖出'" >
                  <img src="@/assets/imgs/sell.png" class="overlay-img"/>
                </div>
              </div>

              <!-- 发货 + 名称 -->
              <p class="good-title">
                <span class="shipment">{{ item.shipment }}</span>
                <span class="name">{{ item.name }}</span>
              </p>

              <!-- 价格 + 地区（只显示省份）-->
              <p class="good-info">
                <span class="price">¥{{ item.price }}</span>
                <span class="place">{{ item.place.split('/')[0] }}</span>
              </p>

              <!-- 分割线 -->
              <div class="divider"></div>

              <!-- 用户信息 -->
              <div class="user">
                <img :src="data.userData.find(user => user.id === item.userId)?.avatar" class="avatar" />
                <span class="username">{{ data.userData.find(user => user.id === item.userId)?.username }}</span>
              </div>
              <div class="good-actions">
                <el-button size="small" type="primary" @click.stop="handleEdit(item)">编辑</el-button>
                <el-button size="small" type="danger" @click.stop="del(item.id)">删除</el-button>
              </div>
        </div>
      </div>

      <div v-else class="empty">
        <img src="@/assets/imgs/empty.png" />
        <div class="text">暂无商品</div>
      </div>
    </div>

    <!-- 历史评价 -->
    <div v-if="activeTab === 'credit'">
      <div v-if="data.commentData.length" class="comments">
        <div
            v-for="item in data.commentData"
            :key="item.id"
            class="comment"
        >
          <div class="comment-header">
            <div class="user">
              <img :src="data.userData.find(user => user.id === item.toId)?.avatar" class="avatar2" />
              <span class="username2">{{ data.userData.find(user => user.id === item.toId)?.username }}</span>
            </div>
            <el-rate
                v-model="item.toRate"
                disabled
                text-color="#ff9900"
            />
          </div>

          <div class="comment-content">
            {{ item.toReview }}
          </div>

          <div class="comment-time">
            {{ item.reviewTime }}
          </div>
        </div>
      </div>

      <div v-else class="empty">
        <img src="@/assets/imgs/empty.png" />
        <div class="text">暂无评价</div>
      </div>
    </div>

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
            action="/api/files/upload"
            :show-file-list="false"
            :headers="{token: data.user.token}"
            :on-success="handleImgSuccess"
            list-type="picture"
            class="img-uploader"
        >
          <template #default>
            <div class="img-wrapper1">
              <!-- 显示头像 -->
              <img
                  v-if="data.form.img"
                  :src="data.form.img"
                  class="img2"
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

      <el-form-item prop="price" label="售价">
        <el-input v-model="data.form.price" type="number" autocomplete="off" placeholder="请输入商品售价"/>
      </el-form-item>
      <el-form-item prop="rePrice" label="原价">
        <el-input v-model="data.form.rePrice" type="number"  autocomplete="off" placeholder="请输入商品原价"/>
      </el-form-item>

      <el-form-item prop="content" label="详细内容">
        <div style="border:1px solid #ccc;width:100%;overflow-y: hidden;border-radius: 10px">
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
import {reactive, ref,onBeforeUnmount,shallowRef} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import {UploadFilled} from "@element-plus/icons-vue";
import {regionData} from "element-china-area-data";

import '@wangeditor/editor/dist/css/style.css'
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";


const router = useRouter();
const route = useRoute();

const activeTab = ref('goods')

const formRef = ref()


const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      userData: [],
      commentData: [],
      goodsData: [],
      form: {},
      formVisible: false,
      categoryData: [],
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


const loadGoods = () => {
  request.get('/goods/selectByUserId').then(res => {
    if (res.code === '200') {
      data.goodsData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadGoods()

const loadComments = () => {
  request.get('/orders/selectComment').then(res => {
    if (res.code === '200') {
      data.commentData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}
loadComments()

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

const goToGoodsDetail = (item) => {
  router.push({
    path: '/front/goodsDetail',
    query: {
      id: item.id,
      from: 'user'  // ✅ 标记来源是主页
    }
  })
}

const goUserProfile = () => {
  router.push({
    path: '/front/person',
    query: {
      tab: 'profile'
    }
  })
}


const handleEdit = (item) =>{
  data.form=JSON.parse(JSON.stringify(item))

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

          loadGoods()
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




</script>

<style scoped>
.profile-page {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}


.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}


.left {
  display: flex;
  align-items: center;
}


.avatar1 {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 16px;
}


.username1 {
  font-size: 18px;
  font-weight: bold;
}


.meta {
  font-size: 14px;
  color: #999;
  margin-top: 6px;
}


.custom-tabs {
  display: flex;
  border-bottom: 1px solid #eee;

}


.tab {
  position: relative;
  margin-right: 24px;
  padding: 12px 0;
  font-size: 16px;
  color: #666;
  cursor: pointer;
}


.tab.active {
  color: #000;
  font-weight: 600;
  text-decoration: underline;
  text-decoration-color: #ffe610 ;
  text-decoration-thickness: 8px;
  text-underline-offset: -4px;

}

.goods {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr)); /* 一行4个 */
  gap: 16px;
  margin-top: 20px;
}

.good {
  background: #fff;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;

}

.good:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

/* 只作用于商品主图 */
.good-img {
  width: 100%;
  height: 220px; /* 固定高度 */
  object-fit: cover; /* 保持比例裁剪，不拉伸 */
  border-radius: 6px;
  margin-bottom: 8px;
}

.img-wrapper {
  position: relative;
  overflow: hidden;
  height: 220px;
}

.overlay {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
  border-radius: 6px;
}

/* 卖出图标，居中显示 */
.overlay-img {
  position: absolute;
  top: 25%;
  right: 0;
  bottom: 0;
  left: 25%;
  width: 50%;
  height: 50%;
  opacity: 0.6;
  object-fit: cover;

  z-index: 2; /* 高于半透明遮罩 */

}


/* 发货 + 名称 */
.good-title {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 4px;
}

.shipment {
  text-decoration: underline;
  text-decoration-color: #ffe610 ;
  text-decoration-thickness: 8px;
  text-underline-offset: -4px;
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 2px;
  margin-right: 4px;
  flex-shrink: 0;
}

.name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  color: #333;
}

/* 价格 + 地区 */
.good-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}

.price {
  color: #e4393c;
  font-weight: 600;
}

.place {
  color: #999;
}

/* 分割线 */
.divider {
  height: 1px;
  background: #eee;
  margin: 8px 0;
}

/* 用户信息 */
.user {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin-right: 6px;
}

.username {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.good-actions {
  display: flex;
  justify-content: center;
  gap: 8px; /* 两个按钮间距 */
  margin-top: 8px;
}

.count {
  margin-left: 6px;
  font-size: 14px;
  color: #999;
}



.comment {
  padding: 16px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.avatar2 {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 6px;
}

.username2 {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.comment-content {
  font-size: 14px;
  color: #555;
  margin-bottom: 6px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}


.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #666;
}


.empty img {
  margin-bottom: 12px;
}


.text {
  font-size: 16px;
  font-weight: bold;
}

.img-uploader {
  width: 100px;
  height: 100px;
  display: inline-block;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 0; /* 消除默认行高 */
}

.img-wrapper1 {
  position: relative;
  display: flex;             /* flex 布局 */
  justify-content: center;    /* 居中 */
  align-items: center;        /* 居中 */
  width: 100px;
  height: 100px;
  overflow: hidden;           /* 防止撑开 */
}

.img2 {
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

.img-wrapper1:hover .delete-btn {
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
