package com.xujun.dao;

import com.xujun.model.PredictorInformation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PredictorFrontMapper {

    @Insert("insert into predictorInformation(location,time,label) " +
            "value(#{location},#{time},#{label})")
    Integer addPredictorInformation(PredictorInformation predictorInformation);

}
