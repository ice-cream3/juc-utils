package com.juc.ice.service;

import com.alibaba.fastjson.JSON;
import com.juc.ice.utils.MQMsg;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @ClassName: RocketMqTestService
 * @Description: 拓展消息
 * @Author: ice
 * @Date: 2023/11/28 20:27
 */
@Service
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2", messageModel = MessageModel.BROADCASTING, consumeMode = ConsumeMode.ORDERLY)
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2", messageModel = MessageModel.BROADCASTING)
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2")
@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", messageModel = MessageModel.BROADCASTING)
public class RocketMqMsgExtListener implements RocketMQListener<MessageExt> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqMsgExtListener.class);

    @Override
    public void onMessage(MessageExt msg) {
        String msgId = msg.getMsgId();
        MQMsg object = JSON.parseObject(new String(msg.getBody()), MQMsg.class);
        logger.info("msgExt收到一个信息,msgId:{},body:{}", msg.getMsgId(), object);
    }
}
