package com.heartsuit.storage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heartsuit.common.result.Result;
import com.heartsuit.storage.domain.Power;
import com.heartsuit.storage.mapper.PowerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@RestController
@RequestMapping("power")
public class PowerController {
    @Autowired
    private PowerMapper powerMapper;

    /**
     * 查询最新10条数据
     * @return
     */
    @GetMapping("select")
    public Result selectList() {
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
        wrapper.last("limit 10");
        List<Power> powerList = powerMapper.selectList(wrapper);
        powerList.forEach(System.out::println);
        return Result.success(powerList);
    }

    /**
     * 查询数据总量
     * @return
     */
    @GetMapping("total")
    public Result selectCount() {
        int count = powerMapper.selectCount(null);
        return Result.success(count);
    }
}
