package com.sedwt.common.remote;

import com.sedwt.common.constant.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author : yj zhang
 * @since : 2021/8/26 13:47
 */
@Component
public class RemoteSystemHttp {
    @Autowired
    private RestTemplate restTemplate;

    public void delete(Long projectId) {
        String url = "http://" + ServiceConstants.SYSTEM_NAME + "/file/delete/project" +"?projectId=" + projectId;
        restTemplate.delete(url);
    }


}
