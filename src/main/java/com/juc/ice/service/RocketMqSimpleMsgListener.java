package com.juc.ice.service;

import com.juc.ice.utils.MQMsg;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @ClassName: RocketMqTestService
 * @Description: 普通消息
 * @Author: ice
 * @Date: 2023/11/28 20:27
 */
@Service
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2", messageModel = MessageModel.BROADCASTING, consumeMode = ConsumeMode.ORDERLY)
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2", messageModel = MessageModel.BROADCASTING)
//@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic", selectorExpression = "tag1 || tag2")
@RocketMQMessageListener(consumerGroup = "global-ice", topic = "global-topic")
public class RocketMqSimpleMsgListener implements RocketMQListener<MQMsg<String>> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqSimpleMsgListener.class);

    @Override
    public void onMessage(MQMsg msg) {
        String msgId = msg.getMsgId();
        MQMsg<String> object = (MQMsg) msg.getMsgBody();
        logger.info("simple收到一个信息,msgId:{},body:{}", msgId, object);
    }
}
