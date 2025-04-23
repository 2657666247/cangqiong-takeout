package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Slf4j
@Api("用户店铺相关接口")
@RequestMapping("/user/shop")
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;
    private static final String key = "SHOP_STATUS";
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> queryStatus() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(key);
        log.info("店铺状态为:{}", status == 1 ? "营业中" : "打烊中");

        return Result.success(status);
    }

}
