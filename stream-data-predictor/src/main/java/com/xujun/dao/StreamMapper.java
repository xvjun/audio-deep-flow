package com.xujun.dao;


import com.xujun.model.ModelInformation;
import com.xujun.model.StreamInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StreamMapper {

    @Insert("insert into streamInformation(streamName,startTime,servingName,cpu,memory,instance,kafkaAddress,receiverTopics,sendTopics,nodePort)" +
            " value(#{streamName},#{startTime},#{servingName},#{cpu},#{memory},#{instance},#{kafkaAddress},#{receiverTopics},#{sendTopics},#{nodePort})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "streamId",keyColumn = "streamId",resultType = Integer.class,before = false)
    Integer addStreamInfomation(StreamInformation streamInformation);

    @Update("update streamInformation set streamName=#{streamName},startTime=#{startTime}, servingName=#{servingName}, " +
            "cpu=#{cpu},memory=#{memory},instance=#{instance},kafkaAddress=#{kafkaAddress},receiverTopics=#{receiverTopics}," +
            "sendTopics=#{sendTopics},nodePort=#{nodePort}" +
            " where streamId=#{streamId}")
    Integer updateStreamInfomationByStreamId(StreamInformation streamInformation);

    @Select("select * from streamInformation where streamId=#{streamId}")
    StreamInformation selectStreamInformationByStreamId(@Param("streamId") Integer streamId);

    @Select("select * from streamInformation where streamName like concat('%',#{streamName},'%')")
    List<StreamInformation> selectStreamInformationByStreamNamePage(@Param("streamName") String streamName);

    @Delete("delete from streamInformation where streamId=#{streamId}")
    Integer deleteStreamInformationByStreamId(@Param("streamId") Integer streamId);

}
