package gzg.momen.urlshortener.utils;

import gzg.momen.urlshortener.constants.ZkConstants;
import gzg.momen.urlshortener.service.ZookeeperService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZookeeperUtility {

    private Long start = 0L;
    private Long end = 0L;
    private Long currentCounter = -1L;

    @Value("${spring.application.name}")
    private String applicationName;

    private final ZkConstants zkConstants;
    private final ZookeeperService zookeeperService;

    public ZookeeperUtility(ZkConstants zkConstants, ZookeeperService zookeeperService) {
        this.zkConstants = zkConstants;
        this.zookeeperService = zookeeperService;
    }

    @PostConstruct
    public void initializeNode() throws KeeperException.NoNodeException {
        log.info("Initializing new Zookeeper node..");
       
        zookeeperService.createNodeInCounter();

        List<String> childNodes = zookeeperService.getChildrenNodes().stream()
                .filter(s -> s.startsWith(applicationName))
                .sorted((s1, s2) -> s2.compareTo(s1))
                .collect(Collectors.toList());

        if(childNodes.isEmpty()) {
            throw new KeeperException.NoNodeException("Empty Zookeeper node.");
        }

        String latestValue = childNodes.get(0);
        long sequenceNumber = Long.parseLong(latestValue.replaceAll("[^\\d]", ""));
        start = sequenceNumber * zkConstants.getRangeLength();
        end = start + zkConstants.getRangeLength();
        currentCounter = start;

        log.info("Start: {} | Current: {} | End: {}", start, currentCounter, end);
    }

    public synchronized Long getNextCount() throws KeeperException.NoNodeException {
        if (currentCounter > end) {
            initializeNode();
        }
        return currentCounter++;
    }

}