package org.xbatis.spring.boot.autoconfigure;

import java.util.Map;

public interface ShardAlgorithm {

    String shardSuffix(Map<String,Object> args);

}
