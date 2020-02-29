package com.xujun.dao;


import com.xujun.model.ServingInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ServingMapper {
    @Insert("insert into servingInformation(servingId,servingName,cpu,memory,instance,modelLocalPath,isCompleted,startTime) " +
            "value(#{servingId},#{servingName},#{cpu},#{memory},#{instance},#{modelLocalPath}," +
            "#{isCompleted},#{startTime})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "servingId",keyColumn = "servingId",resultType = Integer.class,before = false)
    Integer addServingInformation(ServingInformation servingInformation);

    @Update("update servingInformation set servingId=#{servingId},servingName=#{servingName}, " +
            "cpu=#{cpu},memory=#{memory},instance=#{instance},modelLocalPath=#{modelLocalPath}," +
            "isCompleted=#{isCompleted},startTime=#{startTime} where servingId=#{servingId}")
    Integer updateServingInformationByServingId(ServingInformation servingInformation);

    @Select("select * from servingInformation where servingId=#{servingId}")
    ServingInformation selectServingInformationByServingId(@Param("servingId") Integer servingId);

    @Select("select * from servingInformation where servingName like concat('%',#{servingName},'%')")
    List<ServingInformation> selectServingInformationByServingNamePage(@Param("servingName") String servingName);

    @Delete("delete from servingInformation where servingId=#{servingId}")
    Integer deleteServingInformationByServingId(@Param("servingId") Integer servingId);
}
