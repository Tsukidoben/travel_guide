import axios from "axios";
import Vue from "vue";
import store from '@/store';
import router from '../router/index'
import config from "@/config/config";

const request = axios.create({
    // 设置默认的请求头
    method: "post",
    // 设置超时时间
    timeout: config.maxLodingTime,
})
//请求拦截器
request.interceptors.request.use(
    config => {
        // 在请求发送之前，对请求进行处理
        // 比如，添加 token 到请求头中
        const token = localStorage.getItem('token')
        if (token) {
            // config.headers['Authorization'] = `${token}`
            config.headers['Authorization'] = `${token}`
        }
        return config
    },
    error => {
        // 请求出错时，进行处理
        console.log(error)
        Promise.reject(error)
    }
)
let _this =
    new Vue({
        router
    })

// 响应拦截器
request.interceptors.response.use(
    response => {
        const data = response.data;
        if (data.code === 200) {
            return data;
        } else {
            return data;
        }
    },
    error => {
        if (error.response.data.code == 500) {
            _this.$message.error(error.response.data.msg);
        } else {
            _this.$message.error('后端连接异常');
        }
        return Promise.reject(error)
    }
);


export default request

