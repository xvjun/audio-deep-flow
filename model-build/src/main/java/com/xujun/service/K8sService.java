package com.xujun.service;

import com.google.gson.JsonSyntaxException;
import com.xujun.response.Result;
import com.xujun.response.ResultCode;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.BatchV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class K8sService {
    private static BatchV1Api batchV1Api;
    private static CoreV1Api coreV1Api;

    @Value("${audio-deep-flow.k8s.namespace}")
    String k8sNamespace;

    @PostConstruct
    private void init() {
        ApiClient client = null;
        try {
            client = ClientBuilder.cluster().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration.setDefaultApiClient(client);
        batchV1Api = new BatchV1Api();
        coreV1Api = new CoreV1Api();
    }

    public V1Service getSvc(String svc) throws ApiException {
        return coreV1Api.readNamespacedService(svc, k8sNamespace, null, null, null);
    }

    public Optional<Result> createJob(String template) {
        V1Job job = Configuration.getDefaultApiClient().getJSON().deserialize(template, V1Job.class);
        try {
            batchV1Api.createNamespacedJob(k8sNamespace, job, null, null, null);
        } catch (ApiException e) {
            e.printStackTrace();
            return Optional.of(Result.result(ResultCode.CREATE_K8S_JOB_FAILED, job));
        }
        return Optional.empty();
    }

    public boolean deleteJob(String jobName) {
        V1DeleteOptions options = new V1DeleteOptions();
        V1Status status;
        try {
            status = batchV1Api.deleteNamespacedJob(jobName, k8sNamespace, null, options,
                    null, null, null, null);
        } catch (JsonSyntaxException jsonSyntaxException){
            return true;
        } catch (ApiException e) {
            e.printStackTrace();
            return false;
        }
        return status.getStatus().equals("Success");
    }

    public boolean deletePod(String PodName){
        V1DeleteOptions options = new V1DeleteOptions();
        V1Status status;
        try{
            status = coreV1Api.deleteNamespacedPod(PodName,k8sNamespace,null,options,null,null,null,null);
        } catch (JsonSyntaxException jsonSyntaxException){
            return true;
        } catch (ApiException e) {
            e.printStackTrace();
            return false;
        }
        return status.getStatus().equals("Success");
    }

    public List<String> getPodNames() throws ApiException{
        List<String> podNames = new ArrayList<>();
        V1PodList pods = coreV1Api.listNamespacedPod(k8sNamespace, null,
                null, null, null, null, null,
                null, null, null);
        for (V1Pod pod : pods.getItems()) {
            podNames.add(pod.getMetadata().getName());
        }
        return podNames;
    }

    public String getPodLog(String podName) throws ApiException{
        return coreV1Api.readNamespacedPodLog(podName, k8sNamespace, null, null,
                null, null, null, null, null, null);
    }

}
