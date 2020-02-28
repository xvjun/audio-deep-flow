package com.xujun.dao;

import com.xujun.model.DataInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DataMapper {

    @Insert("insert into dataInformation(dataName,hdfsPath,length,capacity,importTime,isCompleted)" +
            " value(#{dataName},#{hdfsPath},#{length},#{capacity},#{importTime},#{isCompleted})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "dataId",keyColumn = "dataId",resultType = Integer.class,before = false)
    Integer addDataInfomation(DataInformation dataInformation);

    @Update("update dataInformation set dataName=#{dataName}, length=#{length}, capacity=#{capacity}," +
            "importTime=#{importTime},isCompleted=#{isCompleted} where hdfsPath= #{hdfsPath}")
    Integer updateDataInfomationByHdfsUrl(DataInformation dataInformation);

    @Update("update dataInformation set dataName=#{dataName},hdfsPath=#{hdfsPath}, length=#{length}, capacity=#{capacity}," +
            "importTime=#{importTime},isCompleted=#{isCompleted} where dataId=#{dataId}")
    Integer updateDataInfomationByDataId(DataInformation dataInformation);

    @Select("select * from dataInformation")
    List<DataInformation> selectDataInformationAll();

    @Select("select * from dataInformation where dataName like concat('%',#{name},'%')")
    List<DataInformation> selectDataInformationPage(String name);

    @Select("select * from dataInformation where dataId=#{dataId}")
    DataInformation selectDataInformationByDataId(@Param("dataId") Integer dataId);

    @Delete("delete from dataInformation where dataId=#{dataId}")
    Integer deleteDataInformationByDataId(@Param("dataId") Integer dataId);

}

