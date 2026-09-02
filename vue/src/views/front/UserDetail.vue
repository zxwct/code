<template>

  <div class="search-page">
    <!-- card -->
    <div class="card1">
      <div class="profile-page">
        <!-- Header -->
        <div class="profile-header">
          <div class="left">
            <img class="avatar1" :src="data.userData.avatar" />
            <div class="info">
              <div class="username1">{{ data.userData.username  }}</div>
              <div class="meta">{{ data.userData.tag }}</div>
            </div>
          </div>
          <el-button round type="primary" @click="toggleChat">去私信</el-button>
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
                <span class="price-group">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="original-price" >¥{{ item.rePrice }}</span>
                </span>
                <span class="place">{{ item.place.split('/')[0] }}</span>
              </p>

              <!-- 分割线 -->
              <div class="divider"></div>

              <!-- 用户信息 -->
              <div class="user">
                <img :src="data.userData.avatar" class="avatar" />
                <span class="username">{{ data.userData.name }}</span>
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
                  <img :src="data.userAllData.find(user => user.id === item.toId)?.avatar" class="avatar2" />
                  <span class="username2">{{ data.userAllData.find(user => user.id === item.toId)?.username }}</span>
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

    </div>

  </div>

</template>

<script setup>
import {computed, reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import collect from "@/assets/imgs/collect1.png";
import message from "@/assets/imgs/message1.png";


const router = useRouter();
const route = useRoute();

const activeTab = ref('goods')




const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      userData: {},
      commentData: [],
      goodsData: [],
      userAllData: []
    }
)

const loadGoods = (id) => {
  request.get(`/goods/selectByUserId/${id}`).then(res => {
    if (res.code === '200') {
      data.goodsData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}


const loadComments = (id) => {
  request.get(`/orders/selectById/${id}`).then(res => {
    if (res.code === '200') {
      data.commentData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}


const loadUser = (id) =>{
  request.get(`/user/selectById/${id}`).then(res =>{
    if (res.code === '200'){
      data.userData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}

const loadUserAll = () =>{
  request.get('/user/selectAll').then(res =>{
    if (res.code === '200'){
      data.userAllData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}

loadUserAll()

const goToGoodsDetail = (item) => {
  router.push({
    path: '/front/goodsDetail',
    query: {
      id: item.id,
      from: 'userDetail'  // ✅ 标记来源是主页
    }
  })
}

const toggleChat = () => {
  router.push({
    path: '/front/chat',
    query: {
      id: data.userData.id,
    }
  })
}

watch(
    () => route.query.id,
    (id) => {
      if (!id) return
      const userId = Number(id)

      loadUser(userId)
      loadGoods(userId)
      loadComments(userId)
    },
    { immediate: true }
)


</script>

<style scoped>
.search-page {
  min-height: calc(100vh - 60px - 60px); /* 60px 头部 + 60px 页脚高度，可按实际调整 */
  background: #f5f5f5;
  padding-bottom: 20px; /* 底部呼吸感 */
  display: flex;
  flex-direction: column;
}

.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin: 20px auto 20px;
  width: 90%;

}


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
  margin-top: 8px;
  align-items: center; /* 垂直居中 */
}

/* 左侧价格组 */
.price-group {
  display: flex;
  align-items: center;
  gap: 8px; /* price 与 original-price 间距 */
}

.price {
  color: #e4393c;
  font-weight: 600;
  font-size: 16px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
}

/* 右侧地区 */
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


</style>
