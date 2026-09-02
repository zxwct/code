<script setup>

import {computed, reactive, ref} from 'vue'
import request from "@/utils/request.js";
import {ElMessage,ElMessageBox} from "element-plus";
import {useRouter, useRoute} from "vue-router";
import {watch} from 'vue'
import {regionData} from "element-china-area-data";


const router = useRouter();
const route = useRoute();

const keyword = ref('')

const activeTab = ref('全部')


const props = defineProps({
  sideBar: { type: String, default: 'SELL' }
})

const formRef = ref()

const data = reactive({
      user: JSON.parse(localStorage.getItem('code_user') || '{}'),
      tableData: [],
      goodsData: [],
      userData: [],
      form: {},
      formVisible: false,
      rules: {
        toRate: [
          {required: true, message: '请选择评分', trigger: 'blur'}
        ],
        toReview: [
          {required: true, message: '请填写评价', trigger: 'blur'}
        ],

      },

    }
)

const load = () => {
  request.get('/orders/selectByUserId', {
    params: {
      keyword: keyword.value,
      activeTab: activeTab.value,  // ✅ 传真实 tab
      sideBar: props.sideBar
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

load()

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

const onSearch = () => {
  load()
}

const changeTab = (tab) => {
  activeTab.value = tab
  load()   // ✅ 切换就查
}


const tabs = ['全部', '待付款', '待发货', '待收货', '待评价', '已完成','已取消']



const payOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认要支付该订单吗？', '提示', {
      type: 'warning'
    })

    const res = await request.post('/orders/pay/'+order.id)

    if (res.code === '200') {
      ElMessage.success('支付成功')
      order.status = '待发货'
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    // 点击取消会进入这里，不提示错误
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('支付失败')
    }
  }
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认要取消该订单吗？', '提示', {
      type: 'warning'
    })

    const res = await request.post('/orders/cancel/'+order.id)

    if (res.code === '200') {
      ElMessage.success('订单已取消')
      order.status = '已取消'
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('取消订单失败')
    }
  }
}

const shipOrder = async (order) => {

  const res = await request.post('/orders/ship/'+order.id)

  if (res.code === '200') {
    ElMessage.success('发货成功')
    order.status = '待收货'
  } else {
    ElMessage.error(res.msg)
  }
  load()

}

const confirmReceive = async (order) => {

  const res = await request.post('/orders/confirm/'+order.id)

  if (res.code === '200') {
    ElMessage.success('确认收货成功')

  } else {
    ElMessage.error(res.msg)
  }

  load()


}

const goComment = (order) => {

  data.form = JSON.parse(JSON.stringify(order))
  data.formVisible = true

}

const save = () => {
  // 表单验证
  formRef.value.validate((valid) =>{
    if(valid){
      request.put('/orders/comment',data.form).then(res =>{
        if(res.code === '200'){

          ElMessage.success('评价成功')
          data.formVisible=false

          load()
        }else{
          ElMessage.error(res.msg)
        }
      })
    }
  })
}





watch(() => props.sideBar, () => {
  load() // sideBar变化时重新加载数据
}, { immediate: true })


</script>

<template>
  <div class="orders-wrapper">

    <!-- 第一部分：订单号搜索 -->
    <div class="card">
      <div class="search-bar">
        <input v-model="keyword" placeholder="请输入订单号"/>
        <button @click="onSearch">搜索</button>
      </div>
    </div>

    <!-- 第二部分：状态筛选 -->
    <div class="card">
      <div class="status-tabs">
        <div
            v-for="tab in tabs"
            :key="tab"
            class="tab"
            :class="{ active: activeTab === tab }"
            @click="changeTab(tab)"
        >
          {{ tab }}
        </div>
      </div>
    </div>

    <!-- 第三部分：订单列表 -->
    <div v-if="data.tableData.length" class="card">

      <div
          v-for="order in data.tableData"
          :key="order.id"
          class="order-card"
      >
        <!-- 订单头 -->
        <div class="order-header">
          <div>订单号：{{ order.no }}</div>
          <div class="seller-info">
            <span class="seller-label">买家:</span>
            <img :src="data.userData.find(user => user.id === order.toId)?.avatar"/>
            <span>{{ data.userData.find(user => user.id === order.toId)?.username }}</span>
          </div>
        </div>

        <div class="divider"></div>

        <!-- 商品信息 -->
        <div class="order-body">
          <img :src="order.goodsImg"/>
          <div class="product-info">
            <div class="product-name">{{ order.goodsName }}</div>
            <div class="order-time">下单时间：{{ order.time }}</div>
            <div class="product-price">¥{{ order.price }}</div>
          </div>
        </div>

        <div class="divider"></div>

        <!-- 订单状态 -->
        <div class="order-footer">
          <!-- 左侧：订单状态 -->
          <div class="order-status">
            {{ order.status }}
          </div>

          <!-- 右侧：操作按钮 -->
          <div class="order-actions">

            <!-- 待付款 -->
            <template v-if="order.status === '待付款' && sideBar === 'BUY'">
              <button class="btn btn-cancel" @click="cancelOrder(order)">
                取消订单
              </button>
              <button class="btn btn-primary" @click="payOrder(order)">
                立即付款
              </button>
            </template>

            <!-- 待发货（我卖出的） -->
            <template v-else-if="order.status === '待发货' && sideBar === 'SELL'">
              <button class="btn btn-primary" @click="shipOrder(order)">
                立即发货
              </button>
            </template>

            <!-- 待收货（我买到的） -->
            <template v-else-if="order.status === '待收货' && sideBar === 'BUY'">
              <button class="btn btn-primary" @click="confirmReceive(order)">
                确认收货
              </button>
            </template>

            <!-- 待评价 -->
            <template v-else-if="order.status === '待评价' && sideBar === 'BUY' && !order.toReview">
              <button class="btn btn-primary" @click="goComment(order)">
                去评价
              </button>
            </template>

            <!-- 已完成 -->
            <template v-else-if="order.status === '已完成'">

            </template>

          </div>
        </div>

      </div>
    </div>

    <div v-else class="empty">
      <img src="@/assets/imgs/empty.png" />
      <div class="text">暂无订单</div>
    </div>

  </div>

  <el-dialog v-model="data.formVisible" title="评价" width="50%" destroy-on-close>

    <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding: 20px 30px 10px 0">

      <el-form-item prop="toRate" label="买家评分">
        <el-rate v-model="data.form.toRate" />
      </el-form-item>

      <el-form-item prop="toReview" label="买家评价">
        <el-input v-model="data.form.toReview" type="textarea" autocomplete="off" placeholder="请输入买家评价"/>
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

</template>

<style scoped>
.orders-wrapper {
  padding: 8px;

}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #f1da44; /* 淡黄边框 */
}

/* 搜索 */
.search-bar {
  display: flex;
  gap: 12px;
}

.search-bar input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.search-bar button {
  height: 36px;
  padding: 0 16px;
  border-radius: 8px;
  border: none;
  background: #000;
  color: #fff;
  cursor: pointer;
}

/* Tabs */
.status-tabs {
  display: flex;
  gap: 24px;
  font-size: 14px;
}

.tab {
  cursor: pointer;
  padding-bottom: 6px;
  color: #666;
}

.tab.active {
  color: #000;
  font-weight: 600;
  border-bottom: 2px solid #ffd100;
}

/* 订单卡片 */
.order-card {
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  font-size: 13px;
  background: #fafafa;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.seller-label {
  font-weight: bold; /* 加粗 */
  font-size: 16px; /* 字体加大，可根据需要调整 */
}

.seller-info img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.divider {
  height: 1px;
  background: #eee;
}

.order-body {
  display: flex;
  padding: 16px;
  gap: 10px;
}

.order-body img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.product-price {
  margin-top: auto;
  font-size: 14px;
  font-weight: 600;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;

}

.order-status {
  color: #ff4d4f;
  font-size: 14px;
}

.order-actions button {
  margin-left: 8px;
}

.btn {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
  cursor: pointer;
}

.btn-primary {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.btn-cancel {
  background: #fff;
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
