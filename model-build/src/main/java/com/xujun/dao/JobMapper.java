package com.xujun.dao;


import com.xujun.model.JobInformation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface JobMapper {

//    private Integer jobId;
//    private String jobName;
//    private String startTime;
//    private Integer hiddenLayers;
//    private Integer layersSize;
//    private Float learningRate;
//    private Integer epochs;
//    private Float dropoutRate;
//    private Integer classSum;
//    private Long dataLength;
//    private Long time;
//    private Integer dataShardSum;
//    private Integer dataShardReadySum;
//    private Integer isCompleted;

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

}

//
//
//    @Update("update dataInformation set dataName=#{dataName}, length=#{length}, capacity=#{capacity}," +
//            "importTime=#{importTime},isCompleted=#{isCompleted} where hdfsPath= #{hdfsPath}")
//    Integer updateDataInfomationByHdfsUrl(DataInformation dataInformation);
//
//    @Update("update dataInformation set dataName=#{dataName},hdfsPath=#{hdfsPath}, length=#{length}, capacity=#{capacity}," +
//            "importTime=#{importTime},isCompleted=#{isCompleted} where dataId=#{dataId}")
//    Integer updateDataInfomationByDataId(DataInformation dataInformation);
//
//    @Select("select * from dataInformation")
//    List<DataInformation> selectDataInformationAll();
//
//    @Select("select * from dataInformation where dataName like concat('%',#{name},'%')")
//    List<DataInformation> selectDataInformationPage(String name);
//
//    @Select("select * from dataInformation where dataId=#{dataId}")
//    DataInformation selectDataInformationByDataId(@Param("dataId") Integer dataId);
//
//    @Delete("delete from dataInformation where dataId=#{dataId}")
//    Integer deleteDataInformationByDataId(@Param("dataId") Integer dataId);
