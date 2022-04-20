package com.heartsuit.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.storage.domain.Power;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-09-03
 */
@Mapper
@Repository
public interface PowerMapper extends BaseMapper<Power> {
    @Insert("insert into device${sn}(ts,voltage,currente,temperature) values(#{ts}, #{voltage}, #{currente}, #{temperature})")
    int insertOne(Power one);

    Map getAllDataAvg();
    Map getAllDataAvgInCity(String city);

    List<String> getSubTablesInCity(String city);
    List<Map<String, Object>> getLastValue(List<String> tableNames);

    List<Map<String, Object>> getDeviceDataTrend(String sn, String startTime, String endTime);
}
