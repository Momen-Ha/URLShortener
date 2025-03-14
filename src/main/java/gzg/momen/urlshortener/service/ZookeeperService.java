package gzg.momen.urlshortener.service;


import gzg.momen.urlshortener.constants.ZkConstants;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZookeeperService implements iZookeeperService {

    private final ZkClient zkClient;
    private final ZkConstants zkConstants;

    @Value("${spring.application.name}")
    private String applicationName;

    public ZookeeperService(ZkClient zkClient, ZkConstants zkConstants) {
        this.zkClient = zkClient;
        this.zkConstants = zkConstants;
    }

    @Override
    public List<String> getChildrenNodes() {
        return zkClient.getChildren(zkConstants.getNodePath());
    }

    @Override
    public void createNodeInCounter() {
        if (!zkClient.exists(zkConstants.getNodePath())) {
            zkClient.create(zkConstants.getNodePath(), "parent node", CreateMode.PERSISTENT);
        }


        String createdNode = zkClient.create(zkConstants.getNodePath().concat("/")
                        .concat(applicationName),
                "child data", CreateMode.PERSISTENT_SEQUENTIAL);


    }

    @Override
    public String getNodeData(String path) {
        return zkClient.readData(path, null);
    }

}
