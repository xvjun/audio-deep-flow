package com.xujun.dao;


import com.xujun.model.JobInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface JobMapper {


    @Insert("insert into jobInformation(jobName,startTime,hiddenLayers,layersSize,learningRate,epochs,dropoutRate," +
            "classSum,dataLength,time,dataShardSum,dataShardReadySum,isCompleted,rootPath,cpu,memory)" +
            " value(#{jobName},#{startTime},#{hiddenLayers},#{layersSize},#{learningRate},#{epochs},#{dropoutRate}," +
            "#{classSum},#{dataLength},#{time},#{dataShardSum},#{dataShardReadySum},#{isCompleted},#{rootPath}," +
            "#{cpu},#{memory})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "jobId",keyColumn = "jobId",resultType = Integer.class,before = false)
    Integer addJobInfomation(JobInformation jobInformation);

    @Update("update jobInformation set jobName=#{jobName},startTime=#{startTime}, hiddenLayers=#{hiddenLayers}, " +
            "layersSize=#{layersSize},learningRate=#{learningRate},epochs=#{epochs},dropoutRate=#{dropoutRate}," +
            "classSum=#{classSum},dataLength=#{dataLength},time=#{time},dataShardSum=#{dataShardSum}," +
            "dataShardReadySum=#{dataShardReadySum},isCompleted=#{isCompleted},rootPath=#{rootPath},cpu=#{cpu}" +
            ",memory=#{memory} where jobId=#{jobId}")
    Integer updateJobInfomationByJobId(JobInformation jobInformation);

    @Select("select * from jobInformation where jobId=#{jobId}")
    JobInformation selectJobInformationByJobId(@Param("jobId") Integer jobId);

    @Select("select * from jobInformation where jobName like concat('%',#{jobName},'%')")
    List<JobInformation> selectJobInformationByJobNamePage(@Param("jobName") String jobName);

    @Delete("delete from jobInformation where jobId=#{jobId}")
    Integer deleteJobInformationByJobId(@Param("jobId") Integer jobId);


}


