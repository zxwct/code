<script setup>
import {computed, reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import { watch } from 'vue'

const router = useRouter();
const route = useRoute();

const keyword = ref('')
const categoryId = ref(0)





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


/* 排序 */
const sortType = ref('default') // default | new | price
const priceOrder = ref('asc')   // asc | desc


const load = () => {
  request.get('/goods/front/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      categoryId: categoryId.value,
      sortType: sortType.value,
      keyword: keyword.value,
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

watch(
    () => route.query,
    (query) => {
      categoryId.value = query.categoryId
          ? Number(query.categoryId)
          : 0
      keyword.value = query.keyword || ''
      data.pageNum = 1
      load()
    },
    { immediate: true }
)


/* 分类切换 */
const changeCategory = (id) => {
  categoryId.value = id
  load()
}

/* 排序切换 */
const changeSort = (type) => {
  sortType.value = type
  load()
}

/* 价格排序切换 */
const changePriceSort = () => {
  if (sortType.value !== 'priceAsc' && sortType.value !== 'priceDesc') {
    sortType.value = 'priceAsc'
    priceOrder.value = 'asc'
  } else {
    priceOrder.value = priceOrder.value === 'asc' ? 'desc' : 'asc'
    sortType.value = priceOrder.value === 'asc' ? 'priceAsc' : 'priceDesc'
  }
  load()
}


const loadCategory = () => {
  request.get('/category/selectPage').then(res => {
    if (res.code === '200') {
      data.categoryData = res.data.list
      data.categoryData.unshift({
        id: 0,
        name: '全部分类'
      })
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const sortedCategoryData = computed(() => {
  return [...data.categoryData].sort((a, b) => a.id - b.id)
})

loadCategory()


const loadUser = () => {
  request.get('/user/selectAll').then(res => {
    if (res.code === '200') {
      data.userData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadUser()


</script>

<template>
  <!-- 搜索页面主容器 -->
  <div class="search-page">

    <!-- card -->
    <div class="card1">

      <!-- 分类栏 -->
      <div class="category-bar">
        <span
            v-for="cat in sortedCategoryData"
            :key="cat.id"
            :class="['category-item', categoryId === cat.id ? 'active' : '']"
            @click="changeCategory(cat.id)"
        >
          {{ cat.name }}
        </span>
      </div>

      <!-- 排序栏 -->
      <div class="sort-bar">
        <span
            :class="['sort-item', sortType === 'default' ? 'active' : '']"
            @click="changeSort('default')"
        >
          综合排序
        </span>

        <span
            :class="['sort-item', sortType === 'new' ? 'active' : '']"
            @click="changeSort('new')"
        >
          最新发布
        </span>

        <span
            class="sort-item price-sort"
            :class="sortType === 'price' ? 'active' : ''"
            @click="changePriceSort"
        >
          价格
         <span class="arrow">
            <i :class="sortType.includes('price') && priceOrder === 'asc' ? 'up active' : 'up'"></i>
            <i :class="sortType.includes('price') && priceOrder === 'desc' ? 'down active' : 'down'"></i>
         </span>

        </span>
      </div>

      <!-- 商品列表（直接复用你的） -->
      <div class="goods">
        <div
            v-for="item in data.tableData"
            :key="item.id"
            class="good"
        >
          <img :src="item.img" class="good-img"/>

          <p class="good-title">
            <span class="shipment">{{ item.shipment }}</span>
            <span class="name">{{ item.name }}</span>
          </p>

          <p class="good-info">
            <span class="price">¥{{ item.price }}</span>
            <span class="place">{{ item.place.split('/')[0] }}</span>
          </p>

          <div class="divider"></div>

          <div class="user">
            <img
                :src="data.userData.find(user => user.id === item.userId).avatar"
                class="avatar"
            />
            <span class="username">
             {{ data.userData.find(user => user.id === item.userId).username }}
            </span>
          </div>
        </div>
      </div>

    </div>

    <div class="card1">
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

  </div>


</template>

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


/* ================= 分类栏 ================= */

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 18px;
}

/* 分类按钮 - 默认 */
.category-item {
  padding: 8px 18px; /* ⬅ 更大 */
  font-size: 14px; /* ⬅ 更清晰 */
  color: #444;
  background: #f7f7f7;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

/* hover 状态（浅黄，不刺眼） */
.category-item:hover {
  background: rgba(255, 230, 16, 0.18);
  color: #a88600;
}

/* 选中状态 */
.category-item.active {
  background: #ffe610;
  color: #333;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(255, 230, 16, 0.45);
}

/* ================= 排序栏 ================= */

.sort-bar {
  display: flex;
  gap: 22px;
  font-size: 14px;
  margin-bottom: 16px;
}

/* 排序项 - 默认 */
.sort-item {
  color: #555;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 6px 14px; /* ⬅ 关键 */
  border-radius: 999px; /* ⬅ 椭圆 */
  transition: all 0.2s ease;
}

/* hover */
.sort-item:hover {
  background: #f2f2f2;
  color: #333;
}

/* 选中 */
.sort-item.active {
  background: #eeeeee;
  color: #333;
  font-weight: 600;
}

/* ================= 价格排序箭头 ================= */

.price-sort {
  display: flex;
  align-items: center;
}

.arrow {
  margin-left: 4px;
  display: flex;
  flex-direction: column;
}

/* 箭头基础（整体加粗） */
.arrow i {
  width: 0;
  height: 0;
  border-left: 5px solid transparent; /* ⬅ 加粗 */
  border-right: 5px solid transparent; /* ⬅ 加粗 */
}

/* 默认箭头（更深灰） */
.arrow .up {
  border-bottom: 8px solid #999; /* ⬅ 更深 */
  margin-bottom: 3px;
}

.arrow .down {
  border-top: 8px solid #999; /* ⬅ 更深 */
}

/* 激活箭头（高对比黄色） */
.arrow .active {
  border-color: #f5c400 transparent; /* ⬅ 比 #ffe610 深一点 */
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
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* 只作用于商品主图 */
.good > img {
  width: 100%;
  height: 160px; /* 固定高度 */
  object-fit: cover; /* 保持比例裁切 */
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
  text-decoration-color: #ffe610;
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