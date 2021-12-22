package org.xbatis.spring.boot;

public interface ShardAlgorithm {

    String shardSuffix(Object... args);

}
