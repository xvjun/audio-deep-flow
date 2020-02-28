package com.xujun.controller;


import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import com.xujun.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/dataprocess")
public class DataController {

    /**
     * 1.直接浏览器上传数据
     * 2.传hdfs地址
     * 3.获得当前所有数据信息
     * 3.根据名称获得当前所有数据信息
     * 4.删除数据
     */

    @Autowired
    DataService dataService;


    /**
     * 本地data上传,
     * @param formatData
     * @return
     */
    @PostMapping("/local/data")
    public Result processFotmatData(@RequestParam("formatData") MultipartFile formatData) {
        if(formatData.isEmpty()){
            return Result.result(ResultCode.MISSING_AUDIO);
        }
        return dataService.processLocalFormatData(formatData);
    }

    /**
     * hdfs data上传,
     * @param hdfsUrl 所属标签
     * @return
     */
    @PostMapping("/hdfs/data")
    public Result processFormatData(@RequestParam("hdfsUrl") String hdfsUrl) {
        System.out.println(hdfsUrl);
        if(hdfsUrl.equals("")){
            return Result.result(ResultCode.MISSING_PARAMS);
        }
        return dataService.processHdfsFormatData(hdfsUrl);
    }

    @GetMapping("/data/list/page")
    public Result getDataPage(@RequestParam("displayName") String displayName,
                          @RequestParam("offset") Integer offset,
                          @RequestParam("limit") Integer limit){
        return dataService.getDataPage(displayName, offset, limit);
    }

    @DeleteMapping("/data")
    public Result deleteData(@RequestParam("dataId") Integer dataId){
        if(dataId == null){
            return Result.result(ResultCode.MISSING_PARAMS);
        }else{
            return dataService.deleteDataById(dataId);
        }
    }








}
