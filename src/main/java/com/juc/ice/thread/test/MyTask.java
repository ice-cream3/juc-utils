package com.juc.ice.thread.test;

import java.util.concurrent.Callable;

/**
 * @ClassName: MyTask
 * @Description:
 * @Author: ice
 * @Date: 2023/6/28 12:51
 */
public class MyTask implements Callable<Integer> {

    private Integer source;

    public MyTask(Integer source){
        this.source = source;
    }
    @Override
    public Integer call() throws Exception {
        System.out.println("当前线程：" + Thread.currentThread().getName());
        switch (source){
            case 1:{
                Thread.sleep(1000);
                return 1;
            }
            case 2:{
                Thread.sleep(2000);
                return 2;
            }
            case 3:{
                Thread.sleep(3000);
                return 3;
            }
            default:return 0;
        }
    }
}
