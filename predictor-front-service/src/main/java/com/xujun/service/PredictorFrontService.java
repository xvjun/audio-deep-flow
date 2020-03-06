package com.xujun.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xujun.config.CommonEnvConfig;
import com.xujun.config.PredictorEnvConfig;
import com.xujun.dao.PredictorFrontMapper;
import com.xujun.kafka.provider.KafkaSender;
import com.xujun.model.PredictorInformation;
import com.xujun.model.req.Request;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class PredictorFrontService {

    private static Logger logger = LoggerFactory.getLogger(PredictorFrontService.class);

    private DateFormat df=new SimpleDateFormat(CommonEnvConfig.DATE_FORMAT);

    @Value("${predictor.url}")
    private String predictorUrl;

    @Autowired
    PredictorFrontMapper frontMapper;

    @Autowired
    KafkaSender sender;

    /**
     * 接收数据，解析格式，调用预估，写入mysql，返回预估结果
     * 1234,19850,1569439815273,[256]
     */
    public Result sendData(Request request, String type) {
        List<Double> featureList = request.getFeatureList();

        // http://localhost:8501/v1/models/saved_model:predict
        JSONObject json = new JSONObject();
        json.put("instances", featureList);
        JSONObject rs = HttpClient.HttpPost(predictorUrl, json);
        JSONArray labelList = null;
        try{
            labelList = rs.getJSONArray("predictions").getJSONArray(0);
        } catch(Exception e){
            e.printStackTrace();
            String message = rs.getString("error");
            logger.error(String.format("predictor error: %s", message));
            return Result.result(ResultCode.PREDICTOR_FAILED, message);
        }

        Integer label = 0;
        Double tplabel = -1.;
        for (int i = 0; i < labelList.size(); i++) {
            if(labelList.getDouble(i) > tplabel){
                label = i;
                tplabel = labelList.getDouble(i);
            }
        }

        String time = df.format(new Date(request.getTs()));
        PredictorInformation predictorInformation = new PredictorInformation();
        predictorInformation.setId(request.getId());
        predictorInformation.setLocaltion(request.getLocation());
        predictorInformation.setTime(time);
        predictorInformation.setLabel(label);

        Integer addCount = frontMapper.addPredictorInformation(predictorInformation);
        if(addCount > 0){
            logger.info("Add predictorInformation successfully");
        }else{
            logger.error("Add predictorInformation failed");
            return Result.result(ResultCode.UPLOAD_MYSQL_FAILED);
        }

        if(type.equals(PredictorEnvConfig.FROM_HTTP)){
            return Result.success(predictorInformation);
        }else if(type.equals(PredictorEnvConfig.FROM_KAFKA)){
            if (sender.send(predictorInformation)) {
                logger.info("kafka send rs successfully");
            }else{
                logger.error("kafka send rs failed");
                return Result.failure();
            }
        }
        return Result.success();
    }
}
