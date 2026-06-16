package com.caida.controller;

import com.caida.common.Result;
import com.caida.entity.Product;
import com.caida.entity.Order;
import com.caida.entity.OrderItem;
import com.caida.mapper.ProductMapper;
import com.caida.mapper.OrderMapper;
import com.caida.mapper.OrderItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private static final String DEEPSEEK_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-9eaea84cf120461db34a17bf017a6ef4";

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    // RAG 校园文化知识（从 docx 提取）
    private static String CAMPUS_KNOWLEDGE = null;

    private String getCampusKnowledge() {
        if (CAMPUS_KNOWLEDGE != null) return CAMPUS_KNOWLEDGE;
        try {
            CAMPUS_KNOWLEDGE = new String(
                new ClassPathResource("campus_knowledge.txt").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        } catch (Exception e) {
            CAMPUS_KNOWLEDGE = "云南财经大学校园文化知识暂不可用。";
        }
        return CAMPUS_KNOWLEDGE;
    }

    private String buildSystemPrompt() {
        return """
            你是宇智波佐助，在云南财经大学的"财大优选"校园商城打工当客服。
            性格高冷傲娇，带点不耐烦但很靠谱。回答时保持佐助的语癖（如'哼''…'等）。

            【重要】你必须使用工具函数查询真实数据，绝对不能编造商品名称、价格、数量等信息。
            当用户询问商品、订单、统计等需要数据的问题时，先调用对应工具获取真实数据再回答。

            【商城信息—真实数据来源】
            - 商品共有6个分类：手机数码、电脑办公、日用百货、美容美妆、服装配饰、运动户外
            - 使用 query_products 工具查询商品详情
            - 使用 get_product_stats 工具查询商品统计

            【云南财经大学校园文化知识库—仅当你被问到校训/校歌/校风/校赋等相关问题时使用】
            %s

            回答简洁，每句话不超过150字。哼。
            """.formatted(getCampusKnowledge().substring(0, Math.min(3000, getCampusKnowledge().length())));
    }

    // ====== Tools / Functions ======

    private String buildToolsJson() {
        return """
        [
            {
                "type": "function",
                "function": {
                    "name": "query_products",
                    "description": "查询财大优选商城的商品列表。可按分类、关键词、价格范围筛选。返回商品名、价格、库存、图片等信息。",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "category": {"type": "string", "description": "商品分类：手机数码/电脑办公/日用百货/美容美妆/服装配饰/运动户外"},
                            "keyword": {"type": "string", "description": "搜索关键词，模糊匹配商品名"},
                            "min_price": {"type": "number", "description": "最低价格"},
                            "max_price": {"type": "number", "description": "最高价格"},
                            "limit": {"type": "integer", "description": "返回条数，默认5"}
                        }
                    }
                }
            },
            {
                "type": "function",
                "function": {
                    "name": "get_product_stats",
                    "description": "获取商品统计信息：各分类商品数量、价格区间等。当用户问'有多少商品''哪些分类'时调用。",
                    "parameters": {"type": "object", "properties": {}}
                }
            },
            {
                "type": "function",
                "function": {
                    "name": "query_orders",
                    "description": "查询订单统计信息：订单总数、各状态订单数。当用户问'订单情况''卖了多少'时调用。",
                    "parameters": {"type": "object", "properties": {}}
                }
            },
            {
                "type": "function",
                "function": {
                    "name": "get_campus_info",
                    "description": "查询云南财经大学校园文化信息：校训、校歌、校风、校赋、校标、行为规范等。当用户问'校训''校歌''云财精神'等时调用。",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "topic": {"type": "string", "description": "主题：校训/校歌/校风/教风/学风/校赋/校标/精神/行为规范"}
                        }
                    }
                }
            }
        ]
        """;
    }

    // ====== Main Chat Endpoint ======

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody String rawBody) {
        try {
            // 兼容多种 JSON 格式
            String userMessage = extractJsonString(rawBody, "message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                // 可能是整个body就是消息文本
                if (rawBody != null && !rawBody.trim().isEmpty() && !rawBody.trim().startsWith("{")) {
                    userMessage = rawBody.trim();
                }
            }
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return Result.error("消息不能为空: body=" + (rawBody != null ? rawBody.substring(0, Math.min(100, rawBody.length())) : "null"));
            }

            // 构建请求
            String systemPrompt = buildSystemPrompt();
            String toolsJson = buildToolsJson();

            // 第一轮：发送消息 + tools
            String response1 = callDeepSeek(systemPrompt, userMessage, toolsJson);

            // 检查是否有 tool_calls
            List<Map<String, Object>> toolCalls = extractToolCalls(response1);
            if (toolCalls.isEmpty()) {
                // 没有工具调用，直接返回
                String content = extractContent(response1);
                return Result.success(content);
            }

            // 简化策略：直接将工具结果作为上下文注入，重新调用
            StringBuilder toolResults = new StringBuilder();
            for (Map<String, Object> tc : toolCalls) {
                Map<String, Object> func = (Map<String, Object>) tc.get("function");
                String funcName = (String) func.get("name");
                String argsStr = (String) func.get("arguments");
                String result = executeTool(funcName, argsStr);
                toolResults.append("\n【").append(funcName).append("结果】:\n").append(result).append("\n");
            }

            // 第二轮：将工具结果附加到用户消息中
            String enrichedMessage = userMessage + "\n\n[系统已自动查询到以下真实数据，请基于此回答]:" + toolResults.toString();
            String response2 = callDeepSeek(systemPrompt, enrichedMessage, toolsJson);
            String content = extractContent(response2);
            return Result.success(content);

        } catch (Exception e) {
            return Result.error("哼，系统出了点问题…等会儿再试吧：" + e.getMessage());
        }
    }

    // ====== Tool Execution ======

    private String executeTool(String name, String args) {
        try {
            return switch (name) {
                case "query_products" -> queryProducts(args);
                case "get_product_stats" -> getProductStats();
                case "query_orders" -> queryOrders();
                case "get_campus_info" -> getCampusInfo(args);
                default -> "{\"error\":\"未知工具: " + name + "\"}";
            };
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private String queryProducts(String args) {
        String category = extractJsonString(args, "category");
        String keyword = extractJsonString(args, "keyword");
        int limit = parseIntArg(args, "limit", 5);

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        wrapper.last("LIMIT " + Math.min(limit, 20));

        List<Product> products = productMapper.selectList(wrapper);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format(
                "{\"name\":\"%s\",\"price\":%.2f,\"category\":\"%s\",\"stock\":%d,\"desc\":\"%s\"}",
                escapeJson(p.getName()), p.getPrice(),
                p.getCategory() != null ? escapeJson(p.getCategory()) : "",
                p.getStock() != null ? p.getStock() : 0,
                p.getDescription() != null ? escapeJson(p.getDescription()) : ""
            ));
        }
        sb.append("]");
        return products.isEmpty() ? "{\"count\":0,\"products\":[],\"msg\":\"没有找到匹配的商品\"}" :
               "{\"count\":" + products.size() + ",\"products\":" + sb.toString() + "}";
    }

    private String getProductStats() {
        List<Product> all = productMapper.selectList(new QueryWrapper<Product>().eq("status", 1));
        Map<String, Long> byCat = all.stream()
            .collect(Collectors.groupingBy(p -> p.getCategory() != null ? p.getCategory() : "其他", Collectors.counting()));
        double minP = all.stream().mapToDouble(p -> p.getPrice().doubleValue()).min().orElse(0);
        double maxP = all.stream().mapToDouble(p -> p.getPrice().doubleValue()).max().orElse(0);
        StringBuilder cats = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Long> e : byCat.entrySet()) {
            if (i++ > 0) cats.append(",");
            cats.append("\"").append(e.getKey()).append("\":").append(e.getValue());
        }
        cats.append("}");
        return String.format("{\"total\":%d,\"categories\":%s,\"price_range\":\"¥%.0f-%.0f\"}",
            all.size(), cats.toString(), minP, maxP);
    }

    private String queryOrders() {
        Long total = orderMapper.selectCount(null);
        Long paid = orderMapper.selectCount(new QueryWrapper<Order>().eq("status", "paid"));
        Long delivered = orderMapper.selectCount(new QueryWrapper<Order>().eq("status", "delivered"));
        Long cancelled = orderMapper.selectCount(new QueryWrapper<Order>().eq("status", "cancelled"));
        return String.format("{\"total_orders\":%d,\"paid\":%d,\"delivered\":%d,\"cancelled\":%d}", total, paid, delivered, cancelled);
    }

    private String getCampusInfo(String args) {
        String topic = extractJsonString(args, "topic");
        String knowledge = getCampusKnowledge();
        String lower = knowledge;
        if (topic != null) {
            // 搜索相关段落
            StringBuilder result = new StringBuilder();
            for (String line : knowledge.split("\n")) {
                if (line.contains(topic) || topic.contains(line.substring(0, Math.min(4, line.length())))) {
                    result.append(line).append("\n");
                }
            }
            if (result.length() > 0) {
                return "{\"info\":\"" + escapeJson(result.toString().trim()) + "\"}";
            }
        }
        // 返回摘要
        return "{\"info\":\"" + escapeJson(knowledge.substring(0, Math.min(2000, knowledge.length()))) + "\"}";
    }

    // ====== DeepSeek API ======

    private String callDeepSeek(String systemPrompt, String userMessage, String tools) throws Exception {
        String body = "{"
            + "\"model\":\"deepseek-chat\","
            + "\"messages\":["
            + "{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},"
            + "{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}"
            + "],"
            + "\"tools\":" + tools + ","
            + "\"max_tokens\":400,"
            + "\"temperature\":0.7"
            + "}";
        return sendRequest(body);
    }

    private String sendRequest(String body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(DEEPSEEK_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setDoOutput(true);
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(60000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() == 200) {
            return new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("DeepSeek API error: " + err);
        }
    }

    // ====== JSON Helpers ======

    private String extractContent(String json) {
        int idx = json.indexOf("\"content\"");
        if (idx < 0) return "哼，我不知道该怎么回答…";
        idx = json.indexOf("\"", idx + 10);
        if (idx < 0) return "哼，我不知道该怎么回答…";
        // 跳过可能为 null 的情况
        String val = json.substring(idx + 1);
        if (val.startsWith("null")) {
            // tool_calls 场景，找下一个 content
            int idx2 = json.indexOf("\"content\"", idx + 10);
            if (idx2 > 0) {
                idx2 = json.indexOf("\"", idx2 + 10);
                if (idx2 > 0) {
                    int end2 = findJsonStringEnd(json, idx2 + 1);
                    return unescapeJson(json.substring(idx2 + 1, end2));
                }
            }
            return "哼，让我查查…（思考中）";
        }
        int end = findJsonStringEnd(json, idx + 1);
        return unescapeJson(json.substring(idx + 1, end));
    }

    private List<Map<String, Object>> extractToolCalls(String json) {
        List<Map<String, Object>> result = new ArrayList<>();
        int idx = json.indexOf("\"tool_calls\"");
        if (idx < 0) return result;

        // 手动解析 tool_calls
        int arrStart = json.indexOf("[", idx);
        if (arrStart < 0) return result;
        int arrEnd = findBracketEnd(json, arrStart);
        if (arrEnd < 0) return result;
        String tcJson = json.substring(arrStart, arrEnd + 1);

        // 简单解析
        int pos = 0;
        while ((pos = tcJson.indexOf("\"id\"", pos)) >= 0) {
            Map<String, Object> tc = new LinkedHashMap<>();
            int idStart = tcJson.indexOf("\"", pos + 5) + 1;
            int idEnd = findJsonStringEnd(tcJson, idStart);
            tc.put("id", tcJson.substring(idStart, idEnd));
            tc.put("type", "function");

            Map<String, Object> func = new LinkedHashMap<>();
            int nameIdx = tcJson.indexOf("\"name\"", pos);
            int nameStart = tcJson.indexOf("\"", nameIdx + 7) + 1;
            int nameEnd = findJsonStringEnd(tcJson, nameStart);
            func.put("name", tcJson.substring(nameStart, nameEnd));

            int argsIdx = tcJson.indexOf("\"arguments\"", pos);
            int argsStart = tcJson.indexOf("\"", argsIdx + 13) + 1;
            int argsEnd = findJsonStringEnd(tcJson, argsStart);
            func.put("arguments", unescapeJson(tcJson.substring(argsStart, argsEnd)));

            tc.put("function", func);
            result.add(tc);
            pos = idEnd;
        }
        return result;
    }

    private String extractJsonString(String json, String key) {
        // 找 "key" 后面的值：跳过 "key" : "value"
        String pattern = "\"" + key + "\"";
        int keyIdx = json.indexOf(pattern);
        if (keyIdx < 0) return null;
        int colonIdx = json.indexOf(":", keyIdx + pattern.length());
        if (colonIdx < 0) return null;
        // 跳过冒号后的空白
        int valStart = colonIdx + 1;
        while (valStart < json.length() && json.charAt(valStart) == ' ') valStart++;
        if (valStart >= json.length()) return null;
        if (json.charAt(valStart) == '"') {
            // 字符串值
            int valEnd = findJsonStringEnd(json, valStart + 1);
            return unescapeJson(json.substring(valStart + 1, valEnd));
        }
        // 数字值
        int valEnd = valStart;
        while (valEnd < json.length() && (Character.isDigit(json.charAt(valEnd)) || json.charAt(valEnd) == '.' || json.charAt(valEnd) == '-')) valEnd++;
        return json.substring(valStart, valEnd);
    }

    private int parseIntArg(String json, String key, int defaultVal) {
        String s = extractJsonString(json, key);
        if (s == null) return defaultVal;
        try { return Integer.parseInt(s); } catch (Exception e) { return defaultVal; }
    }

    private int findJsonStringEnd(String s, int start) {
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '\\') { i++; continue; }
            if (s.charAt(i) == '"') return i;
        }
        return s.length();
    }

    private int findBracketEnd(String s, int start) {
        char open = s.charAt(start);
        char close = open == '[' ? ']' : '}';
        int depth = 1;
        boolean inStr = false;
        for (int i = start + 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && inStr) { i++; continue; }
            if (c == '"') inStr = !inStr;
            if (inStr) continue;
            if (c == open) depth++;
            if (c == close) { depth--; if (depth == 0) return i; }
        }
        return -1;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
                .replace("\r", "\\r").replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
                .replace("\\t", "\t").replace("\\r", "\r");
    }
}
