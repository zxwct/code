<template>
  <div class="container">
    <!-- 左侧 -->
    <div class="left">
      <!-- 收货地址 -->
      <div class="card1">
        <div class="card-title">
          <span>收货地址</span>
          <span class="manage" @click="router.push('/front/address')">管理地址</span>
        </div>

        <div class="address-box" v-if="data.tableData===null">
          <div class="add-btn"  @click="router.push('/front/address')">
            <span>+</span>
            添加收货地址
          </div>
        </div>

        <div class="address-list" v-else>
          <div
              v-for="address in data.tableData"
              :key="address.id"
              class="address-card"
              :class="{ selected: selectedAddress === address.id }"
              @click="selectAddress(address.id)"
          >
            <div class="card-left">
              <img
                  :src="selectedAddress === address.id ? addressing : addressed "
                  alt="地址图标"
              />
            </div>
            <div class="card-right">
              <div class="region">{{ address.address }}</div>
              <div class="info">{{ address.info }}</div>
              <div class="contact">{{ address.name }} {{ address.phone }}</div>
            </div>
          </div>
        </div>

      </div>

      <!-- 订单信息 -->
      <div class="card1">
        <div class="card-title">订单信息</div>

        <div class="order-item">
          <img
              class="order-img"
              :src="data.goodsData.img"
          />
          <div class="order-info">
            <div class="name">
              {{ data.goodsData.name }}
              <span
                  v-if="data.goodsData.shipment"
                  class="shipment-tag"
              >
                {{ data.goodsData.shipment }}
              </span>
            </div>
            <div class="place">
              {{ data.goodsData.place }}
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- 右侧 -->
    <div class="right">
      <div class="card1">
        <div class="card-title">价格明细</div>

        <div class="row">
          <span>商品价格</span>
          <span>¥{{ goodsPrice }}</span>
        </div>
        <div class="row">
          <span>运费</span>
          <span>¥{{ freightPrice }}</span>
        </div>

        <div class="total">
          <span>合计</span>
          <span>¥{{ totalPrice }}</span>
        </div>

        <button class="buy-btn" @click="submitOrder">
          确认购买
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed, reactive, ref,onMounted} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import addressed from '@/assets/imgs/address.png'
import addressing from '@/assets/imgs/address1.png'


const router = useRouter();
const route = useRoute();


const id = ref(route.query.id)

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      tableData: [],
      goodsData: {},
      userData: [],
      showNotice: true,
    }
)


const load = () => {
  request.get('/address/selectByUserId').then(res => {
    if (res.code === '200') {
      data.tableData = res.data

      if (data.tableData.length > 0) {
        selectedAddress.value = data.tableData[0].id
      }

    } else {
      ElMessage.error(res.msg)
    }
  })
}

load()


const loadGoods = () => {
  request.get('/goods/selectById/' + id.value).then(res => {
    if (res.code === '200') {
      data.goodsData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadGoods()

const goodsPrice = computed(() => {
  const price = data.goodsData?.price || 0
  return Number(price).toFixed(2)
})

const freight = computed(() => {
  if (data.goodsData?.shipment === '邮费到付') {
    return 3
  }
  return 0
})

const freightPrice = computed(() => {
  return freight.value.toFixed(2)
})

const totalPrice = computed(() => {
  const price = data.goodsData?.price || 0
  return (price + freight.value).toFixed(2)
})

const selectedAddress = ref(null)

const selectAddress = (id) => {
  selectedAddress.value = id
}

const submitOrder = () => {
  if (!data.user.id) {
    ElMessage.warning('请先登录');
    return;
  }
  if(selectedAddress.value === ''){
    ElMessage.error('请选择您的收货地址')
    return;
  }
  if(data.goodsData.status !== '已上架'){
    ElMessage.error('商品未上架或已卖出，请联系卖家确认')
    return;
  }
  request.post('/orders/add',{
    goodsId: id.value,
    addressId: selectedAddress.value
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('已下单，请及时支付订单！')
      router.push('/front/home')
    } else {
      ElMessage.error(res.msg)
    }
  })

}


</script>

<style scoped>
.container {
  width: 1520px;
  margin: 20px auto;
  display: flex;
  gap: 20px;
}

.left {
  flex: 1;
}

.right {
  width: 320px;
}

/* 你给的卡片样式 */
.card1 {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.manage {
  font-size: 14px;
  color: #999;
  cursor: pointer;
}

/* 地址 */
.address-box {
  border: 1px dashed #e5e5e5;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
}

.add-btn {
  color: #ff5000;
  font-size: 16px;
  margin-bottom: 8px;
  cursor: pointer;
}

.add-btn span {
  display: inline-block;
  width: 20px;
  height: 20px;
  background: #ffefe5;
  border-radius: 50%;
  line-height: 20px;
  margin-right: 6px;
}

.address-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;   /* 卡片间距 */
}

.address-card {
  display: flex;
  width: calc(25% - 12px);  /* 一行4个，减去gap */
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.address-card:hover {
  border-color: #f68452;  /* 悬浮状态边框橙色 */
  box-shadow: 0 4px 12px rgba(246, 161, 118, 0.25);
}

.address-card.selected {
  border-color: #f68452;  /* 选中状态边框 */
  box-shadow: 0 4px 12px rgba(246, 161, 118, 0.25);
}

.card-left img {
  width: 30px;
  height: 30px;
}

.card-right {
  display: flex;
  flex-direction: column;
  margin-left: 12px;
  justify-content: space-between;
}

.card-right .region {
  font-size: 14px;
  color: #606266;
}

.card-right .info {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 4px 0;
}

.card-right .contact {
  font-size: 14px;
  color: #909399;
}



/* 订单 */
.order-item {
  display: flex;
  gap: 16px;
  align-items: center;
}

.order-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
}

.order-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 120px; /* 与图片高度一致 */
}

.order-info .name {
  font-size: 14px;
  margin-top: 0;
  margin-bottom: 6px;
}

.shipment-tag {
  margin-left: 8px;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 10px;
  background: #fff3e6;
  color: #ff5000;
  vertical-align: middle;
}

.order-info .place {
  margin-top: auto;       /* 仍然贴底 */
  font-size: 13px;
  color: #999;
}


.count button {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background: #f2f2f2;
  cursor: pointer;
}

/* 价格 */
.row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
  font-size: 18px;
  color: #ff5000;
  display: flex;
  justify-content: space-between;
}

.buy-btn {
  margin-top: 20px;
  width: 100%;
  height: 44px;
  border-radius: 22px;
  background: #ff5000;
  color: #fff;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
</style>
