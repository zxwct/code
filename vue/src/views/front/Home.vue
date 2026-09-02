<template>
  <div>

    <!-- 公告栏 -->
    <div class="notice" v-if="data.showNotice">

      <el-carousel height="40px" direction="vertical" :interval="3000" indicator-position="none">
        <el-carousel-item
            v-for="item in data.noticeData"
            :key="item.id"
            class="carousel-item"
        >
          <div class="carousel-content">
            <span style="color: #606266">{{ item.content }}</span>
            <span style="color: grey;cursor: pointer;" @click="closeNotice">x</span>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 中部白色卡片 -->
    <div class="card1">
      <div class="card-body">
        <!-- 左：分类 -->
        <div class="carousel">
          <div class="left">
            <div v-for="item in sortedCategoryData " :key="item.id" class="category">

              <div style="display: flex;gap: 10px" @click="router.push('/front/search?categoryId=' + item.id +'&keyword=')" >
                <img :src="item.icon" style="width: 20px;height: 20px;object-fit: fill;margin-left: 20px" alt="">
                <span style="color: #1f1f1f" class="category-item">{{item.name}}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 中：轮播 -->
        <div class="center">
          <div class="carousel-wrapper">
            <el-carousel height="380px">
              <el-carousel-item
                  v-for="item in data.bannerData"
                  :key="item.id"
              >
               <div style="height: 100%;width: 100%;position: relative;">
                 <div style="position: absolute;top: 0;right: 0;bottom: 0;left: 0;background: linear-gradient(to right,rgba(0,0,0,0.5) 0%, transparent 70%);display: flex;align-items: center;justify-content: center;">
                   <span class="banner-text" >{{item.name}}</span>
                 </div>
                 <img :src="item.img" class="banner-img" />
               </div>

              </el-carousel-item>
            </el-carousel>
          </div>

        </div>

        <!-- 右：推荐 -->
        <div class="right">

          <div v-for="(item,index) in sortedHotCategoryData" :key="item.id" class="hotCategory-item" :style="{background : categoryColors[index]}">

            <div style="margin-right: 10px;width: 140px">
              <div>
                <span style="color: white;font-size: 18px;font-weight: 400;">{{item.name}}</span>
              </div>
              <div>
                <span style="color: white;font-size: 12px;">{{item.info}}</span>
              </div>
              <div style="margin-top: 5px;padding-right: 10px">
                <img :src="item.img" style="width: 100px;height: 100px;object-fit: cover;" alt="">
              </div>
            </div>

            <div style="height: 164px; background: rgba(255,255,255,0.6); flex: 1; border-radius: 12px; padding: 10px; display: flex; gap: 10px;">
              <div v-for="c in item.goodsList" :key="c.id" style="flex: 1; display: flex; flex-direction: column; align-items: center; " class="hotCategory-item-goods" @click="router.push('/front/goodsDetail?id=' + c.id)">
                <div style="height: 90px; width: 100%; margin-top: 10px; overflow: hidden; border-radius: 10px;">
                  <img :src="c.img" style="width: 100%; height: 100%; object-fit: cover;" alt="">
                </div>
                <div style="text-align: center; margin-top: 10px;">
                  <span style="color: orangered; font-size: 14px; font-weight: 500;">￥{{ c.price }}</span>
                </div>
              </div>
            </div>

          </div>

        </div>

      </div>
    </div>

    <!-- 下部白色卡片 -->
    <div class="card1">
      <!-- 分类切换 -->
      <div class="tabs">
        <span class="tab-left">精选好物</span>
        <span class="tab-right" @click="router.push('/front/search')">查看全部</span>
      </div>

      <!-- 商品列表 -->
      <div class="goods">
        <div v-for="item in data.goodsData" :key="item.id" class="good" @click="goToGoodsDetail(item)">
          <!-- 图片 -->
          <img :src="item.img" class="good-img" />

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
        </div>
      </div>


    </div>
  </div>

</template>

<script setup>
import {reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import { computed } from 'vue'
import {useRouter} from "vue-router";

const router = useRouter();

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      noticeData: [],
      categoryData: [],
      bannerData: [],
      hotCategoryData: [],
      goodsData:[],
      userData:[],
      showNotice: true,

    }
)

const categoryColors = [
    'Linear-gradient(135deg, #ff6b35 0%,#ff8c42 100%)',
    'Linear-gradient(135deg, #ffd93d 0%,#ffe66d 100%)',
    'Linear-gradient(135deg, #6bcf7f 0%,#8fe99e 100%)',
    'Linear-gradient(135deg, #ff6b9d 0%,#ffa5c5 100%)'
]

const loadNotice = () =>{
  request.get('/notice/selectAll').then(res =>{
    if (res.code === '200'){
      data.noticeData =res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const closeNotice =() =>{
  data.showNotice=false
}

loadNotice()

const loadCategory = () => {
  request.get('/category/selectPage').then(res => {
    if (res.code === '200') {
      data.categoryData = res.data.list
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const sortedCategoryData = computed(() => {
  return [...data.categoryData].sort((a, b) => a.id - b.id)
})

loadCategory()

const loadBanner = () => {
  request.get('/banner/selectPage').then(res => {
    if (res.code === '200') {
      data.bannerData = res.data.list
    } else {
      ElMessage.error(res.msg)
    }
  })
}

loadBanner()

const loadHotCategory = () => {
  request.get('/category/selectHot').then(res => {
    if (res.code === '200') {
      data.hotCategoryData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const sortedHotCategoryData = computed(() => {
  return [...data.hotCategoryData].sort((a, b) => a.id - b.id)
})

loadHotCategory()


const loadGoods = () => {
  request.get('/goods/selectHot').then(res => {
    if (res.code === '200') {
      data.goodsData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadGoods()

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

const goToGoodsDetail = (item) => {
  router.push({
    path: '/front/goodsDetail',
    query: {
      id: item.id,
      from: 'home'  // ✅ 标记来源是主页
    }
  })
}


</script>

<style scoped>

.carousel-item {
  height: 100%;
}

.carousel-content {
  height: 100%;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: space-between;
}


.notice {
  width: 1600px;
  margin-top: 20px;
  padding-left: 100px;
  padding-right: 10px;

}

.category-item:hover{
  background-color: #f6cb7e;
  cursor: pointer;
}


.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin: 20px auto 20px;
  width: 90%;

}

.card-body {
  display: flex;
}

.left {
  width: 180px;
}

.category {
  padding: 10px 0;
}

.center {
  flex: 1;
  margin: 0 20px;
}

.carousel {
  height: 380px;
  border-radius: 16px;
  background-color: #f6f6f6;
  display: flex;
  align-items: center;
  justify-content: center;

}

/* 外层控制圆角 */
.carousel-wrapper {
  height: 380px;
  border-radius: 16px;
  overflow: hidden;        /* 圆角生效 */
}

/* 取消 el-carousel 内部裁切 */
.el-carousel__container {
  overflow: visible !important;
}

/* 轮播项撑满高度 */
.el-carousel__item {
  height: 100%;
}

/* 图片样式 */
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;       /* 不变形 */
  display: block;
}

.banner-text {
  font-size: 20px;
  color: #efeded;
  font-weight: 700;
  text-shadow:
      0 2px 4px rgba(0,0,0,0.6),
      0 0 8px rgba(0,0,0,0.4);
}


.right {
  width: 850px;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;

}

.hotCategory-item {
  border-radius: 12px;
  height: 184px;
  width: 100%;
  display: flex;
  padding: 10px;
  transition: all 0.5s ease;

}
.hotCategory-item:hover{
  transform: translateY(-5px);

}

.hotCategory-item-goods{
  transition: all 0.5s ease;
}
.hotCategory-item-goods:hover{
  scale: 1.05;
}

.tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tab-left {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.tab-right {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.tab-right:hover {
  color: #409eff; /* Element Plus 主色 */
}


.goods {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr)); /* 防止最后一行拉伸 */
  gap: 16px;
  justify-items: start; /* 左对齐，不让最后一行自动拉伸 */
}

.good {
  background: #fff;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, box-shadow 0.2s;
  max-width: 230px; /* 限制每个卡片最大宽度 */
}

.good:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

/* 只作用于商品主图 */
.good > img {
  width: 100%;
  height: 160px;         /* 固定高度 */
  object-fit: cover;     /* 保持比例裁切 */
  border-radius: 6px;
  margin-bottom: 8px;
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


</style>