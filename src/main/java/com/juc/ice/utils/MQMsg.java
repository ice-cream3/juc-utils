package com.juc.ice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MQMsg
 * @Description:
 * @Author: ice
 * @Date: 2023/11/29 12:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MQMsg<T> {

    private String msgId;

    private String uniqId;

    @Getter
    private Object msgBody;

    public MQMsg(Object msgBody) {
        this.msgBody = msgBody;
        this.msgId = MQClientIDUtil.createUniqID();
    }

    public MQMsg(String uniqId, Object msgBody) {
        this.msgId = MQClientIDUtil.createUniqID();
        this.uniqId = uniqId;
        this.msgBody = msgBody;
    }

}
