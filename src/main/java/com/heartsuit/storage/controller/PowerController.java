package com.heartsuit.storage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heartsuit.common.result.Result;
import com.heartsuit.storage.domain.Power;
import com.heartsuit.storage.mapper.PowerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * TopN查询: 查询最新10条数据
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

    /**
     * 分页查询
     * @return
     */
    @GetMapping("page")
    public Result selectPage() {
        // Test
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
        wrapper.eq("sn", 1100);
        wrapper.eq("city", "西安");
        wrapper.eq("groupid", 2);

        IPage page = new Page(1, 3);
        IPage<Power> powerIPage = powerMapper.selectPage(page, null);

        System.out.println("total : " + powerIPage.getTotal());
        System.out.println("pages : " + powerIPage.getPages());

        for (Power power : powerIPage.getRecords()) {
            System.out.println(power);
        }
        return Result.success(powerIPage);
    }

    /**
     * 条件查询：查询传入设备组的最新数据
     * @param city
     * @return
     */
    @GetMapping("lastValue/{city}")
    public Result getLastValue(@PathVariable String city) {
        List<String> tableNames = powerMapper.getSubTablesInCity(city);
        List<Map<String, Object>> lastValues = powerMapper.getLastValue(tableNames);
        Map<String, Object> result = new HashMap<>();
        result.put("tableNames", tableNames);
        result.put("powers", lastValues);
        return Result.success(result);
    }

    /**
     * 条件、聚合查询：查询指定城市的数据量，数据指标：电压、电流、温度平均值
     * @param city
     * @return
     */
    @GetMapping("totalInCity/{city}")
    public Result selectCountInCity(@PathVariable String city) {
        Map<String, Object> allDataAvgInCity = powerMapper.getAllDataAvgInCity(city);
        return Result.success(allDataAvgInCity);
    }

    /**
     * 分页、条件查询：查询传入设备在指定时间范围内的数据
     * @param page
     * @param size
     * @param params
     * @return
     */
    @GetMapping("getDeviceData")
    public Result getDeviceData(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam Map<String, Object> params) {
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
//        wrapper.select("")
        wrapper.eq("sn", params.get("sn"));
//        wrapper.between("ts", "2021-09-07 09:15:11.138", "2021-09-07 10:15:11.138");
        wrapper.between("ts", params.get("startTime"), params.get("endTime"));
        wrapper.orderByDesc("ts");
        IPage deviceData = powerMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(deviceData);
    }
}
