const frontRoutes = [
    {
        path: "/front",
        name: 'frontIndex',
        component: () => import('@/views/front/index.vue'),
        meta: {
            title: '首页'
        },
        redirect: '/front/home',
        children:[
            {
                path: "home",
                name: 'home',
                component: () => import('@/views/front/home/index.vue'),
                meta: {
                    title: '首页'
                },
            },
            {
                path: "attractionList",
                name: 'attractionList',
                component: () => import('@/views/front/attractionList/attractionList.vue'),
                meta: {
                    title: '景点查询'
                },
            },
            {
                path: "attractionDetail",
                name: 'attractionDetail',
                component: () => import('@/views/front/attractionList/components/attractionDetail.vue'),
                meta: {
                    title: '景点详情'
                },
            },
            {
                path: "foodList",
                name: 'foodList',
                component: () => import('@/views/front/foodList/foodList.vue'),
                meta: {
                    title: '特色小吃'
                },
            },
            {
                path: "foodDetail",
                name: 'foodDetail',
                component: () => import('@/views/front/foodList/components/foodDetail.vue'),
                meta: {
                    title: '小吃详情'
                },
            },
            {
                path: "myCollect",
                name: 'myCollect',
                component: () => import('@/views/front/myCollect/myCollect.vue'),
                meta: {
                    title: '我的收藏'
                },
            },
            {
                path: "postReview",
                name: 'postReview',
                component: () => import('@/views/front/postReview/postReview.vue'),
                meta: {
                    title: '发布攻略'
                },
            },
            {
                path: "myStrategyGuide",
                name: 'myStrategyGuide',
                component: () => import('@/views/front/myStrategyGuide/myStrategyGuide.vue'),
                meta: {
                    title: '我的攻略'
                },
            },
            {
                path: "myStrategyDetail",
                name: 'myStrategyDetail',
                component: () => import('@/views/front/myStrategyGuide/components/myStrategyDetail.vue'),
                meta: {
                    title: '我的攻略详情'
                },
            },
            {
                path: "strategyPlaza",
                name: 'strategyPlaza',
                component: () => import('@/views/front/strategyPlaza/strategyPlaza.vue'),
                meta: {
                    title: '攻略专区'
                },
            },
            {
                path: "strategyPlazaDetail",
                name: 'strategyPlazaDetail',
                component: () => import('@/views/front/strategyPlaza/components/strategyPlazaDetail.vue'),
                meta: {
                    title: '攻略详情'
                },
            },
            {
                path: "routeGenerate",
                name: 'routeGenerate',
                component: () => import('@/views/front/routeRecommend/RouteGenerate.vue'),
                meta: {
                    title: '智能行程规划'
                },
            },
            {
                path: "myRoutes",
                name: 'myRoutes',
                component: () => import('@/views/front/routeRecommend/MyRoutes.vue'),
                meta: {
                    title: '我的行程'
                },
            },
            {
                path: "routeDetail/:id",
                name: 'routeDetail',
                component: () => import('@/views/front/routeRecommend/RouteDetail.vue'),
                meta: {
                    title: '行程详情'
                },
            },
            {
                path: "hotelInfoFront",
                name: 'hotelInfoFront',
                component: () => import('@/views/front/hotelInfoFront/hotelInfo.vue'),
                meta: {
                    title: '酒店列表'
                },
            },
            {
                path: "hotelInfoDetail",
                name: 'hotelInfoDetail',
                component: () => import('@/views/front/hotelInfoFront/components/hotelInfoDetail.vue'),
                meta: {
                    title: '酒店详情'
                },
            },
            {
                path: "ticketOrder",
                name: 'ticketOrder',
                component: () => import('@/views/front/ticketOrder/ticketOrder.vue'),
                meta: {
                    title: '门票订单'
                },
            },
            {
                path: "hotelOrderFront",
                name: 'hotelOrderFront',
                component: () => import('@/views/front/hotelOrderFront/hotelOrder.vue'),
                meta: {
                    title: '酒店订单'
                },
            },
            {
                path: "frontChatRoom",
                name: 'frontChatRoom',
                component: () => import('@/views/front/frontChatRoom/frontChatRoom.vue'),
                meta: {
                    title: '聊天室'
                },
            },
            {
                path: "myCenter",
                name: 'myCenter',
                component: () => import('@/views/front/myCenter/myCenter.vue'),
                meta: {
                    title: '个人中心'
                },
                redirect: '/front/myCenter/personalInfo',
                children:[
                    {
                        path: "userInfo",
                        name: 'userInfo',
                        component: () => import('@/views/front/userInfo/userInfo.vue'),
                        meta: {
                            title: '个人信息'
                        },
                    },
                    {
                        path: "changePassWord",
                        name: 'changePassWord',
                        component: () => import('@/views/front/changePassWord/changePassWord.vue'),
                        meta: {
                            title: '修改密码'
                        },
                    },
                ],
            },
        ]
    },
]

export default frontRoutes;
