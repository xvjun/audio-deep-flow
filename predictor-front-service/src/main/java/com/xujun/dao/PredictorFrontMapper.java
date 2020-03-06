package com.xujun.dao;

import com.xujun.model.PredictorInformation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PredictorFrontMapper {

    @Insert("insert into predictorInformation(id,localtion,time,label) " +
            "value(#{id},#{localtion},#{time},#{label})")
    Integer addPredictorInformation(PredictorInformation predictorInformation);

    @Update("update predictorInformation set label=#{label} where" +
            " id=#{id} and localtion=#{localtion} and time=#{time}")
    Integer updatePredictorInformation(PredictorInformation predictorInformation);
}
