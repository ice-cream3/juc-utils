package com.juc.ice.thread.test;

import lombok.Getter;

/**
 * @Description: 任务枚举
 * @author: ice
 */
@Getter
public enum TaskEnum {

    TASK1(1, "任务1"),
    TASK2(2, "任务2"),
    TASK3(3, "任务3"),
    TASK4(4, "任务4"),
    TASK5(5, "任务5"),
    TASK6(6, "任务6");

    // 任务类型
    private Integer type;

    // 任务名称
    private String desc;

    TaskEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
