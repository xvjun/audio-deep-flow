package com.xujun.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Component {
    private List<DeploymentInformation> deploymentInformationList;
    private List<JobInformation> jobInformationList;
    private List<PodInformation> podInformationList;
    private List<ServiceInformation> serviceInformationList;
}
