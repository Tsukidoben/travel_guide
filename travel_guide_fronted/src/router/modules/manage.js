const manageRoutes = [
    {
        path: "/manage",
        name: 'manageIndex',
        component: () => import('@/views/manage/Index.vue'),
        redirect: '/manage/home',
        meta: {
            title: '首页'
        },
        children:[
            {
                path: "home",
                name: 'manageHome',
                component: () => import('@/views/manage/components/Home.vue'),
                meta: {
                    title: '首页',
                },
            },
            {
                path: "dataStatisticsOne",
                name: 'dataStatisticsOne',
                component: () => import('@/views/manage/dataStatisticsOne/dataStatisticsOne.vue'),
                meta: {
                    title: '数据统计',
                },
            },
            {
                path: "dataStatisticsTwo",
                name: 'dataStatisticsTwo',
                component: () => import('@/views/manage/dataStatisticsTwo/dataStatisticsTwo.vue'),
                meta: {
                    title: '数据统计',
                },
            },
            {
                path: "userInfos",
                name: 'userInfos',
                component: () => import('@/views/manage/userInfo/userInfo.vue'),
                meta: {
                    title: '个人信息',
                    group: '人员管理'
                },
            },
            {
                path: "userManage",
                name: 'userManage',
                component: () => import('@/views/manage/userManage/UserManageList.vue'),
                meta: {
                    title: '用户管理',
                    group: '用户管理'
                },
            },
            {
                path: "carouselImage",
                name: 'carouselImage',
                component: () => import('@/views/manage/carouselImage/CarouselImageList.vue'),
                meta: {
                    title: '轮播图管理',
                    group: '系统设置'
                },
            },
            {
                path: "attractionType",
                name: 'attractionType',
                component: () => import('@/views/manage/attractionType/AttractionTypeList.vue'),
                meta: {
                    title: '分类管理',
                    group: '业务管理'
                },
            },
            {
                path: "attractionInfo",
                name: 'attractionInfo',
                component: () => import('@/views/manage/attractionInfo/AttractionInfoList.vue'),
                meta: {
                    title: '景点管理',
                    group: '业务管理'
                },
            },
            {
                path: "ticketInfo",
                name: 'ticketInfo',
                component: () => import('@/views/manage/ticketInfo/TicketInfoList.vue'),
                meta: {
                    title: '门票管理',
                    group: '业务管理'
                },
            },
            {
                path: "attractionOrder",
                name: 'attractionOrder',
                component: () => import('@/views/manage/attractionOrder/AttractionOrderList.vue'),
                meta: {
                    title: '景点订单',
                    group: '业务管理'
                },
            },
            {
                path: "tripStrategy",
                name: 'tripStrategy',
                component: () => import('@/views/manage/tripStrategy/TripStrategyList.vue'),
                meta: {
                    title: '攻略管理',
                    group: '业务管理'
                },
            },
            {
                path: "foodCategory",
                name: 'foodCategory',
                component: () => import('@/views/manage/foodInfo/FoodCategoryList.vue'),
                meta: {
                    title: '小吃分类管理',
                    group: '业务管理'
                },
            },
            {
                path: "foodInfo",
                name: 'foodInfo',
                component: () => import('@/views/manage/foodInfo/FoodInfoList.vue'),
                meta: {
                    title: '小吃信息管理',
                    group: '业务管理'
                },
            },
            {
                path: "foodShop",
                name: 'foodShop',
                component: () => import('@/views/manage/foodInfo/FoodShopList.vue'),
                meta: {
                    title: '小吃店铺管理',
                    group: '业务管理'
                },
            },
            {
                path: "foodComment",
                name: 'foodComment',
                component: () => import('@/views/manage/foodInfo/FoodCommentList.vue'),
                meta: {
                    title: '小吃评论管理',
                    group: '业务管理'
                },
            },
            {
                path: "hotelInfo",
                name: 'hotelInfo',
                component: () => import('@/views/manage/hotelInfo/HotelInfoList.vue'),
                meta: {
                    title: '酒店管理',
                    group: '业务管理'
                },
            },
            {
                path: "hotelRoom",
                name: 'hotelRoom',
                component: () => import('@/views/manage/hotelRoom/HotelRoomList.vue'),
                meta: {
                    title: '房间管理',
                    group: '业务管理'
                },
            },
            {
                path: "hotelOrder",
                name: 'hotelOrder',
                component: () => import('@/views/manage/hotelOrder/HotelOrderList.vue'),
                meta: {
                    title: '酒店订单',
                    group: '业务管理'
                },
            },
            {
                path: "manageChatRoom",
                name: 'manageChatRoom',
                component: () => import('@/views/manage/manageChatRoom/manageChatRoom.vue'),
                meta: {
                    title: '聊天室'
                },
            }
        ]
    }


]

export default manageRoutes;
