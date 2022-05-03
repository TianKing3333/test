package com.atguigu.yygh.task.scheduled;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: yygh_parent
 * @description:
 * @author: 江天赐
 * @create: 2022-04-29 20:26
 */
@Component
@EnableScheduling //开启定时任务操作
public class ScheduleTask {
    @Autowired
    private RabbitService rabbitService;
    //每天8点执行方法，就医提醒
    //cron表达式，设置执行间隔
    //0 0 8 * * ? 每天8点执行
    @Scheduled(cron="0 0 8 * * ?")//方便测试，每隔30s执行 0/30 * * * * ?
    public void taskPatient(){
        //调用rabbitService发送信息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
