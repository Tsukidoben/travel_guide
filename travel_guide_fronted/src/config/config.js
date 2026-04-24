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
