package com.xujun.dao;

import com.xujun.model.JobInformation;
import com.xujun.model.PredictorInformation;
import com.xujun.model.mapper.BarSearchTypeByTimeInfo;
import com.xujun.model.mapper.LineLocationByTimeInfo;
import com.xujun.model.mapper.LineSearchTypeByTimeInfo;
import com.xujun.model.mapper.ScatterByAllInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MonitorMapper {


    @Select("select label,count(1) as count from predictorInformation where time > #{startTime} and time < #{endTime}" +
            " group by label")
    List<BarSearchTypeByTimeInfo> BarSearchTypeByTime(@Param("startTime") String startTime,
                                                      @Param("endTime") String endTime);

    @Select("select location,label,count(1) as count from predictorInformation " +
            "where time > #{startTime} and time < #{endTime} and label > 0" +
            " group by location,label")
    List<LineLocationByTimeInfo> LineLocationByTime(@Param("startTime") String startTime,
                                                    @Param("endTime") String endTime);

    @Select("select substr(time,1,#{interval}) as time,label,count(1) as count " +
            "from predictorInformation " +
            "where time > #{startTime} and time < #{endTime} and label > 0 " +
            "group by substr(time,1,#{interval}),label " +
            "order by time")
    List<LineSearchTypeByTimeInfo> LineSearchTypeByTime(@Param("startTime") String startTime,
                                                        @Param("endTime") String endTime, @Param("interval") Integer interval);

    @Select("select location,time,label " +
            "from predictorInformation " +
            "where time > #{startTime} and time < #{endTime}")
    List<ScatterByAllInfo> ScatterByAll(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
