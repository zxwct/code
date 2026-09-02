import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const request = axios.create({
    // 生产环境由宝塔 Nginx 将 /api 转发到 Spring Boot。
    // 开发环境由 vite.config.js 中的代理转发到 localhost:9999。
    baseURL: '/api',
    timeout: 10000
})

//request 拦截器
//可以自请求发送前对请求做一些处理

request.interceptors.request.use(config => {
        config.headers['Content-Type'] = 'application/json;charset=utf8';
        let user = JSON.parse(localStorage.getItem('code_user') || '{}')
        config.headers['token']= user.token

        return config

    }, error => {
        return Promise.reject(error)

    }
);

//response 拦截器
//可以在接口响应后统一处理结果

request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务器返回的字符串数据
        if (typeof res == "string") {
            res = res ? JSON.parse(res) : res
        }
        if(res.code === '401'){
            ElMessage.error(res.msg)
            router.push('/login')
        }else{
            return res;
        }
    },
    error =>{
        if(error.response.status === 404){
            ElMessage.error('未找到请求接口')
        }else if(error.response.status === 500 ){
            ElMessage.error('系统异常，请查看后端控制台报错')
        }else {
            console.error(error.message)
        }
        return Promise.reject(error)
    }
    )

export default request



