import config from "@/config/config";

export default {
    data(){
        return{
            imageUrl: '',
            showPreview: false,
        }
    },
    methods: {
        getUrlParams(url) {
            const params = {};
            const queryString = url.split('?')[1];
            if (queryString) {
                const searchParams = new URLSearchParams(queryString);
                for (const [key, value] of searchParams) {
                    params[key] = value;
                }
            }
            return params;
        },
        getPicUrlByJson(json,index = 0){
            if(json){
                if(json.indexOf("[{") != -1 && json.indexOf("}]")!=-1){
                    // 转json
                    return  config.downloadUrl + JSON.parse(json)[index].id
                }else{
                    return json
                }
            }else{
                return ""
            }
        },
        getDownUrl(json,index = 0){
            if(json){
                if(json.indexOf("[{") != -1 && json.indexOf("}]")!=-1){
                    // 转json
                    return  config.downloadUrl + JSON.parse(json)[index].id
                }else{
                    return json
                }
            }else{
                return ""
            }
        },
        getFileName(json,index = 0){
            if(json){
                if(json.indexOf("[{") != -1 && json.indexOf("}]")!=-1){
                    // 转json
                    return  JSON.parse(json)[index].fileName
                }else{
                    return json
                }
            }else{
                return ""
            }
        },
        showFullscreen(url) {
            this.showPreview = true;
            this.imageUrl = url;
        },
        closePreview(){
            this.showPreview = false;
        },
        getHtmlPlainText(html_str) {
            let re = new RegExp('<[^<>]+>', 'g');
            return html_str.replace(re, "");
        },
        isEmpty(val){
            if(val == null || val === '' || typeof (val) =='undefined'){
                return true;
            }
            return false;
        },
        timestampToYMD(timestamp) {
            if(this.isEmpty(timestamp)){
                return ''
            }
            //将时间戳格式转换成年月日时分秒
            var date = new Date(timestamp);
            var Y = date.getFullYear() + '-';
            var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
            return Y + M + D;
        },
        isEmail(email) {
            let reg = /^([a-zA-Z]|[0-9])(\w|)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
            return reg.test(email) || !email;
        },
        // 判断手机号是否合法
        isPhoneNumber(tel) {
            let reg = /^1[345789]\d{9}$/;
            return reg.test(tel);
        },
        isEnglishOrNumber(value) {
            let reg = /^[A-Za-z0-9]+$/;
            return reg.test(value);
        },
        timestampToYMDHMS(timestamp) {
            if(this.isEmpty(timestamp)){
                return ''
            }
            //将时间戳格式转换成年月日时分秒
            var date = new Date(timestamp);
            var Y = date.getFullYear() + '-';
            var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';

            var h = (date.getHours() < 10 ? '0' + (date.getHours()) : date.getHours()) + ':';
            var m = (date.getMinutes() < 10 ? '0' + (date.getMinutes()) : date.getMinutes()) + ':';
            var s = (date.getSeconds() < 10 ? '0' + (date.getSeconds()) : date.getSeconds());
            return Y + M + D + h + m + s;
        }
    }
}
