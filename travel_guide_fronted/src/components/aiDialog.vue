<template>
  <div class="bigBox">
    <div class="radioMax" v-if="drawer === false">
      <el-tooltip class="item" effect="dark" content="点击咨询智能客服" placement="left">
        <div class="floating-ball" @mouseenter="setHover(true)" @mouseleave="setHover(false)" @click="openAI">
          <div style="height: 100%;width: 100%;text-align: center;line-height: 50px;color: #FFFFFF;font-size: 14px" v-show="!ishover">
            AI客服
          </div>
        </div>
      </el-tooltip>
    </div>
    <el-drawer
        :wrapperClosable="false"
        :size="'50%'"
        :append-to-body="true"
        :visible.sync="drawer"
        :show-close="false"
        @closed="clearData"
        :direction="'rtl'">
      <template v-slot:title>
        <div style="display:flex;justify-content: space-between;">
          <div style="font-weight: bold;color: #000000;font-size:20px">智能客服</div>
          <div style="font-size:24px;color:#333333">
            <i class="el-icon-refresh-right" style="cursor:pointer;margin-right:8px" @click="reload"></i>
            <i class="el-icon-close" style="cursor:pointer" @click="drawer = false"></i>
          </div>
        </div>
      </template>
      <div style="height: 100%;background: linear-gradient(to bottom, #FFFFFF, #EDE0D0);">
        <div ref="scrollContainer" style="height:calc(100% - 100px);width:100%;padding: 0 20px;box-sizing: border-box;overflow-y: auto">
          <div class="talk" v-for="(item,index) in dataArray" :key="index">
            <div style="display: flex;" class="myTalk">
              <div  style="margin-right: 8px">
                {{ item.userQues }}
              </div>
              <el-avatar size="medium" :src="getPicUrlByJson(userInfo.headPicUrl,0)"></el-avatar>
            </div>
            <div style="display: flex;" class="aiTalk">
              <el-avatar size="medium" :src="require('@/assets/imgs/aiHead.png')"></el-avatar>
              <div v-if="ailodingFlag && (index === dataArray.length-1)" style="width: 16px;margin-left: 8px" class="ailoading"></div>
              <div v-else style="margin-left: 8px" v-html="getAiContent(item.aiResult)">
              </div>
            </div>
          </div>
        </div>
        <div style="height:60px;margin:10px auto 30px;width:90%;background: rgb(248,241,233);border-radius: 40px;padding:6px 12px;box-sizing: border-box;display: flex">
          <div class="searchInput" style="background:#FFFFFF;width: calc(100% - 60px);height: 100%;border-radius: 40px;overflow: hidden">
            <el-input v-model="searchWord" placeholder="请输入内容" @keyup.enter.native="sendRequest()"></el-input>
          </div>
          <div v-loading="loading" style="overflow:hidden;height:48px;width:48px;border-radius:50%;margin-left:8px;flex-shrink: 0;background:#FFFFFF;font-size:24px;line-height: 48px;text-align: center">
            <i class="el-icon-top" style="cursor:pointer;margin-top:12px" @click="sendRequest()"></i>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import config from "@/config/config";
import common from "@/utils/common";
import request from "@/utils/request";
import { marked } from "marked";
import debounce from 'lodash.debounce';
export default {
  name: "aiDialog",
  mixins: [common],
  data() {
    return {
      renderMathJax: debounce(() => {
        // 获取渲染内容的容器元素
        const container = this.$refs.scrollContainer;
        if (window.MathJax && container) {
          // 只渲染 container 中的公式
          window.MathJax.typeset([container]);
        }
      }, 500),// 防抖时间设置为 500ms
      drawer:false,
      ishover: false,
      dataArray:[],
      searchWord:'',
      loading:false,
      ailodingFlag:false,
    }
  },
  computed: {
    userInfo(){
      return  this.$store.getters.getUser;
    }
  },
  mounted() {
    // 执行 MathJax 渲染（使用防抖处理）
    this.renderMathJax();
  },
  beforeDestroy() {
    if (window.MathJax && window.MathJax.typesetClear) {
      window.MathJax.typesetClear(); // 清除待渲染的公式
    }
  },
  methods: {
    //清空数据
    clearData() {
      this.searchWord = ''
      this.dataArray = []
    },
    openAI() {
      this.drawer = true
      this.ishover = false
      this.ailodingFlag = false
      this.getHistoryAI()
    },
    // 滚动到页面底部
    scrollToBottom() {
      this.$nextTick(() => {
        //如果没有内容就没必要执行滚动
        if (this.dataArray.length === 0) {
          return
        }
        const scrollContainer = this.$refs.scrollContainer;
        if (!scrollContainer) {
          return;
        }
        const targetScrollTop = scrollContainer.scrollHeight;
        const currentScrollTop = scrollContainer.scrollTop;
        const scrollDuration = 1000; // 1秒内滚动到底部
        let startTime = null;
        const scrollAnimation = (currentTime) => {
          if (!startTime) startTime = currentTime;
          const progress = (currentTime - startTime) / scrollDuration;
          const newScrollTop = currentScrollTop + (targetScrollTop - currentScrollTop) * Math.min(progress, 1);
          scrollContainer.scrollTop = newScrollTop;
          if (progress < 1) {
            requestAnimationFrame(scrollAnimation);
          }
        };
        requestAnimationFrame(scrollAnimation);
      });
    },
    reload() {
      this.dataArray = []
      this.getHistoryAI()
      this.loading = false;
      this.ailodingFlag = false
    },
    setHover(flag) {
      this.ishover = flag
    },
    // 处理 MathJax 公式，确保正确格式
    handleInlineFormula(content) {
      // 设置换行
      // content = content.replace(/\n/g, '<br/>');

      // 先处理 \(...\) 和 \[...\] 公式
      content = content.replace(/\\\([^\\)]*\\\)/g, (match) => {
        return match.replace(/\\/g, '____');  // 临时替换反斜杠
      });

      // 将普通的 [公式] 替换为 \(公式\)
      content = content.replace(/\[([^\]]+)\]/g, '\\($1\\)');

      // 处理 \boxed{x}，确保 MathJax 能识别
      content = content.replace(/\\boxed{([^}]+)}/g, '\\boxed{$1}');

      // 处理 \quad 等空格符号
      content = content.replace(/\\quad/g, '  '); // 可以考虑替换为多个空格，MathJax会自动处理

      // 处理 \text{} 中的内容，确保 MathJax 不误解
      content = content.replace(/\\text{([^}]+)}/g, ' \\text{$1} ');

      // 将普通的 ( ... ) 替换为 \( ... \) 格式
      content = content.replace(/\(.*?\)/g, (match) => {
        return `\\(${match.slice(1, -1)}\\)`;  // 去掉括号后添加 MathJax 格式
      });

      // 恢复之前替换的反斜杠
      content = content.replace(/____/g, '\\');

      return content;
    },
    // 解析Markdown
    getAiContent(content) {
      if (!content) return "";
      content = content.replace(/\\\(([^\\]|\\[^)])*\\\)/g, (match) => {
        return match.replace(/\\/g, '\\\\'); // 关键：每个 \ 替换为 \\
      });
      // 预处理公式，避免被 Markdown 转义
      content = this.handleInlineFormula(content);
      // 解析 Markdown
      let htmlContent = marked(content);
      // 使用 Vue 的 $nextTick 确保 DOM 渲染后执行 MathJax
      this.$nextTick(() => {
        if (this.$el) { // 组件仍然存在
          this.renderMathJax();
        }
      });

      return htmlContent;
    },
    getHistoryAI() {
      request({
        url: config.backHost + "/ai/getAiList?limit=10"
      }).then(res => {
        if (res.code == 200) {
          this.dataArray = res.data.reverse();
          this.scrollToBottom()
        } else {
          this.dataArray = []
        }
      })
    },
    async sendRequest() {
      if (this.searchWord.trim() === '') {
        return
      }
      this.loading = true
      let _this = this
      let inputData = this.searchWord
      this.dataArray.push({
        "aiResult": "",
        "createTime": '',
        "id": "",
        "userId": "",
        "userQues": this.searchWord
      });
      this.dataArray = JSON.parse(JSON.stringify(this.dataArray))
      this.searchWord = ''
      this.scrollToBottom()
      this.ailodingFlag = true
      const response = await fetch(config.backHost + '/ai/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": localStorage.getItem("token")
        },
        body: JSON.stringify({
          input: inputData
        })
      });
      var rs = "";
      let text = ''
      const reader = response.body.getReader();

      async function processStream() {
        const {done, value} = await reader.read();
        if (done) {
          _this.loading = false;
          return;
        }
        // 累加流中的数据
        rs += new TextDecoder().decode(value);
        _this.ailodingFlag = false
        //获取到完成的数据然后去掉data:
        let decodedData = rs.toString().split('data:')
        //将数组遍历转为对象
        text = ''
        decodedData.forEach(item => {
          if (item) {
            try {
              const data = JSON.parse(item);
              if(data.type != 'status'){
                // data.results.forEach(items => {
                text += data.content
                // })
                _this.dataArray[_this.dataArray.length - 1].aiResult = text
                _this.dataArray = JSON.parse(JSON.stringify(_this.dataArray))
                _this.scrollToBottom()
              }else{
                text += "<p style='color: #2b65ff;margin: 5px 0'>" +data.content + '</p>'
                _this.dataArray[_this.dataArray.length - 1].aiResult = text
                _this.dataArray = JSON.parse(JSON.stringify(_this.dataArray))
                _this.scrollToBottom()
              }
            } catch (e) {
              // console.log(e)
            }
          }
        })
        await processStream();
      }

      await processStream();
    },
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

.bigBox {
  font-family: 'Noto Sans SC', sans-serif;
}

@keyframes wave {
  0% {
    background-position: 0% 50%;
    transform: rotate(0deg);
  }
  25% {
    background-position: 50% 100%;
    transform: rotate(180deg);
  }
  50% {
    background-position: 100% 50%;
    transform: rotate(360deg);
  }
  75% {
    background-position: 50% 0%;
    transform: rotate(540deg);
  }
  100% {
    background-position: 0% 50%;
    transform: rotate(720deg);
  }
}


.floating-ball {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ff0000, #ff7300, #ffeb00, #47ff00, #00ffee, #2b65ff, #8000ff);
  background-size: 400% 400%;
  position: fixed;
  bottom: 50px;
  right: 25px;
  z-index: 9999;
  cursor: pointer;
  transition: width 0.3s ease-in-out, height 0.3s ease-in-out, bottom 0.3s ease-in-out, right 0.3s ease-in-out;
}

.floating-ball:hover {
  animation: wave 1s infinite linear;
  //transform: scale(1.2);
  width: 60px;
  height: 60px;
  bottom: 45px;
  right: 20px;
}

::v-deep {
  .searchInput {
    .el-input {
      height: 100%;
    }

    .el-input__inner {
      height: 100%;
      border: none;
      font-size: 16px;
    }
  }
}

//设置加载动画
::v-deep {
  .el-loading-spinner .path {
    stroke: #000000 !important;
  }
}

.talk {
  display: flex;
  flex-direction: column; /* 让子元素上下排列 */
  gap: 16px; /* 上下间距 */
  align-items: flex-start;
}

.talk {
  margin-bottom: 16px;
}

.talk > div > div {
  background: #FBE2CC;
  color: #333333;
  padding: 10px;
  border-radius: 10px;
  min-height: 20px;
  display: inline-block; /* 让宽度根据内容自适应 */
  max-width: 80%; /* 防止超宽，可以根据需要调整 */
  line-height: 1.5;
  word-wrap: break-word; /* 让长单词换行 */
}

.myTalk {
  align-self: flex-end; /* 让自己的消息靠右 */
  width: 100%;
  justify-content: right;
}

.aiTalk {
  align-self: flex-start; /* 让 AI 的消息靠左 */
  width: 100%;
}

::v-deep {
  pre {
    white-space: pre-wrap; /* 保持换行 */
    word-break: break-word; /* 长单词换行 */
  }
}

@keyframes dots {
  0% {
    content: ".";
  }
  33% {
    content: "..";
  }
  66% {
    content: "...";
  }
  100% {
    content: "";
  }
}

.ailoading::after {
  content: "";
  animation: dots 1.5s infinite steps(1);
}
</style>
