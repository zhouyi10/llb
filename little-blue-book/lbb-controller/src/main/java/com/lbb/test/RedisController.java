package com.lbb.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * String(字符串)类型操作
     */
    @GetMapping("/string")
    public String stringOperations() {
        String key = "redis_string_key";
        String value = "redis_string_value";

        // 设置值
        redisTemplate.opsForValue().set(key, value);

        // 设置值并指定过期时间
        redisTemplate.opsForValue().set(key + "_expire", value, 60, TimeUnit.SECONDS);

        // 获取值
        Object result = redisTemplate.opsForValue().get(key);

        // 自增
        redisTemplate.opsForValue().increment("counter", 1);

        // 自减
        redisTemplate.opsForValue().decrement("counter", 1);

        return "String操作完成: " + result;
    }

    /**
     * Hash(哈希)类型操作
     */
    @GetMapping("/hash")
    public String hashOperations() {
        String key = "redis_hash_key";

        // 设置单个字段
        redisTemplate.opsForHash().put(key, "field1", "value1");
        redisTemplate.opsForHash().put(key, "field2", "value2");

        // 批量设置
        Map<String, Object> map = new HashMap<>();
        map.put("field3", "value3");
        map.put("field4", "value4");
        redisTemplate.opsForHash().putAll(key, map);

        // 获取单个字段
        Object value = redisTemplate.opsForHash().get(key, "field1");

        // 获取所有字段和值
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        // 删除字段
        redisTemplate.opsForHash().delete(key, "field1");

        // 判断字段是否存在
        Boolean exists = redisTemplate.opsForHash().hasKey(key, "field2");

        return "Hash操作完成: " + value;
    }

    /**
     * List(列表)类型操作
     */
    @GetMapping("/list")
    public String listOperations() {
        String key = "redis_list_key";

        // 从左侧推入
        redisTemplate.opsForList().leftPush(key, "value1");
        redisTemplate.opsForList().leftPush(key, "value2");

        // 从右侧推入
        redisTemplate.opsForList().rightPush(key, "value3");

        // 批量推入
        redisTemplate.opsForList().rightPushAll(key, "value4", "value5");

        // 获取列表范围
        List<Object> values = redisTemplate.opsForList().range(key, 0, -1);

        // 获取列表长度
        Long size = redisTemplate.opsForList().size(key);

        // 弹出左侧元素
        Object leftPop = redisTemplate.opsForList().leftPop(key);

        // 弹出右侧元素
        Object rightPop = redisTemplate.opsForList().rightPop(key);

        return "List操作完成, 当前列表: " + values;
    }

    /**
     * Set(集合)类型操作
     */
    @GetMapping("/set")
    public String setOperations() {
        String key = "redis_set_key";

        // 添加元素
        redisTemplate.opsForSet().add(key, "value1", "value2", "value3");

        // 获取所有元素
        Set<Object> members = redisTemplate.opsForSet().members(key);

        // 判断元素是否存在
        Boolean isMember = redisTemplate.opsForSet().isMember(key, "value1");

        // 获取集合大小
        Long size = redisTemplate.opsForSet().size(key);

        // 删除元素
        redisTemplate.opsForSet().remove(key, "value1");

        // 随机获取一个元素
        Object randomMember = redisTemplate.opsForSet().randomMember(key);

        return "Set操作完成, 成员: " + members;
    }

    /**
     * ZSet(有序集合)类型操作
     */
    @GetMapping("/zset")
    public String zsetOperations() {
        String key = "redis_zset_key";

        // 添加元素及分数
        redisTemplate.opsForZSet().add(key, "value1", 1.0);
        redisTemplate.opsForZSet().add(key, "value2", 2.0);
        redisTemplate.opsForZSet().add(key, "value3", 3.0);

        // 批量添加
        Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        // 注意: 实际使用时需要创建TypedTuple对象

        // 获取排名范围(从小到大)
        Set<Object> range = redisTemplate.opsForZSet().range(key, 0, -1);

        // 按分数范围查询
        Set<Object> rangeByScore = redisTemplate.opsForZSet().rangeByScore(key, 1.0, 2.0);

        // 获取元素分数
        Double score = redisTemplate.opsForZSet().score(key, "value1");

        // 获取元素排名
        Long rank = redisTemplate.opsForZSet().rank(key, "value1");

        // 增加分数
        redisTemplate.opsForZSet().incrementScore(key, "value1", 1.0);

        // 删除元素
        redisTemplate.opsForZSet().remove(key, "value1");

        return "ZSet操作完成, 排名: " + range;
    }

    /**
     * 通用操作
     */
    @GetMapping("/common")
    public String commonOperations() {
        String key = "redis_common_key";
        redisTemplate.opsForValue().set(key, "test_value");

        // 设置过期时间
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);

        // 获取剩余过期时间
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        // 判断key是否存在
        Boolean hasKey = redisTemplate.hasKey(key);

        // 删除key
        redisTemplate.delete(key);

        // 重命名key
        // redisTemplate.rename(key, "new_key");

        return "通用操作完成";
    }

    /**
     * 管道操作(批量操作,提高性能)
     */
    @GetMapping("/pipeline")
    public String pipelineOperations() {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            for (int i = 0; i < 100; i++) {
                connection.stringCommands().set(("pipeline_key_" + i).getBytes(),
                        ("value_" + i).getBytes());
            }
            return null;
        });

        return "管道操作完成, 执行了 " + results.size() + " 个命令";
    }


}
