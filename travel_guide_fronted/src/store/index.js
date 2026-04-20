import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import config from "@/config/config";
import router from "@/router";
Vue.use(Vuex);

const store = new Vuex.Store({
    state: {
        user: null,
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        },
        clearUser(state) {
            state.user = null;
        }
    },
    actions: {
        login({ commit }, { user, token }) {
            commit('setUser', user);
            localStorage.setItem('token', token);
        },
        logout({ commit }) {
            // 清除 Vuex 中的用户信息
            commit('clearUser');
            // 清除 localStorage 中的 token
            localStorage.removeItem('token');
        },
        async fetchUser({ commit, state }) {
            // 从 localStorage 获取 token
            let token = localStorage.getItem('token');
            if (!token) return
            try {
                const response = await axios.post(
                    config.backHost + "/user/getCurrentUser",
                    {},
                    { headers: { Authorization: token } }
                );
                if( response.data.code === 200){
                    // 将用户信息保存到 Vuex 中
                    commit('setUser', response.data.data);
                } else {
                    console.error('获取用户信息失败');
                    commit('clearUser'); // 请求失败时清除用户信息
                    // 这里可以根据需要清除 localStorage 中的 token
                    localStorage.removeItem('token');
                    router.replace({ name: "login" });
                }
            } catch (error) {
                console.error('获取用户信息失败', error);
                commit('clearUser'); // 请求失败时清除用户信息
                // 这里可以根据需要清除 localStorage 中的 token
                localStorage.removeItem('token');
            }
        }
    },
    getters: {
        getUser: state => state.user,
        getToken: () => localStorage.getItem('token'),  // 从 localStorage 获取 token
        isAuthenticated: () => !!localStorage.getItem('token'), // 判断是否认证
    },
});

export default store;
