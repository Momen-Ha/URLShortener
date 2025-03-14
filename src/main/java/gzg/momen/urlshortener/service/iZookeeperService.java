package gzg.momen.urlshortener.service;

import java.util.List;

public interface iZookeeperService {
    public List<String> getChildrenNodes();
    public void createNodeInCounter();
    public String getNodeData(String path);
}
