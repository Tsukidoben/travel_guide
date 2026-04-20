import request from "@/utils/request";
import config from "@/config/config";
import Common from "@/utils/common";
import $ from 'jquery';

export default {
    mixins:[Common],
    data() {
        return {
            // 列表字段
            pageBean: {
                page: 1,
                pageSize: 20,
                total: 0,
            },
            tableData: [],
            searchForm: {},
            // 表单相关字段
            formDialog: false,
            formAction: "view",
            formTitle: "表单详情",
        }
    },
    methods: {
        openForm(action,title,data){
            this.formDialog = true;
            this.formAction= action;
            this.formTitle = title;
            if(data){
                this.$nextTick(()=>{
                    this.$refs.form.formData = $.extend({},data)
                })
            }
            this.$nextTick(()=>{
                this.$refs.form.initForm();
            })
        },
        delBatch(list, delUrl) {
            if (list && list.length > 0) {
                let ids = [];
                for (let i = 0; i < list.length; i++) {
                    ids.push(list[i].id)
                }
                this.$confirm('确认删除？删除后不可回退', '温馨提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    request({
                        url: config.backHost + delUrl,
                        data: {ids: ids}
                    }).then(res => {
                        if (res.code == 200) {
                            this.$message({
                                type: 'success',
                                message: '删除成功!'
                            });
                            this.query(this.searchForm, this.pageBean);
                        } else {
                            this.$message.error(res.msg);
                        }
                    })
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            } else {
                this.$message.warning("请选择数据")
            }
        },
        delById(row, delUrl) {
            this.$confirm('确认删除？删除后不可回退', '温馨提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                request({
                    url: config.backHost + delUrl + row.id
                }).then(res => {
                    if (res.code == 200) {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.query(this.searchForm, this.pageBean);
                    } else {
                        this.$message.error(res.msg);
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        searchData() {
            this.pageBean.page = 1;
            this.query(this.searchForm, this.pageBean);
        },
        clearSearch() {
            for (let key in this.searchForm) {
                this.$set(this.searchForm, key, '');
            }
            this.query(this.searchForm, this.pageBean);
        },
        currPageChange(page) {
            this.pageBean.page = page;
            this.query(this.searchForm, this.pageBean);
        },
        handleSizeChange(size) {
            this.pageBean.pageSize = size;
            this.query(this.searchForm, this.pageBean);
        },
    }
}
