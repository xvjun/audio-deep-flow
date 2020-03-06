package com.xujun.dao;


import com.xujun.model.JobInformation;
import com.xujun.model.ModelInformation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ModelMapper {

    @Insert("insert into modelInformation(modelName,hdfsPath,tock,lossStr,accuracyStr,valLossStr,valAccuracyStr," +
            "accuracy,loss,valAccuracy,valLoss,completeTime,hiddenLayers,layersSize,learningRate,epochs,dropoutRate,classSum)" +
            " value(#{modelName},#{hdfsPath},#{tock},#{lossStr},#{accuracyStr},#{valLossStr},#{valAccuracyStr}," +
            "#{accuracy},#{loss},#{valAccuracy},#{valLoss},#{completeTime},#{hiddenLayers},#{layersSize}," +
            "#{learningRate},#{epochs},#{dropoutRate},#{classSum})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "modelId",keyColumn = "modelId",resultType = Integer.class,before = false)
    Integer addModelInfomation(ModelInformation modelInformation);

    @Update("update modelInformation set modelName=#{modelName},hdfsPath=#{hdfsPath}, tock=#{tock}, " +
            "lossStr=#{lossStr},accuracyStr=#{accuracyStr},valLossStr=#{valLossStr},valAccuracyStr=#{valAccuracyStr}," +
            "accuracy=#{accuracy},loss=#{loss},valAccuracy=#{valAccuracy},valLoss=#{valLoss}," +
            "completeTime=#{completeTime},hiddenLayers=#{hiddenLayers},layersSize=#{layersSize},learningRate=#{learningRate}" +
            ",epochs=#{epochs},dropoutRate=#{dropoutRate},classSum=#{classSum} where modelId=#{modelId}")
    Integer updateModelInfomationByModelId(ModelInformation modelInformation);

    @Select("select * from modelInformation where modelId=#{modelId}")
    ModelInformation selectModelInformationByModelId(@Param("modelId") Integer modelId);

    @Select("select * from modelInformation where modelName like concat('%',#{modelName},'%')")
    List<ModelInformation> selectModelInformationByModelNamePage(@Param("modelName") String modelName);

    @Delete("delete from modelInformation where modelId=#{modelId}")
    Integer deleteModelInformationByModelId(@Param("modelId") Integer modelId);


}