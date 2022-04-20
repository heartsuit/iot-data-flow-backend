package com.heartsuit.storage.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heartsuit.common.result.Result;
import com.heartsuit.storage.domain.Power;
import com.heartsuit.storage.mapper.PowerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 降采样：查询设备数据趋势
     * @param params
     * @return
     */
    @GetMapping("getDeviceDataTrend")
    public Result getDeviceDataTrend(@RequestParam Map<String, Object> params) {
        String sn = (String) params.get("sn");
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        List<Map<String, Object>> deviceDataTrend = powerMapper.getDeviceDataTrend(sn, startTime, endTime);

        List<Object> axisX = new ArrayList<>();
        List<Object> voltage = new ArrayList<>();
        List<Object> currente = new ArrayList<>();;
        List<Object> temperature = new ArrayList<>();;

        deviceDataTrend.forEach(item -> {
            axisX.add(item.get("ts"));
            voltage.add(item.get("voltage"));
            currente.add(item.get("currente"));
            temperature.add(item.get("temperature"));
        });

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> series = new HashMap<>();
        series.put("电压", voltage);
        series.put("电流", currente);
        series.put("温度", temperature);

        result.put("axisX", axisX);
        result.put("series", series);
        return Result.success(result);
    }

    /**
     * 导出数据为Excel，实际一般为条件检索后导出
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("exportXls")
    public void exportExcel(HttpServletResponse response, @RequestParam Map<String, Object> params) throws IOException, ClassNotFoundException {
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
        wrapper.eq("sn", params.get("sn"));
        wrapper.between("ts", params.get("startTime"), params.get("endTime"));
        wrapper.orderByDesc("ts");
        List<Power> powers = powerMapper.selectList(wrapper);

        List<Map<String, Object>> rows = powers.stream().map(item -> {
            Map<String, Object> maps = new HashMap<>();
            maps.put("ts", item.getTs().toString());
            maps.put("voltage", item.getVoltage());
            maps.put("currente", item.getCurrente());
            maps.put("temperature", item.getTemperature());
            maps.put("sn", item.getSn());
            maps.put("city", item.getCity());
            maps.put("groupid", item.getGroupid());
            return maps;
        }).collect(Collectors.toList());

        ExcelWriter writer = ExcelUtil.getWriter();

        // Title
        int columns = Class.forName("com.heartsuit.storage.domain.Power").getDeclaredFields().length;
        writer.merge(columns - 1, "历史数据");

        // Header
        writer.addHeaderAlias("ts", "时间");
        writer.addHeaderAlias("voltage", "电压");
        writer.addHeaderAlias("currente", "电流");
        writer.addHeaderAlias("temperature", "温度");
        writer.addHeaderAlias("sn", "设备编号");
        writer.addHeaderAlias("city", "城市");
        writer.addHeaderAlias("groupid", "分组ID");

        // Body
        writer.setColumnWidth(0, 30);
        writer.setColumnWidth(1, 30);
        writer.setColumnWidth(2, 30);
        writer.setColumnWidth(3, 30);
        writer.setColumnWidth(4, 30);
        writer.setColumnWidth(5, 30);
        writer.setColumnWidth(6, 30);
        writer.setColumnWidth(7, 30);
        writer.write(rows, true);

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("设备device" + params.get("sn") + "历史数据-" + DateUtil.today() + ".xls", "utf-8"));

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }
}
