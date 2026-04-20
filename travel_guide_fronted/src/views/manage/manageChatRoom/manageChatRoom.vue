<template>
  <div style="width: 100%;margin: 0 auto;height: 100%">
    <Chat
        :roomId="roomId"
        :orderId.sync="orderId"
    />
  </div>
</template>

<script>
import common from "@/utils/common";
import Chat from "@/components/chatRoom.vue";
export default {
  name: "manageChatRoom",
  components: {Chat},
  mixins: [common],
  data() {
    return {
      roomId:'',
      orderId:'',
      urlQuery:{}
    }
  },
  mounted() {
    const params = new URLSearchParams(window.location.search)
    this.urlQuery = Object.fromEntries(params.entries())
    this.roomId = this.urlQuery.roomId
    this.orderId = this.urlQuery.orderId
  },
  methods: {

  }
}
</script>

<style scoped lang="scss">
</style>
