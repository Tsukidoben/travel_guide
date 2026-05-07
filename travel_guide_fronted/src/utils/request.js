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
        // 处理网络错误或响应错误
        if (error.response) {
            // 服务器返回了错误响应
            if (error.response.data && error.response.data.code == 500) {
                _this.$message.error(error.response.data.msg);
            } else {
                _this.$message.error('请求失败：' + (error.response.data?.msg || '未知错误'));
            }
        } else if (error.request) {
            // 请求已发送但没有收到响应（网络错误、后端未启动等）
            _this.$message.error('后端连接异常，请检查后端服务是否启动');
        } else {
            // 其他错误
            _this.$message.error('请求配置错误：' + error.message);
        }
        return Promise.reject(error)
    }
);


export default request

