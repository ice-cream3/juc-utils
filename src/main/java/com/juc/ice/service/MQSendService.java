package com.juc.ice.service;

import com.alibaba.fastjson.JSON;
import com.juc.ice.utils.MQMsg;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MQSendService
 * @Author: dream
 * @Date: 2023/11/29 11:33
 */
@Service
public class MQSendService {

    private static final Logger logger = LoggerFactory.getLogger(MQSendService.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public SendResult send(String topic, String tag, MQMsg<?> msgBody) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            logger.info("send消息,t:{},mid:{}", topic, msgId);
            sendResult = producer.send(new Message(topic, tag, JSON.toJSONBytes(msgBody)));
            logger.info("send成功,t:{},mid:{},sendResult:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("send异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return sendResult;
    }

    public SendResult send(String topic, MQMsg<?> msgBody) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            logger.info("send消息,t:{},mid:{}", topic, msgId);
            sendResult = producer.send(new Message(topic, JSON.toJSONBytes(msgBody)));
            logger.info("send成功,t:{},mid:{},sendResult:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("send异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return sendResult;
    }

    /**
     * 同步消息
     * @param topic:[tag] --> global:admin
     */
    public SendResult syncSend(String topic, MQMsg<?> msgBody) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{}", topic, msgId);
            sendResult = rocketMQTemplate.syncSend(topic, msgBody);
            logger.info("发送成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步消息
     * @param timeout 发送超时时间,毫秒
     */
    public SendResult syncSend(String topic, MQMsg<?> msgBody, long timeout) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{}", topic, msgId);
            sendResult = rocketMQTemplate.syncSend(topic, msgBody, timeout);
            logger.info("发送成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步消息
     * @param headers 头信息
     */
    public SendResult syncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            sendResult = rocketMQTemplate.syncSend(topic, message);
            logger.info("发送成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送消息异常,mid:{},t:{},error:{}", msgId, topic, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     */
    public SendResult syncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers, long timeout) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            sendResult = rocketMQTemplate.syncSend(topic, message, timeout);
            logger.info("发送成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     * @param delayLevel 延时发送(1=1s 2=5s 3=10s ...)
     *  (延时级别:1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h)
     */
    public SendResult syncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers, long timeout, int delayLevel) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            sendResult = rocketMQTemplate.syncSend(topic, message, timeout, delayLevel);
            logger.info("发送成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 批量发送消息
     * @throws Exception 消息不能为空
     */
    public SendResult syncBatchSend(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers) throws Exception {
        SendResult sendResult;
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("批量消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {
            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return (org.springframework.messaging.Message) getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("批量发送消息,t:{},mid:{}", topic, msgIds);
            sendResult = rocketMQTemplate.syncSend(topic, messageList);
            logger.info("批量发送成功,t:{},mid:{},result:{}", topic, msgIds, sendResult);
        } catch (Exception e) {
            logger.error("批量发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 批量发送消息
     * @throws Exception 消息不能为空
     */
    public SendResult syncBatchSend(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers, long timeout) throws Exception {
        SendResult sendResult;
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("批量消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {
            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return (org.springframework.messaging.Message) getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("批量发送消息,t:{},mid:{}", topic, msgIds);
            sendResult = rocketMQTemplate.syncSend(topic, messageList, timeout);
            logger.info("批量发送成功,t:{},mid:{},result:{}", topic, msgIds, sendResult);
        } catch (Exception e) {
            logger.error("批量发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 异步消息
     */
    public void asyncSend(String topic, MQMsg<?> msgBody, SendCallback callback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("异步发送消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.asyncSend(topic, msgBody, callback);
        } catch (Exception e) {
            logger.error("异步发送消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步消息
     * @param timeout 发送超时时间,毫秒
     */
    public void asyncSend(String topic, MQMsg<?> msgBody, SendCallback callback, long timeout) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("异步发送消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.asyncSend(topic, msgBody, callback, timeout);
        } catch (Exception e) {
            logger.error("异步发送消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步消息
     * @param headers 头信息
     */
    public void asyncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers, SendCallback callback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("异步发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.asyncSend(topic, message, callback);
        } catch (Exception e) {
            logger.error("异步发送消息异常,mid:{},t:{},error:{}", msgId, topic, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     */
    public void asyncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers, long timeout, SendCallback callback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("异步发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.asyncSend(topic, message, callback, timeout);
        } catch (Exception e) {
            logger.error("异步发送消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     * @param delayLevel 延时发送(1=1s 2=5s 3=10s ...)
     *  (延时级别:1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h)
     */
    public void asyncSend(String topic, MQMsg<?> msgBody, Map<String, Object> headers, SendCallback callback, long timeout, int delayLevel) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("异步发送消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.asyncSend(topic, message, callback, timeout, delayLevel);
        } catch (Exception e) {
            logger.error("异步发送消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 批量发送消息
     * @throws Exception 消息不能为空
     */
    public void asyncBatchSend(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers, SendCallback callback) throws Exception {
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("批量消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {
            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return (org.springframework.messaging.Message) getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("异步批量发送消息,t:{},mid:{}", topic, msgIds);
            rocketMQTemplate.asyncSend(topic, messageList, callback);
        } catch (Exception e) {
            logger.error("异步批量发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 批量发送消息
     * @throws Exception 消息不能为空
     *
     */
    public void asyncBatchSend(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers, long timeout, SendCallback callback) throws Exception {
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("异步批量消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {

            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("异步批量发送消息,t:{},mid:{}", topic, msgIds);
            rocketMQTemplate.asyncSend(topic, messageList, callback, timeout);
        } catch (Exception e) {
            logger.error("异步批量发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 同步顺序消息
     * @param hashKey 顺序哈希值
     */
    public SendResult syncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送顺序消息,t:{},mid:{}", topic, msgId);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, msgBody, hashKey);
            logger.info("发送顺序成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步顺序消息
     * @param timeout 发送超时时间,毫秒
     */
    public SendResult syncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey, long timeout) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送顺序消息,t:{},mid:{}", topic, msgId);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, msgBody, hashKey, timeout);
            logger.info("发送顺序成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 同步顺序消息
     * @param headers 头信息
     */
    public SendResult syncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey, Map<String, Object> headers) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送顺序消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
            logger.info("发送顺序成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送顺序消息异常,mid:{},t:{},error:{}", msgId, topic, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    private org.springframework.messaging.Message<?> getMessage(MQMsg<?> msgBody, Map<String, Object> headers) {
        return new org.springframework.messaging.Message<Object>() {
            @Override
            public Object getPayload() {
                return msgBody;
            }
            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> msgHeaders = new HashMap<>();
                if (null != headers && !headers.isEmpty()) {
                    msgHeaders.putAll(headers);
                }
                return new MessageHeaders(msgHeaders);
            }
        };
    }

    /**
     * 此等价与 getMessage == getMessaging
     * @param msgBody 消息体
     * @param headers 消息头信息
     * @return 消息
     */
    private org.springframework.messaging.Message<?> getMessaging(MQMsg<?> msgBody, Map<String, Object> headers) {
        MessageHeaderAccessor msgHeaders = new MessageHeaderAccessor();
        msgHeaders.copyHeaders(headers);
        return MessageBuilder.withPayload(msgBody).setHeaders(msgHeaders).build();
    }

    /**
     * 同步顺序消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     */
    public SendResult syncSendOrderly(String topic, MQMsg<?> msgBody, Map<String, Object> headers, String hashKey, long timeout) {
        SendResult sendResult;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送顺序消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, message, hashKey, timeout);
            logger.info("发送顺序成功,t:{},mid:{},result:{}", topic, msgId, sendResult);
        } catch (Exception e) {
            logger.error("发送顺序消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 批量顺序发送消息
     * @throws Exception 消息不能为空
     */
    public SendResult syncBatchSendOrderly(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers, String hashKey) throws Exception {
        SendResult sendResult;
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("批量顺序消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {
            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("批量顺序发送消息,t:{},mid:{}", topic, msgIds);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, messageList, hashKey);
            logger.info("批量顺序发送成功,t:{},mid:{},result:{}", topic, msgIds, sendResult);
        } catch (Exception e) {
            logger.error("批量顺序发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 批量顺序发送消息
     * @throws Exception 消息不能为空
     */
    public SendResult syncBatchSendOrderly(String topic, List<MQMsg<?>> msgList, Map<String, Object> headers, String hashKey, long timeout) throws Exception {
        SendResult sendResult;
        if (CollectionUtils.isEmpty(msgList)) {
            throw new Exception("批量顺序消息不能为空");
        }
        List<String> msgIds = new ArrayList<>();
        try {
            List<org.springframework.messaging.Message> messageList = msgList.stream().map(f -> {
                msgIds.add(f.getMsgId());
                return (org.springframework.messaging.Message) getMessage(f, headers);
            }).collect(Collectors.toList());
            logger.info("批量顺序发送消息,t:{},mid:{}", topic, msgIds);
            sendResult = rocketMQTemplate.syncSendOrderly(topic, messageList, hashKey, timeout);
            logger.info("批量顺序发送成功,t:{},mid:{},result:{}", topic, msgIds, sendResult);
        } catch (Exception e) {
            logger.error("批量顺序发送消息异常,mid:{},error:{}", msgIds, e.getMessage(), e);
            throw e;
        }
        return sendResult;
    }

    /**
     * 异步顺序消息
     * @param hashKey 顺序哈希值
     */
    public void asyncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey, SendCallback sendCallback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送异步顺序消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.asyncSendOrderly(topic, msgBody, hashKey, sendCallback);
        } catch (Exception e) {
            logger.error("发送异步顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步顺序消息
     * @param timeout 发送超时时间,毫秒
     */
    public void asyncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey, long timeout, SendCallback sendCallback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送异步顺序消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.asyncSendOrderly(topic, msgBody, hashKey, sendCallback, timeout);
        } catch (Exception e) {
            logger.error("发送异步顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步顺序消息
     * @param headers 头信息
     */
    public void asyncSendOrderly(String topic, MQMsg<?> msgBody, String hashKey, Map<String, Object> headers, SendCallback sendCallback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送异步顺序消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.asyncSendOrderly(topic, message, hashKey, sendCallback);
        } catch (Exception e) {
            logger.error("发送异步顺序消息异常,mid:{},t:{},error:{}", msgId, topic, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 异步顺序消息
     * @param headers 头信息
     * @param timeout 发送超时时间(毫秒)
     */
    public void asyncSendOrderly(String topic, MQMsg<?> msgBody, Map<String, Object> headers, String hashKey, long timeout, SendCallback sendCallback) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送异步顺序消息,t:{},mid:{},h:{}", topic, msgId, headers);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.asyncSendOrderly(topic, message, hashKey, sendCallback, timeout);
        } catch (Exception e) {
            logger.error("发送异步顺序消息异常,t:{},mid:{},error:{}", topic, msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 单向消息发送
     *  只管发送,不在乎消息是否成功
     */
    public void sendOneWay(String topic, MQMsg<?> msgBody) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送单向消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.sendOneWay(topic, msgBody);
        } catch (Exception e) {
            logger.error("发送单向消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 单向消息发送
     *  只管发送,不在乎消息是否成功
     */
    public void sendOneWay(String topic, MQMsg<?> msgBody, Map<String, Object> headers) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送单向消息,t:{},mid:{}", topic, msgId);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.sendOneWay(topic, message);
        } catch (Exception e) {
            logger.error("发送单向消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 单向消息发送
     *  只管发送,不在乎消息是否成功
     */
    public void sendOneWayOrderly(String topic, MQMsg<?> msgBody) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送单向顺序消息,t:{},mid:{}", topic, msgId);
            rocketMQTemplate.sendOneWay(topic, msgBody);
        } catch (Exception e) {
            logger.error("发送单向顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 单向顺序消息发送
     *  只管发送,不在乎消息是否成功
     */
    public void sendOneWayOrderly(String topic, MQMsg<?> msgBody, Map<String, Object> headers) {
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送单向顺序消息,t:{},mid:{}", topic, msgId);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            rocketMQTemplate.sendOneWay(topic, message);
        } catch (Exception e) {
            logger.error("发送单向顺序消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
    }

    public MessageExt sendAndReceive(String topic, MQMsg<?> msgBody, Map<String, Object> headers) {
        MessageExt messageExt;
        String msgId = msgBody.getMsgId();
        try {
            logger.info("发送消息,t:{},mid:{}", topic, msgId);
            org.springframework.messaging.Message<?> message = getMessage(msgBody, headers);
            messageExt = rocketMQTemplate.sendAndReceive(topic, message, MessageExt.class);
            logger.info("发送消息成功,t:{},mid:{}", topic, msgId);
        } catch (Exception e) {
            logger.error("发送消息异常,mid:{},error:{}", msgId, e.getMessage(), e);
            throw e;
        }
        return messageExt;
    }


//     if (Objects.equals(type, MessageExt .class)) {
//    } else if (Objects.equals(type, byte[].class)) {
//    } else {
//        if (Objects.equals(type, String.class)) {
//        } else {
//            try {
//                if (type instanceof Class) {
}
