<template>

  <div class="product-detail-page">

    <!-- ================== 卖家信息 ================== -->
    <div class="card1 seller-info">
      <div class="seller-avatar" @click="goToUserDetail">
        <img :src="data.userData.find(user => user.id === data.goods.userId)?.avatar" alt="seller avatar" />
      </div>
      <div class="seller-info-text" @click="goToUserDetail">
        <h3>{{ data.userData.find(user => user.id === data.goods.userId)?.name}}</h3>
        <p>{{ data.goods.place }}</p>
      </div>
    </div>

    <!-- ================== 商品详情 ================== -->
    <div class="card1 product-main">
      <div class="product-layout">

        <!-- 左侧多图列表 -->
        <div class="product-thumbs">
          <div
              v-for="(img, index) in data.goods.imgList"
              :key="index"
              :class="['thumb', activeIndex === index ? 'active' : '']"
              @click="activeIndex = index"
          >
            <img :src="img" />
          </div>
        </div>

        <!-- 中间轮播图 -->
        <div class="product-carousel">
          <button class="arrow left" @click="prevImage">&#10094;</button>
          <img :src="data.goods.imgList[activeIndex]" class="carousel-img" />
          <button class="arrow right" @click="nextImage">&#10095;</button>
        </div>

        <!-- 右侧商品信息 -->
        <div class="product-info">
          <!-- ================== 第一部分：价格/发货/浏览量 ================== -->
          <div class="info-part info-part-top">
            <!-- 第一行：层色+黄色下划线 -->
            <p class="category-line">{{ data.goods.quality }}</p>

            <!-- 第二行：价格 | 原价 | 发货设置 -->
            <p class="price-row">
              <span class="price1">¥{{ data.goods.price }}</span>
              <span class="original-price">¥{{ data.goods.rePrice }}</span>
              <span class="divider1">|</span>
              <span class="shipment1">{{ data.goods.shipment }}</span>
            </p>

            <!-- 第三行：浏览量 | 想要人数 -->
            <p class="views-row">
               {{ data.goods.num }}浏览
              <span class="divider1">|</span>
              {{ data.goods.wantNum }} 人想要
            </p>
          </div>

          <!-- ================== 第二部分：名称 + 详细信息 ================== -->
          <div class="info-part info-part-middle">
            <h2 class="product-name">{{ data.goods.name }}</h2>
            <div class="product-content" v-html="data.goods.content"></div>
          </div>

          <!-- ================== 第三部分：按钮 ================== -->
          <div class="info-part info-part-bottom">
            <!-- 左边大按钮，分两半 -->
            <!-- 左边按钮组 -->
            <div class="left-btn">
              <button class="chat-btn" @click="toggleChat">
                <img src="@/assets/imgs/message.png" alt="">聊一聊
              </button>
              <button class="buy-btn" @click="goToOrders">
                立即购买
              </button>
            </div>

            <!-- 右边收藏按钮 -->
            <button class="favorite-btn"  @click="toggleFavorite">
              <img :src="data.goods.isCollected ? love1 : love" alt="">
              {{ data.goods.isCollected ? '已收藏' : '收藏' }}
            </button>
          </div>

        </div>

      </div>
    </div>

    <!-- ================== 为你推荐 ================== -->
    <div class="card1 recommendations">
      <h3>为你推荐</h3>

      <!-- 推荐列表 -->
      <div class="goods">
        <div v-for="item in data.recommendations" :key="item.id" class="good" @click="goToRecommendation(item)">
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

import {computed, reactive, ref,watchEffect} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import love from '@/assets/imgs/love.png'
import love1 from '@/assets/imgs/love1.png'

const router = useRouter();
const route = useRoute();

const id =ref(route.query.id)



const data = reactive({
  user: JSON.parse(localStorage.getItem("code_user") || "{}"),
  goods: {
    imgList: [] // 默认空数组

  },
  userData: [],
  recommendations:[],
  isFavorite:false,
  collectId: null,
});

const goToOrders = () => {

  if (!data.user.id) {
    ElMessage.warning('请先登录');
    return;
  }

  router.push({
    path: '/front/confirm',
    query: {
      id: data.goods.id,
      from: 'goodsDetail'  // ✅ 标记来源是主页
    }
  })
}



const checkFavorite = async (goodsId) => {
  if (!data.user.id || !goodsId) {
    data.goods.isCollected = false;
    data.collectId = null;
    return;
  }

  const res = await request.get('/collect/selectAll', {
    params: {
      userId: data.user.id,
      itemId: goodsId
    }
  });

  // 确保不是旧商品
  if (goodsId !== id.value) return;

  if (res.code === '200' && res.data.length > 0) {
    const collectItem = res.data.find(item => item.itemId === Number(goodsId));
    if (collectItem) {
      data.goods.isCollected = true;
      data.collectId = collectItem.id;
    } else {
      data.goods.isCollected = false;
      data.collectId = null;
    }
  } else {
    data.goods.isCollected = false;
    data.collectId = null;
  }
};




const toggleFavorite = async () => {
  if (!data.user.id) {
    ElMessage.warning('请先登录');
    return;
  }

  try {
    if (!data.goods.isCollected) {
      // 收藏
      const res = await request.post('/collect/add', {
        userId: data.user.id,
        itemId: id.value
      });

      if (res.code === '200') {
        ElMessage.success('收藏成功');
        await checkFavorite(id.value); // 再次刷新当前商品收藏状态
        await loadGoods()
      }

    } else {
      // 取消收藏
      if (!data.collectId) return; // 安全判断
      const res = await request.delete('/collect/delete/' + data.collectId);

      if (res.code === '200') {
        ElMessage.success('已取消收藏');
        await checkFavorite(id.value); // 再次刷新当前商品收藏状态
        await loadGoods()
      }
    }
  } catch (e) {
    ElMessage.error('操作失败');
  }
};



// ================== 加载商品详情 ==================
const loadGoods = async () => {
  const currentId = id.value   // ⭐ 锁住当前 id，防竞态

  try {

    const res = await request.get("/goods/selectById/" + currentId)
    if (res.code !== "200") return

    // ❗ 如果路由已经变了，直接丢弃旧请求
    if (currentId !== id.value) return

    data.goods = res.data

    // 图片处理
    if (data.goods.imgList) {
      data.goods.imgList = data.goods.imgList.split(',')
      data.goods.imgList.unshift(data.goods.img)
    } else {
      data.goods.imgList = [data.goods.img]
    }

    loadRecommendations()
    await checkFavorite(currentId)

  } catch (err) {
    console.error(err)
  }
}



// ================== 加载用户信息 ==================
const loadUser = async () => {
  try {
    const res = await request.get("/user/selectAll");
    if (res.code === "200") {
      data.userData = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  } catch (err) {
    console.error(err);
    ElMessage.error("加载用户失败");
  }
};
loadUser();

// ================== 图片轮播逻辑 ==================
const activeIndex = ref(0);

const prevImage = () => {
  if (data.goods.imgList.length === 0) return;
  activeIndex.value =
      (activeIndex.value - 1 + data.goods.imgList.length) %
      data.goods.imgList.length;
};

const nextImage = () => {
  if (data.goods.imgList.length === 0) return;
  activeIndex.value = (activeIndex.value + 1) % data.goods.imgList.length;
};

// ================== 推荐商品 ==================
const loadRecommendations = async () => {
  if (!data.goods.categoryId) return;
  try {
    const res = await request.get('/goods/selectByCategory/' + data.goods.categoryId);
    if (res.code === '200') {
      // 过滤掉当前商品
      data.recommendations = res.data.filter(item => item.id !== data.goods.id);
    } else {
      ElMessage.error(res.msg);
    }
  } catch (err) {
    console.error(err);
    ElMessage.error('加载推荐商品失败');
  }
};

// ================== 点击推荐商品 ==================
const goToRecommendation = (item) => {
  // 1️⃣ 新标签页方式
  // 假设你有一个多标签管理逻辑，可以直接在框架里新增一个 tab
  // 这里示例用 router.push 打开新路由
  router.push({ path: '/front/goodsDetail', query: { id: item.id } });
};

// ================== 监听路由变化，刷新页面 ==================
watchEffect(async () => {
  const newId = route.query.id
  if (!newId) return

  id.value = newId
  activeIndex.value = 0

  // ⭐ 必须先清空，防止 UI 闪旧状态

  data.isFavorite = false
  data.collectId = null

  data.goods = { imgList: [] }

  await loadGoods()

  if (route.query.from === 'home') {
    try {
      await request.put('/goods/incrementView/' + newId)
      data.goods.num = (data.goods.num || 0) + 1
    } catch (err) {
      console.error('增加浏览量失败', err)
    }

    router.replace({
      path: route.path,
      query: { ...route.query, fromHome: undefined }
    })
  }
})

const goToUserDetail = () => {
  router.push({
    path: '/front/userDetail',
    query: {
      id: data.goods.userId,
    }
  })
}

const toggleChat = () => {
  router.push({
    path: '/front/chat',
    query: {
      id: data.goods.userId,
    }
  })
}





</script>

<style scoped>

.product-detail-page {
  display: flex;
  flex-direction: column;
}

/* 使用你提供的 .card1 样式 */
.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin: 20px auto 10px;
  width: 90%;
}

/* 卖家信息 */
.seller-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.seller-avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
}
.seller-info-text h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}
.seller-info-text p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

/* 商品详情三列布局 */
.product-layout {
  display: flex;
  gap: 20px;
}

/* 左侧缩略图 */
/* 左侧缩略图 */
/* 左侧缩略图容器 */
.product-thumbs {
  display: flex;
  flex-direction: column;
  gap: 10px;

  max-height: 600px;      /* 根据右侧轮播图高度设置 */
  overflow-y: auto;       /* 超出显示滚动条 */
  padding-right: 4px;     /* 避免滚动条遮挡图片 */
  box-sizing: border-box;

  /* 隐藏滚动条 */
  scrollbar-width: none;  /* Firefox */
  -ms-overflow-style: none; /* IE 10+ */
}

.product-thumbs::-webkit-scrollbar {
  display: none;          /* Chrome, Safari, Edge */
}

/* 缩略图外层容器，用于边框效果 */
.thumb {
  width: 108px;                /* 比图片略大 */
  height: 108px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid transparent; /* 默认透明边框 */
  border-radius: 10px;           /* 外层圆角 */
  cursor: pointer;
  transition: all 0.2s ease;
  box-sizing: border-box;        /* 边框不影响大小 */
}

/* 图片 */
.thumb img {
  width: 100px;                  /* 图片比边框小 */
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

/* 悬浮状态边框 */
.thumb:hover {
  border: 1px solid rgba(0, 0, 0, 0.5);
}

/* 选中状态边框加粗 */
.thumb.active {
  border: 2px solid #000;
}


/* 中间轮播图 */

.product-carousel {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;      /* 卡片背景 */
  border-radius: 16px;
  width: 600px;
  height: 600px;             /* 正方形 */
  overflow: hidden;          /* 防止图片溢出 */
}

/* 核心：图片等比例放大 */
.carousel-img {
  max-width: 100%;   /* 宽度不超过卡片宽度 */
  max-height: 100%;  /* 高度不超过卡片高度 */
  width: auto;       /* 自适应原比例 */
  height: auto;      /* 自适应原比例 */
  object-fit: contain;  /* 保持比例，放大到最大不超出容器 */
}


.arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0,0,0,0.3);
  color: #fff;
  border: none;
  font-size: 24px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
}
.arrow.left { left: 10px; }
.arrow.right { right: 10px; }

/* 右侧商品信息 */
.product-info {
  width: 500px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 10px;
}

/* ================== 第一部分 ================== */
.info-part-top {
  display: flex;
  flex-direction: column;
  gap: 6px;

}

/* 第一行：层色 + 黄色下划线 */
.category-line {
  color: #333;
  font-size: 18px;
  margin: 10px 0;
  font-weight: 500;
  display: flex;
  justify-content: flex-end; /* 右对齐 */
  text-decoration: underline;
  text-decoration-color: #ffe610;
  text-decoration-thickness: 8px; /* 下划线粗 */
  text-underline-offset: -4px;
  width: 100%;
  box-sizing: border-box;
}

/* 第二行：价格 | 原价 | 发货设置 */
.price-row {
  font-size: 16px;
  font-weight: 600;
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.price1 {
  font-size: 36px;
  color: #e4393c;
  font-weight: 600;
}
.original-price {
  text-decoration: line-through;
  color: #999;
}
.divider1 {
  margin: 0 4px;
  color: #ccc;
}
.shipment1 {
  color: #666;
  font-size: 14px;
}

/* 第三行：浏览量 | 想要人数 */
.views-row {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0;
}

/* ================== 第二部分 ================== */
.info-part-middle {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.product-name {
  font-size: 20px;
  font-weight: 600;
  margin: 10px 0;
  color: #333;
}
.product-content {
  max-height: 180px;
  overflow-y: auto;
  font-size: 18px;
  color: #666;
  line-height: 1.6;
  word-break: break-word;
}

/* 富文本内部兜底 */
.product-content img {
  max-width: 100%;
  height: auto;
}

.product-content p {
  margin: 0 0 6px;
}

.product-content h1,
.product-content h2,
.product-content h3 {
  font-size: 18px;
  margin: 6px 0;
}

/* ================== 第三部分 ================== */
.info-part-bottom {
  display: flex;
  gap: 10px;
}

/* 左边按钮组：两半，宽度稍短 */
.left-btn {
  display: flex;
  flex: 0 0 350px; /* 固定宽度，短一点 */
  border-radius: 20px;
  overflow: hidden;
}

.left-btn button {
  flex: 1;
  border: none;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  height: 50px;
  font-size: 16px;
}

/* 聊一聊按钮 */
.chat-btn {
  background-color: #ffe610; /* 黄色背景 */
  color: #333; /* 字体黑色 */
}

/* 立即购买按钮 */
.buy-btn {
  background-color: #000; /* 黑色背景 */
  color: #fff; /* 字体白色 */
}

/* 右边收藏按钮 */
.favorite-btn {
  flex: 1;                /* 让右边按钮占剩余空间 */
  min-width: 100px;       /* 防止太小 */
  height: 50px;
  background-color: #f2f2f2;
  color: #333;
  border-radius: 20px;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  font-size: 16px;
  cursor: pointer;
}

/* 左边按钮图标 */
.left-btn button img {
  width: 20px;   /* 根据需要调整大小 */
  height: 20px;
}

/* 右边收藏按钮图标 */
.favorite-btn img {
  width: 20px;   /* 根据需要调整大小 */
  height: 20px;
}


/* 推荐列表 */
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