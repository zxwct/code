<template>
  <div class="collect-page">
    <!-- 顶部选项 -->
    <div class="filter-bar">
      <div
          v-for="option in filterOptions"
          :key="option.value"
          :class="['filter-item', { active: selectedFilter === option.value }]"
          @click="selectFilter(option.value)"
      >
        {{ option.label }}
      </div>
    </div>

    <!-- 商品列表 -->
    <div v-if="filteredGoods.length" class="goods">
      <div
          v-for="item in filteredGoods"
          :key="item.id"
          class="good"
          @click="goToGoodsDetail(item)"
      >

          <!-- 悬浮操作 -->
          <div class="img-wrapper">
            <img :src="item.img" class="good-img"/>

            <!-- 已卖出 overlay -->
            <div v-if="item.status === '已卖出'" class="overlay">
            </div>
            <div v-if="item.status === '已卖出'" >
              <img src="@/assets/imgs/sell.png" class="overlay-img"/>
            </div>

            <!-- 悬浮操作 -->
            <div class="hover-actions">
              <!-- 左边按钮：取消收藏，所有状态都显示 -->
              <div class="action left" @click.stop="cancelCollect(item)">
                <img :src="collect" alt="" style="width: 20px; height: 20px;" />
                取消收藏
              </div>

              <!-- 右边按钮：只有非已卖出状态才显示“我想要” -->
              <div
                  class="action right"
                  v-if="item.status !== '已卖出'"
                  @click.stop="wantIt(item)"
              >
                <img :src="message" alt="" style="width: 20px; height: 20px;" />
                我想要
              </div>
            </div>
          </div>
        <p class="good-title">
          <span class="name">{{ item.name }}</span>
        </p>

        <p class="good-info">
          <span class="price">¥{{ item.price }}</span>
        </p>

      </div>
    </div>

    <div v-else class="empty">
      <img src="@/assets/imgs/empty.png" />
      <div class="text">暂无商品</div>
    </div>

  </div>
</template>

<script setup>
import {computed, reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import message from "@/assets/imgs/message1.png";
import collect from "@/assets/imgs/collect1.png";

const router = useRouter();
const route = useRoute();


const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      pageNum: 1,
      pageSize: 6,
      total: 0,
      tableData: [],
      categoryData: [],
      goodsData: [],
      userData: [],
      showNotice: true,

    }
)

// 加载收藏列表 + 商品信息
const load = async () => {
  try {
    // 获取当前用户收藏列表
    const collectRes = await request.get('/collect/selectByUserId');
    const collectList = collectRes.code === '200' ? collectRes.data : [];

    // 获取收藏商品列表
    const goodsRes = await request.get('/goods/selectAll'); // 假设这里获取所有商品
    const goodsList = goodsRes.code === '200' ? goodsRes.data : [];

    // 合并 collect 信息到商品
    data.tableData = collectList.map(c => {
      // 后端字段类型可能是 number/string 混用，这里统一做 Number 比较
      const g = goodsList.find(item => Number(item.id) === Number(c.itemId)) || {};
      return {
        ...g,                // 商品信息
        isCollected: true,   // 收藏状态
        collectId: c.id,     // 收藏表id
        collectPrice: c.collectPrice // 收藏表的价格
      };
    });

  } catch (err) {
    console.error(err);
    ElMessage.error('加载收藏列表失败');
  }
};

load()

const goToGoodsDetail = (item) => {
  if (item.status !== '已上架') {
    ElMessage.warning('该商品已卖出')
    return
  }
  router.push({
    path: '/front/goodsDetail',
    query: {
      id: item.id,
      from: 'collect'
    }
  })
}

const cancelCollect = (item) => {
  console.log(item)
  request.delete('/collect/delete/' + item.collectId).then(res => {
    if (res.code === '200') {
      ElMessage.success('已取消收藏')
      load()
    }
  })
}

const wantIt = (item) => {
  if (!item?.userId) {
    ElMessage.warning('无法确定聊天对象');
    return;
  }
  router.push({
    path: '/front/chat',
    query: {id: item.userId}
  })
}

// 选项
const filterOptions = [
  {label: '全部', value: 'all'},
  {label: '降价宝贝', value: 'discount'},
  {label: '无效宝贝', value: 'invalid'},
]

const selectedFilter = ref('all')


// 用户信息映射，方便查找
const userMap = {}
data.userData.forEach(u => userMap[u.id] = u)

// 过滤后的商品
const filteredGoods = computed(() => {
  let list = data.tableData

  if (selectedFilter.value === 'discount') {
    list = list.filter(item => item.collectPrice && item.price < item.collectPrice);
  } else if (selectedFilter.value === 'invalid') {
    list = list.filter(item => item.status === '已卖出')
  }

  return list
})


const selectFilter = (value) => {
  selectedFilter.value = value
}



</script>

<style scoped>
.collect-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
  overflow-y: auto;
}

/* 顶部选项 */
.filter-bar {
  display: flex;
  gap: 12px;
  padding: 10px 0;
}

.filter-item {
  padding: 10px 18px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s, color 0.2s;
}

.filter-item.active,
.filter-item:hover {
  background: #f0f0f0;
  font-weight: 600;
}

/* 商品列表 */
.goods {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 防止最后一行拉伸 */
  gap: 16px;

}

.goods {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr)); /* 一行4个 */
  gap: 16px;
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
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* 图片固定高度 + 保持比例 */
.good-img {
  width: 100%;
  height: 220px; /* 固定高度 */
  object-fit: cover; /* 保持比例裁剪，不拉伸 */
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


.pagination button {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background: #f0f0f0;
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



.hover-actions {
  position: absolute;
  left: 0;
  bottom: -40px;
  width: 100%;
  height: 40px;
  display: flex;
  transition: all 0.25s ease;
  background: rgba(0, 0, 0, 0.6);
  z-index: 3;
}
.good:hover .hover-actions {
  bottom: 0;
}
.action {
  flex: 1;
  color: #fff;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  gap: 6px;
  z-index: 4; 

}

.action.left {
  border-right: 1px solid rgba(255,255,255,0.3);
}

.action:hover {
  background: rgba(255,255,255,0.15);
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
