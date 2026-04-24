import Common from "@/utils/common";
import config from "@/config/config";
import request from "@/utils/request";

export default {
    mixins: [Common],
    props: {
        dialogVisible: {
            type: Boolean,
            default: false,
        },
        title: {
            type: String,
            default: "表单详情",
        },
        // 操作 add 新增  edit 编辑  view 查看
        action: {
            type: String,
            default: "view",
        }
    },
    computed: {
        visible() {
            return this.dialogVisible;
        }
    },
    methods: {
        /**
         * 确认提交
         * @param action 操作
         * @param formData 表单数据
         */
        confirmSubmit(action, formData) {
            if (this.isEmpty(action)) {
                this.$message.warning("操作为空，不允许操作")
                return;
            }
            if (action == 'view') {
                this.$message.warning("当前仅可查看")
                return;
            }

            let url = "";
            if (action === "add") {
                url = this.addUrl;
            } else if (action === "edit") {
                url = this.editUrl;
            } else {
                this.$message.warning("操作未定义");
                return;
            }

            let message = this.beforeSave();
            if(!this.isEmpty(message)){
                this.$notify({
                    title: '温馨提示',
                    message: message,
                    dangerouslyUseHTMLString: true,
                    type:'warning'
                })
                return
            }
            request({
                url: config.backHost + url,
                method: 'POST',
                data: this.formData,
            }).then((res)=>{
                if(res.code == 200){
                    if(action == 'add'){
                        this.$message.success('新建成功')
                    }else {
                        this.$message.success('修改成功')
                    }

                    this.$emit('reload')
                    this.closeDialog();
                }else {
                    this.$message.error(res.msg)
                }
            })
        },
        initForm(){
            
        },
        clearData() {
            for (let key in this.formData) {
                this.$set(this.formData, key, "")
            }
        },
        closeDialog() {
            this.clearData();
            this.$emit('update:dialogVisible', false)
        }
    }
}

