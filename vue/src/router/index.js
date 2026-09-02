import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/', redirect: '/front/home'},
    {path: '/manager', component:() =>  import('../views/Manager.vue'),
      children: [
        {path: 'home',meta:{name: '主页'}, component:() => import('../views/manager/Home.vue'),},
        {path: 'admin', meta:{name: '管理员信息'}, component:() =>  import('../views/manager/Admin.vue'),},
        {path: 'user', meta:{name: '普通用户信息'}, component:() =>  import('../views/manager/User.vue'),},
        {path: 'person', meta:{name: '个人信息'}, component:() =>  import('../views/manager/Person.vue'),},
        {path: 'updatePassword', meta:{name: '修改密码'}, component:() =>  import('../views/manager/UpdatePassword.vue'),},
        {path: 'notice', meta:{name: '系统公告'}, component:() =>  import('../views/manager/Notice.vue'),},
        {path: 'category', meta:{name: '商品分类'}, component:() =>  import('../views/manager/Category.vue'),},
        {path: 'banner', meta:{name: '轮播图信息'}, component:() =>  import('../views/manager/Banner.vue'),},
        {path: 'goods', meta:{name: '闲置物品信息'}, component:() =>  import('../views/manager/Goods.vue'),},
        {path: 'address', meta:{name: '收货地址管理'}, component:() =>  import('../views/manager/Address.vue'),},
        {path: 'orders', meta:{name: '订单管理'}, component:() =>  import('../views/manager/Orders.vue'),},

      ]
    },
    {path: '/front', component:() =>  import('../views/Front.vue'),
      children: [
        {path: 'home',meta:{name: '首页'}, component:() => import('../views/front/Home.vue'),},
        {path: 'publish',meta:{name: '发布'}, component:() => import('../views/front/Publish.vue'),},
        {path: 'chat',meta:{name: '消息'}, component:() => import('../views/front/Chat.vue'),},
        {path: 'search',meta:{name: '搜索'}, component:() => import('../views/front/Search.vue'),},
        {path: 'goodsDetail',meta:{name: '商品详情'}, component:() => import('../views/front/GoodsDetail.vue'),},
        {path: 'userDetail',meta:{name: '卖家详情'}, component:() => import('../views/front/UserDetail.vue'),},
        {path: 'confirm',meta:{name: '下单确认'}, component:() => import('../views/front/Confirm.vue'),},
        {path: 'address',meta:{name: '收货地址'}, component:() => import('../views/front/Address.vue'),},
        {
          path: 'person',
          meta: { name: '个人中心' },
          component: () => import('../views/front/Person.vue')
        }


      ]
    },

    {path: '/login',  component: () => import('../views/Login.vue'),},
    {path: '/register',  component: () => import('../views/Register.vue'),},
    {path: '/error',  component: () => import('../views/404.vue'),},
    {path: '/:pathMatch(.*)', redirect:'/error'}
  ],
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem("code_user") || "{}")
  const token = user?.token

  // ✅ 1. 后台必须 ADMIN
  if (to.path.startsWith("/manager")) {
    if (!token) {
      ElMessage.warning("请先登录")
      return next("/login")
    }
    if (user.role !== "ADMIN") {
      return next("/error")
    }
    return next()
  }

  // ✅ 2. 前台开放页面（不用登录）
  const publicPaths = [
    "/front/home",
    "/front/search",
    "/front/goodsDetail",
    "/front/userDetail"
  ]

  if (publicPaths.includes(to.path)) {
    return next()
  }

  // ✅ 3. 前台其他页面（需要登录）
  if (to.path.startsWith("/front")) {
    if (!token) {
      ElMessage.warning("请先登录")
      return next(from.path && from.path.startsWith("/front") ? false : "/front/home")
    }
  }

  next()
})


export default router
