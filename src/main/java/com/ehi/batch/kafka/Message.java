package com.ehi.batch.kafka;

import lombok.Data;

import java.util.Date;

/**
 * @author portz
 * @date 04/24/2022 20:57
 */
@Data
public class Message {
    private Long id;    //id
    private String msg; //消息
    private Date sendTime;  //时间戳

}