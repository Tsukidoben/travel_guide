const host = window.location.protocol + '//' + window.location.hostname + (window.location.port ? (":" + window.location.port) : '');
const backHost = "http://127.0.0.1:8088"

// 路由前缀
const projectPrefix = ""

const config = {
    // 当前地址
    host: host,

    // 后台地址
    backHost: backHost,
    uploadUrl: backHost + '/file/upload',
    downloadUrl: backHost + '/file/download/',
    projectPrefix: projectPrefix,
    frontUrl: host + projectPrefix + '/front',
    mvueUrl: host + projectPrefix + '/manage/home',
    maxLodingTime: 1000 * 60,
    projectName: "贵阳旅游攻略系统",
    logoTitle: "贵阳旅游攻略系统",
    companyName: "贵阳旅游攻略系统",
    companyNameE:'TRAVEL-GUIDE',
    msgNumFlag:true,
    
    // 高德地图 API Keys
    amapKeys: {
        // Web端(JS API) Key - 用于前端地图渲染
        jsApi: 'ce1da13e89ae4c9a4dfdde2447fba5a7',
        // Web服务 Key - 用于地图截图等后端服务
        webService: '3153060cf9d1f621b054becd3511c6a1'
    },
    routesWhiteList: [
        'login',
        'frontIndex',
        'home',
        'attractionList',
        'attractionDetail',
        'foodList',
        'foodDetail',
        'strategyPlaza',
        'strategyPlazaDetail',
        'hotelInfoFront',
        'hotelInfoDetail',
    ],
}

export default config;
