<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="(item,index) in list" :key="index" v-show="index < 3 || moreFlag">
        <div class="own-list-search-item">
          <span class="label">{{ item.name }}：</span>
          <div class="input" v-if="item.type == 'select'">
            <el-select filterable style="width: 100%;" clearable v-model="localSearchForm[item.value]" size="small" :placeholder="item.placeholder" @input="updateSearchForm">
              <el-option v-for="items in item.data" :key="items[item.key]" :label="items[item.label]" :value="items[item.key]"></el-option>
            </el-select>
          </div>
          <div class="input" v-else-if="item.type == 'date'">
            <el-date-picker style="width: 100%;" v-model="localSearchForm[item.value]" type="date" value-format="yyyy-MM-dd" :placeholder="item.placeholder" @input="updateSearchForm"></el-date-picker>
          </div>
          <div class="input" v-else-if="item.type == 'datetime'">
            <el-date-picker style="width: 100%;" v-model="localSearchForm[item.value]" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" :placeholder="item.placeholder" @input="updateSearchForm"></el-date-picker>
          </div>
          <div class="input" v-else>
            <el-input size="small" clearable v-model="localSearchForm[item.value]" :placeholder="item.placeholder" @input="updateSearchForm"></el-input>
          </div>
        </div>
      </el-col>
      <el-col :span="offsetForButton">
        <div style="min-height: 1px;">&nbsp;</div>
      </el-col>
      <el-col :span="6">
        <el-button type="text" @click="moreFlag = !moreFlag" v-if="list.length>3">
          {{ moreFlag ? '收起' : '更多' }}
          <i :class="moreFlag ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </el-button>
        <el-button plain type="primary" size="small" @click="searchData">查询</el-button>
        <el-button plain type="warning" size="small" @click="clearSearch">重置</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import TableUtils from "@/utils/tableUtils";
  import request from "@/utils/request";
  import config from "@/config/config";

  export default {
    name: "searchTop",
    mixins: [TableUtils],
    data() {
      return {
        moreFlag: false,
        localSearchForm: {},
      }
    },
    computed: {
      offsetForButton() {
        if (this.moreFlag) {
          const itemsPerRow = 4;
          const visibleCount = this.list.filter((_, i) => i < 3 || this.moreFlag).length;
          const remain = visibleCount % itemsPerRow;
          console.log(remain);
          if (remain == 0) {
            return 18
          } else if (remain == 1) {
            return 12
          } else if (remain == 2) {
            return 6
          } else if (remain == 3) {
            return 0
          } else {
            return 0
          }
        } else {
          return 0
        }
      }
    },
    props: {
      list: {
        type: Array,
        default: () => [],
      },
      searchForm: {
        type: Object,
        default: () => {
        },
      }
    },
    watch: {
      searchForm: {
        handler(newVal) {
          this.localSearchForm = {...newVal};
        },
        deep: true,
      },
    },
    mounted() {
      if (this.list.length > 0) {
        this.list.forEach(item => {
          if (item.type == 'select') {
            if (item.isApiData) {
              // 如果携带url，则需要查询
              request({
                url: config.backHost + item.apiConfig.url,
                data: item.apiConfig.data ? item.apiConfig.data : {},
              }).then(res => {
                this.$set(item, 'data', res.data);
              })
            }
          }
          if (!item.key) {
            this.$set(item, 'key', "value");
          }
          if (!item.label) {
            this.$set(item, 'label', "label");
          }
        })
      }
    },
    methods: {
      updateSearchForm() {
        this.$emit('update:searchForm', this.localSearchForm);
      },
      searchData() {
        this.$emit('searchData')
      },
      clearSearch() {
        this.localSearchForm = {}
        this.$emit('update:searchForm', this.localSearchForm);
        this.$emit('clearSearch')
      },
    }
  }
</script>

<style scoped lang="scss">
@use "@/assets/css/table.scss";
::v-deep{
  .el-input__icon{
    line-height:50px
  }
}
</style>
