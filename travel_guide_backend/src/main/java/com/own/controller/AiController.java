package com.own.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.y8e.common.utils.ResultUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.own.common.utils.CommonUtil;
import com.own.common.utils.ContextUtil;
import com.own.model.*;
import com.own.model.vo.AgentPlan;
import com.own.model.vo.AgentStep;
import com.own.model.vo.StreamMsg;
import com.own.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {

    private final OpenAiChatModel chatModel;

    @Autowired
    private HuAiResultService baseService;

    @Value("${deepseek.historyCount}")
    private Integer historyCount;

    @Autowired
    public AiController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }


//    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<?> ai(@RequestBody Map<String, String> param) {
//        String input = param.get("input");
//
//        List<Message> messages = new ArrayList<>();
//
//        messages.add(new SystemMessage(
//                "你是一个专业旅游助手。你只能回答与旅游相关的问题，包括景点门票、酒店住宿、交通出行、旅游攻略、行程规划等内容。如果用户问到非旅游相关的问题，礼貌地提醒用户：“抱歉，我只能回答旅游相关的问题，请问你有什么旅游方面的问题么？”。每次回复限制 200 字以内。"
//        ));
//
//        if (historyCount != -1) {
//            // 为了多轮对话
//            List<HuAiResult> history = this.baseService.getAiList(historyCount);
//            history.forEach(h -> {
//                messages.add(new UserMessage(h.getUserQues()));
//                messages.add(new AssistantMessage(h.getAiResult()));
//            });
//        }
//
//        // 添加本次信息
//        messages.add(new UserMessage(input));
//
//        Prompt prompt = new Prompt(messages);
//
//        StringBuilder result = new StringBuilder();
//
//        Date now = new Date();
//
//        String currentUserId = ContextUtil.getCurrentUserId();
//
//        Flux<StreamMsg> statusFlux = Flux.just(
//                new StreamMsg("status", "意图分析中..."),
//                new StreamMsg("status", "结果返回中...")
//        );
//
//        Flux<StreamMsg> aiFlux = this.chatModel.stream(prompt)
//                .flatMap(chatResponse ->
//                        Flux.fromIterable(chatResponse.getResults())
//                                .map(res -> Optional.ofNullable(res)
//                                        .map(Generation::getOutput)
//                                        .map(AbstractMessage::getText)
//                                        .orElse(StrUtil.EMPTY))
//                                .filter(StrUtil::isNotEmpty)
//                                .map(text -> {
//                                    result.append(text);
//                                    return new StreamMsg("ai", text);
//                                })
//                )
//                .doOnComplete(() ->
//                        Mono.fromRunnable(() ->
//                                this.baseService.save(new HuAiResult()
//                                        .setAiResult(result.toString())
//                                        .setUserId(currentUserId)
//                                        .setUserQues(input)
//                                        .setCreateTime(now)
//                                )
//                        ).subscribeOn(Schedulers.boundedElastic()).subscribe()
//                );
//
//        return Flux.concat(statusFlux, aiFlux);
//    }

    private final Map<String, Function<Map<String, Object>, String>> toolRegistry = Map.of(
            "queryScenicTickets", p -> queryScenicTickets((String) p.get("input")),
            "queryHotelInfo", p -> queryHotelInfo((String) p.get("input")),
            "queryRoomInfo", p -> queryRoomInfo((String) p.get("input")),
            "queryTravelGuide", p -> queryTravelGuide((String) p.get("input"))
    );

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StreamMsg> chat(@RequestBody Map<String, String> param) {

        String input = param.get("input");
        String userId = ContextUtil.getCurrentUserId();
        Date now = new Date();

        StringBuilder finalResult = new StringBuilder();

        Flux<StreamMsg> statusFlux = Flux.just(
                new StreamMsg("status", "分析需求中..."),
                new StreamMsg("status", "生成执行计划中...")
        );

        Flux<StreamMsg> agentFlux =
                this.run(input)
                        .doOnNext(msg -> {
                            if ("ai".equals(msg.getType())) {
                                finalResult.append(msg.getContent());
                            }
                        });

        return Flux.concat(statusFlux, agentFlux)
                .doOnComplete(() ->
                        this.saveResult(userId, input, finalResult.toString(), now)
                );
    }

    public Mono<AgentPlan> plan(String input) {
        List<Message> messages = List.of(
                new SystemMessage("""
                        你是一个专业旅游Agent的任务规划模块。
                        请根据用户问题输出JSON任务计划。
                        
                        规则：
                        1. intent 只能是：景点 / 酒店 / 房间 / 攻略 / 其他
                        2. tool 只能是：
                           queryScenicTickets / queryHotelInfo / queryRoomInfo / queryTravelGuide
                        3. 只输出JSON，不要解释
                        4. targetTask 20字以内的计划
                        
                        JSON格式：
                        {
                          "targetTask": "",
                          "steps": [
                            { "tool": "", "params": {} , "intent": "" }
                          ]
                        }
                        """),
                new UserMessage(input)
        );

        Prompt prompt = new Prompt(messages);

        return Mono.fromSupplier(() -> {
            String json = chatModel.call(prompt)
                    .getResults().get(0)
                    .getOutput().getText();
            return JSONUtil.toBean(json, AgentPlan.class);
        });
    }

    // ================= Executor + Observer =================
    public Mono<List<String>> execute(AgentPlan plan, String input) {
        return Mono.fromSupplier(() -> {
            List<String> observations = new ArrayList<>();
            for (AgentStep step : plan.getSteps()) {
                Function<Map<String, Object>, String> tool = toolRegistry.get(step.getTool());
                if (tool != null) {
                    String result = tool.apply(Map.of("input", input));
                    if (StrUtil.isNotEmpty(result)) {
                        observations.add(result);
                    }
                }
            }
            if (observations.isEmpty()) {
                observations.add("未查询到相关旅游数据");
            }
            return observations;
        });
    }

    // ================= Responder =================
    public Flux<StreamMsg> respond(String input, List<String> observations) {

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("""
                你是专业旅游助手。
                请基于以下信息回答用户问题：
                - 不要出现数据库、系统等字样
                - 不编造
                - 不超过200字
                """));
        messages.add(new SystemMessage(String.join("\n", observations)));
        messages.add(new UserMessage(input));

        Prompt prompt = new Prompt(messages);

        return chatModel.stream(prompt)
                .flatMap(r -> Flux.fromIterable(r.getResults())
                        .map(res -> Optional.ofNullable(res.getOutput())
                                .map(AbstractMessage::getText)
                                .orElse(StrUtil.EMPTY))
                        .filter(StrUtil::isNotEmpty)
                        .map(text -> new StreamMsg("ai", text))
                );
    }

    // ================= Agent 核心方法 =================
    public Flux<StreamMsg> run(String input) {
        return plan(input)
                .flatMapMany(plan -> {

                    Flux<StreamMsg> statusFlux = Flux.just(
                            new StreamMsg("status", "识别意图：" + plan.getTargetTask()),
                            new StreamMsg("status", "调用工具中...")
                    );

                    Mono<List<String>> obsMono = execute(plan, input);

                    Flux<StreamMsg> answerFlux = obsMono.flatMapMany(obs -> respond(input, obs));

                    return Flux.concat(statusFlux, answerFlux);
                });
    }

    // ================= 保存结果 =================
    public void saveResult(String userId, String question, String answer, Date createTime) {
        baseService.save(new HuAiResult()
                .setUserId(userId)
                .setUserQues(question)
                .setAiResult(answer)
                .setCreateTime(createTime));
    }


    // ==== 数据库查询工具示例 ====
    private String queryScenicTickets(String input) {
        List<AttractionInfo> list = SpringUtil.getBean(AttractionInfoService.class)
                .list();
        if (CollUtil.isEmpty(list)) return "无景点信息";

        List<String> hotelIds = list.stream().map(AttractionInfo::getId).toList();
        Map<String, List<TicketInfo>> collect = SpringUtil.getBean(TicketInfoService.class)
                .lambdaQuery()
                .in(TicketInfo::getAttractionId, hotelIds)
                .list().stream().collect(Collectors.groupingBy(TicketInfo::getAttractionId));
        list.forEach(hotelInfo -> {
            hotelInfo.setTicketInfoList(collect.get(hotelInfo.getId()));
        });
        return CommonUtil.toJson(list);
    }

    private String queryHotelInfo(String input) {
        List<HotelInfo> list = SpringUtil.getBean(HotelInfoService.class)
                .list();
        if (CollUtil.isEmpty(list)) return "无酒店信息";

        List<String> hotelIds = list.stream().map(HotelInfo::getId).toList();
        Map<String, List<HotelRoom>> collect = SpringUtil.getBean(HotelRoomService.class)
                .lambdaQuery()
                .in(HotelRoom::getHotelId, hotelIds)
                .list().stream().collect(Collectors.groupingBy(HotelRoom::getHotelId));
        list.forEach(hotelInfo -> {
            hotelInfo.setRoomList(collect.get(hotelInfo.getId()));
        });
        return CommonUtil.toJson(list);
    }

    private String queryRoomInfo(String input) {
        List<HotelInfo> list = SpringUtil.getBean(HotelInfoService.class)
                .list();
        if (CollUtil.isEmpty(list)) return "无酒店信息";

        List<String> hotelIds = list.stream().map(HotelInfo::getId).toList();
        Map<String, List<HotelRoom>> collect = SpringUtil.getBean(HotelRoomService.class)
                .lambdaQuery()
                .in(HotelRoom::getHotelId, hotelIds)
                .list().stream().collect(Collectors.groupingBy(HotelRoom::getHotelId));
        list.forEach(hotelInfo -> {
            hotelInfo.setRoomList(collect.get(hotelInfo.getId()));
        });
        return CommonUtil.toJson(list);
    }

    private String queryTravelGuide(String input) {
        List<TripStrategy> list = SpringUtil.getBean(TripStrategyService.class).list();
        return CommonUtil.toJson(list);
    }

    @PostMapping("/getAiList")
    public String getAiList(@RequestParam(value = "limit", required = false) Integer limit) {
        return ResultUtil.<List<HuAiResult>>successWithData(baseService.getAiList(limit));
    }


    @PostMapping("/deleteAiResult")
    public String deleteAiResult() {
        this.baseService
                .lambdaUpdate()
                .eq(HuAiResult::getUserId, ContextUtil.getCurrentUserId())
                .remove();
        return ResultUtil.success();
    }

}

