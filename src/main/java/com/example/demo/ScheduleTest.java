package com.example.demo;

import com.example.demo.util.ScheduleUtils;
import com.example.demo.util.ScheduleUtils.Job;

/**
 * @author lixk
 * @version [1.0, 2017年8月16日]
 * @ClassName: ScheduleTest
 * @Description: TODO
 * @date 2017年8月16日 下午3:03:36
 * @since version 1.0
 */
public class ScheduleTest {

    public void print1() {
        System.out.println("1");
    }

    public void print2() {
        System.out.println("2");
    }

    public void print3() {
        throw new RuntimeException("exception!");
    }

    /**
     * @param args
     * @throws Exception
     * @Title: main
     * @Description: TODO
     */
    public static void main(String[] args) throws Exception {
        Job job1 = new Job();
        job1.setClassName("com.example.demo.ScheduleTest");
        job1.setCron("*/1 * * * * *");
        job1.setJobName("定时器1");
        job1.setMethodName("print1");
        job1.setStatus(1);

        Job job2 = new Job();
        job2.setClassName("com.example.demo.ScheduleTest");
        job2.setCron("*/2 * * * * *");
        job2.setJobName("定时器2");
        job2.setMethodName("print2");
        job2.setStatus(1);

        //添加定时任务
        ScheduleUtils.add(job1);
        ScheduleUtils.add(job2);
        Thread.sleep(10000);
        //关闭定时任务
        ScheduleUtils.cancel(job1);
    }

}
