package org.xbatis.spring.boot;

public interface ShardAlgorithm {

    String shardSuffix(String... args);

}
