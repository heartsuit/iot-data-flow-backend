package com.heartsuit.alert;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-11-10
 */
@Data
public class AlertMessage {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private Date startsAt;

//    时间字符串：0001-01-01T00:00:00Z，如何解析？？
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
//    private Timestamp endsAt;

    private Map<String, Object> values;
    private Map<String, String> labels;
    private Map<String, String> annotations;
}
