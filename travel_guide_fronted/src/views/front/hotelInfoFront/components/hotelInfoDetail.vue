<template>
  <div class="hotel-detail">
    <div class="container">
      <el-page-header style="padding: 0 12.5px;margin-bottom: 20px" @back="$router.go(-1)" content="酒店详情" class="custom-header"></el-page-header>

      <div class="booking-sidebar shadow-card">
        <div class="sticky-box">
          <div class="price-header-v2">
            <span class="price-label">单价</span>
            <div class="price-amount">
              <span class="val">￥{{ currentPrice }}</span>
              <span class="unit">每晚</span>
            </div>
          </div>

          <div class="booking-form-v2">
            <div class="form-label-mini">选择日期</div>
            <el-date-picker
                v-model="bookingDates"
                type="daterange"
                range-separator="-"
                start-placeholder="入住"
                end-placeholder="离店"
                value-format="yyyy-MM-dd"
                class="custom-date-box"
                :clearable="false"
                :picker-options="pickerOptions"
            ></el-date-picker>

            <div class="form-label-mini" style="margin-top:15px">预订数量</div>
            <el-popover
                placement="bottom"
                width="320"
                trigger="click"
                popper-class="visitor-picker-popover"
            >
              <div class="visitor-picker-panel">
                <div class="visitor-row">
                  <div class="v-info">
                    <div class="v-name">房间数</div>
                  </div>
                  <div class="v-control">
                    <el-button icon="el-icon-minus" circle size="mini" :disabled="buyCount <= 1" @click="buyCount--"></el-button>
                    <span class="v-num">{{ buyCount }}</span>
                    <el-button icon="el-icon-plus" circle size="mini" @click="buyCount++"></el-button>
                  </div>
                </div>
              </div>

              <div slot="reference" class="custom-select-box">
                <i class="el-icon-house"></i>
                <span class="display-text">数量 x {{ buyCount }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
            </el-popover>
            <div class="form-label-mini" style="margin-top:15px">预订数量</div>
            <el-button
                icon="el-icon-headset"
                class="contact-service-btn"
                @click="contactService"
            >联系酒店客服</el-button>
            <div class="total-summary" v-if="selectedRoomId">
              <div class="summary-line"><span>天数:</span> <span>{{ stayDays }} 晚</span></div>
            </div>
          </div>
        </div>
      </div>

      <el-row :gutter="25" style="margin: 0">
        <el-col :span="17">
          <div class="main-content shadow-card">
            <div v-if="detail.htoelPic" class="card-image" :class="'pic-count-' + JSON.parse(detail.htoelPic).length">
              <img v-if="JSON.parse(detail.htoelPic).length == 1" :src="getPicUrlByJson(detail.htoelPic, 0)">

              <template v-else-if="JSON.parse(detail.htoelPic).length == 2">
                <div style="width: 50%;overflow: hidden"><img :src="getPicUrlByJson(detail.htoelPic,0)" class="pic-main"></div>
                <div style="width: 50%;overflow: hidden"><img :src="getPicUrlByJson(detail.htoelPic,1)" class="pic-sub"></div>
              </template>

              <template v-else-if="JSON.parse(detail.htoelPic).length >= 3">
                <div style="width: 66%;overflow: hidden"><img :src="getPicUrlByJson(detail.htoelPic,0)" class="pic-main"></div>
                <div class="side-pics">
                  <div style="width:100%;height: 50%;overflow: hidden"><img :src="getPicUrlByJson(detail.htoelPic,1)"></div>
                  <div style="width: 100%;height: 50%;overflow: hidden"><img :src="getPicUrlByJson(detail.htoelPic,2)"></div>
                </div>
              </template>
            </div>

            <div class="article-body">
              <h1 class="attr-title">{{ detail.htoelName }}</h1>
              <div class="meta-info">
                <span v-if="detail.address"><i class="el-icon-location-outline"></i> {{ detail.address }}</span>
              </div>
              <div>
                <div class="section-label">酒店介绍</div>
                <div style="margin-bottom: 20px">
                  {{ detail.hotelDesc }}
                </div>
              </div>
              <div>
                <div class="section-label">酒店服务</div>
                <div class="service-tags-container">
                  <template v-if="detail.hotelService">
                    <el-tag
                        v-for="(service, index) in detail.hotelService.split(',')"
                        :key="index"
                        class="service-tag"
                        effect="plain"
                        size="medium"
                    >
                      {{ service.trim() }}
                    </el-tag>
                  </template>
                  <span v-else style="color: #999; font-size: 14px;">暂无服务设施信息</span>
                </div>
              </div>

              <div class="ticket-section">
                <div class="section-label">房型预订</div>
                <div v-if="roomList.length > 0">
                  <div v-for="room in roomList"
                       :key="room.id"
                       class="ticket-card"
                       @click="selRoom(room)"
                       :class="{ 'active': selectedRoomId === room.id }">
                    <div class="ticket-info">
                      <div class="t-name">{{ room.roomName }}</div>

                      <div class="room-tags">
                        <span class="r-tag"><i class="el-icon-office-building"></i> {{ room.roomFloor }}层</span>
                        <span class="r-tag"><i class="el-icon-user"></i> 可住{{ room.peopleCount }}人</span>
                      </div>

                      <div class="room-intro-box">
                        {{ room.roomDesc }}
                      </div>
                    </div>

                    <div class="ticket-price-action">
                      <div class="price-val">
                        <div style="font-size: 24px;font-weight: bold">
                          <span class="unit">￥</span>{{ room.price * stayDays * buyCount }}
                        </div>
                        <div style="font-size: 14px;color: #63687a">{{buyCount}}个 x {{stayDays}}晚 x ￥{{room.price}}</div>
                      </div>
                      <div class="btns">
                        <el-button type="primary" plain round size="medium" @click.stop="handleOrder(room)">立即预订</el-button>
                      </div>
                    </div>
                  </div>
                </div>
                <el-empty v-else description="暂无房型信息" :image-size="60"></el-empty>
              </div>

              <div class="detail-html-content">
                <div class="section-label">酒店详情</div>
                <div class="rich-text-content" v-html="detail.hotelDetail"></div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import config from "@/config/config";
import common from "@/utils/common";

export default {
  mixins: [common],
  data() {
    return {
      detail: {},
      roomList: [],
      selectedRoomId: null,
      currentPrice: 0,
      buyCount: 1,
      bookingDates: [],
      pickerOptions: {
        disabledDate(time) { return time.getTime() < Date.now() - 8.64e7; }
      }
    };
  },
  computed: {
    stayDays() {
      if (this.bookingDates && this.bookingDates.length === 2) {
        const start = new Date(this.bookingDates[0]);
        const end = new Date(this.bookingDates[1]);
        return Math.max(1, Math.floor((end - start) / (1000 * 60 * 60 * 24)));
      }
      return 1;
    },
    totalPrice() {
      return this.currentPrice * this.buyCount * this.stayDays;
    }
  },
  mounted() {
    this.getDetail();
    this.initDefaultDates();
  },
  methods: {
    contactService(){
      request({
        url: config.backHost + "/chatRoom/getRoomByHotelId/"+this.$route.query.id,
      }).then((res)=>{
        this.$router.push({name:'frontChatRoom',query:{roomId:res.data}})
      })
    },
    async getDetail() {
      const id = this.$route.query.id;
      const res = await request({ url: config.backHost + `/hotelInfo/getById/${id}` });
      if (res.code === 200) {
        this.detail = res.data;
        this.getRooms(id);
      }
    },
    initDefaultDates() {
      const start = new Date();
      const end = new Date();
      end.setDate(start.getDate() + 1);
      const formatDate = (date) => {
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, '0');
        const d = String(date.getDate()).padStart(2, '0');
        return `${y}-${m}-${d}`;
      };
      this.bookingDates = [formatDate(start), formatDate(end)];
    },
    async getRooms(id) {
      const res = await request({
        url: config.backHost + "/hotelRoom/listPage",
        method: "POST",
        data: {
          params: { hotelId: id },
          pageBean: { page: 1, pageSize: -1 }
        }
      });
      if (res.code === 200) {
        this.roomList = res.data;
        if (this.roomList.length > 0) this.selRoom(this.roomList[0]);
      }
    },
    selRoom(room) {
      this.selectedRoomId = room.id;
      this.currentPrice = room.price;
    },
    handleOrder(room) {
      if (!this.bookingDates || this.bookingDates.length < 2) {
        this.$message.warning("请先选择入住日期");
        return;
      }
      const targetRoom = room || this.roomList.find(r => r.id === this.selectedRoomId);
      this.$confirm('是否确认预订?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: config.backHost + '/hotelOrder/submit',
          data: {
            "hotelId": this.$route.query.id,
            "roomId": targetRoom.id,
            "buyCount": this.buyCount,
            "totalPrice": this.totalPrice,
            "startDate": this.bookingDates[0],
            "endDate": this.bookingDates[1],
            "days": this.stayDays
          }
        }).then(res => {
          if (res.code === 200) {
            document.querySelector("body").innerHTML = res.data;
            document.forms[0].submit();
          }
        });
      })
    }
  }
};
</script>

<style scoped lang="scss">
$primary-blue: $theme-color;
$bg-color: #f5f7fa;
$dark-text: #1a2b49;
.contact-service-btn {
  width: 100%;
  margin-top: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  height: 45px;
  font-weight: 600;
  color: #606266;
  transition: all 0.3s;

  &:hover {
    color: $primary-blue;
    border-color: $primary-blue;
    background-color: rgba($primary-blue, 0.05);
  }
}
.hotel-detail { background: $bg-color; min-height: 100%; padding-bottom: 50px; }
.container { max-width: 1220px; margin: 0 auto; padding: 20px; position: relative; }
.shadow-card { background: #fff; border-radius: 12px; box-shadow: 0 2px 15px rgba(0,0,0,0.06); margin-bottom: 15px; overflow: hidden; }

.card-image {
  position: relative; max-height: 400px; overflow: hidden; display: flex; gap: 4px;
  img { width: 100%; height: 100%; object-fit: cover; transition: transform .5s ease; max-height: 400px; }
  &.pic-count-3 {
    .pic-main { width: 100%; }
    .side-pics { width: 34%; display: flex; flex-direction: column; gap: 4px; img { width: 100%; } }
  }
  &:hover img { transform: scale(1.05); }
}

.article-body {
  padding: 30px 35px;
  .attr-title { font-size: 28px; font-weight: bold; margin-bottom: 15px; color: $dark-text; }
  .meta-info { color: #555; font-size: 14px; margin-bottom: 30px; span { margin-right: 20px; i { color: $primary-blue; margin-right: 5px; } } }
}

.section-label {
  font-size: 20px; font-weight: bold; margin: 0 0 20px; display: flex; align-items: center;
  &::before { content: ""; width: 4px; height: 18px; background: $primary-blue; margin-right: 10px; border-radius: 2px; }
}

.ticket-card {
  cursor: pointer; border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 15px; overflow: hidden; transition: 0.3s;
  &:hover, &.active { border-color: $primary-blue; box-shadow: 0 4px 12px rgba(255,138,69,0.1); }
  .ticket-info { padding: 20px; .t-name { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 8px; } .t-desc { font-size: 14px; color: #666; } }
  .ticket-price-action { background: #EBEEF1; padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; .price-val { font-size: 24px; font-weight: bold; color: $primary-blue; .unit { font-size: 14px; } small { font-size: 12px; color: #999; } } .btns { display: flex; gap: 10px; } }
}

.booking-sidebar {
  padding: 24px;
  position: fixed;
  top: 152px;
  right: 0;
  z-index: 11;
  width: calc(1220px * (7/24) - 20px - 8.5px);
  left: calc(1220px * (17/24) + ((100vw - 1220px)/2) + 12.5px);
  box-sizing: border-box;

  .price-header-v2 {
    margin-bottom: 20px;
    .price-label { color: #666; font-size: 14px; }
    .price-amount { margin-top: 4px; .val { font-size: 26px; font-weight: 800; color: $dark-text; } .unit { font-size: 14px; margin-left: 5px; } }
  }

  .form-label-mini { font-size: 12px; color: #999; margin-bottom: 6px; }
  .custom-date-box { width: 100% !important; border-radius: 12px; border: none; background: #edf2f7; margin-bottom: 10px; }

  .custom-select-box {
    background: #edf2f7; border-radius: 12px; height: 48px; display: flex; align-items: center; padding: 0 15px; cursor: pointer;
    .display-text { flex: 1; margin: 0 12px; font-weight: 600; font-size: 14px; color: $dark-text; }
  }
}

.total-summary {
  margin-top: 20px; padding-top: 15px; border-top: 1px dashed #ddd;
  .summary-line { display: flex; justify-content: space-between; font-size: 14px; color: #666; margin-bottom: 5px; }
  .total { font-weight: bold; color: $primary-blue; font-size: 16px; margin-top: 8px; }
}

.visitor-picker-panel {
  padding: 10px;
  .visitor-row { display: flex; justify-content: space-between; align-items: center; .v-name { font-weight: bold; } .v-control { display: flex; align-items: center; gap: 10px; } }
}
::v-deep{
  .el-range-input{
    background: transparent;
  }
}
::v-deep{
  .el-button--primary.is-plain{
    color: $theme-color;
    border-color: $theme-color;
    background: transparent;
  }
  .el-button--primary.is-plain:hover{
    background: $side-bar-container-hoverBackColor;
  }
}
.hotel-desc-quote {
  background: #f8faff;
  padding: 12px 18px;
  border-radius: 8px;
  color: #5a6d91;
  font-size: 15px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  i { color: $primary-blue; font-size: 20px; }
}

.service-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  //.s-label { font-size: 16px; color: #000;white-space: nowrap; }
  //.s-list {
  //  line-height: 24px;
  //}
}

.room-tags {
  display: flex;
  gap: 8px;
  margin: 10px 0;
  .r-tag {
    background: #f0f2f5;
    color: #666;
    padding: 3px 10px;
    border-radius: 4px;
    font-size: 12px;
    display: flex;
    align-items: center;
    i { margin-right: 4px; font-size: 13px; }
  }
}

.room-intro-box {
  color: #888;
  font-size: 13px;
  line-height: 1.6;
  margin-top: 10px;
  background: #fafafa;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px dashed #eee;
}

.rich-text-content {
  line-height: 1.8;
  color: #333;
  font-size: 15px;
  ::v-deep img {
    max-width: 100%;
    border-radius: 12px;
    margin: 10px 0;
  }
}
.service-tags-container {
  margin-bottom: 30px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  .service-tag {
    border-radius: 6px;
    font-weight: 500;
    color: $primary-blue;
    border-color: rgba($primary-blue, 0.3);
    background-color: rgba($primary-blue, 0.05);
    padding: 0 15px;
    height: 32px;
    line-height: 30px;
    transition: all 0.3s;

    &:hover {
      background-color: $primary-blue;
      color: #fff;
      transform: translateY(-2px);
    }
  }
}
@media (max-width: 1466px) { .booking-sidebar { top: 212px; } }
@media (max-width: 1260px) { .booking-sidebar { width: calc(100vw * (7/24) - 20px - 22.5px); left: calc(100vw * (17/24)); } }
</style>
