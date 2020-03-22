package com.xujun.controller;

import com.xujun.response.Result;
import com.xujun.service.MonitorService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monitor")
public class MoniterController {

    /**
     * id[String]
     * time[Long]
     * location[Long]
     * label[Integer]
     *
     *
     * 指定时间段，按类型统计数量，柱状图，饼图
     * 指定区间大小(分、时、天、月、年)，统计各区间各类型数量，折线图
     * 指定时间段，按location统计各类型数量，折线图
     * 横轴为location，纵轴为时间，散点图，颜色和大小区分
     *
     *
     */
    @Autowired
    private MonitorService monitorService;

    @GetMapping("/BarSearchTypeByTime")
    public Result BarSearchTypeByTime(@RequestParam("startTime") String startTime,
                                      @RequestParam("endTime") String endTime){
        return monitorService.BarSearchTypeByTime(startTime, endTime);
    }

    @GetMapping("/PieSearchTypeByTime")
    public Result PieSearchTypeByTime(@RequestParam("startTime") String startTime,
                                      @RequestParam("endTime") String endTime){
        return monitorService.PieSearchTypeByTime(startTime, endTime);
    }

    @GetMapping("/LineLocationByTime")
    public Result LineLocationByTime(@RequestParam("startTime") String startTime,
                                     @RequestParam("endTime") String endTime){
        return monitorService.LineLocationByTime(startTime, endTime);
    }


    @GetMapping("/LineSearchTypeByTime")
    public Result LineSearchTypeByTime(@RequestParam("startTime") String startTime,
                                       @RequestParam("endTime") String endTime,
                                       @RequestParam("interval") Integer interval){
        return monitorService.LineSearchTypeByTime(startTime, endTime, interval);
    }

    @GetMapping("/ScatterByAll")
    public Result ScatterByAll(@RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime){
        return monitorService.ScatterByAll(startTime, endTime);
    }




}
