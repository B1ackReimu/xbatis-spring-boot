package org.xbatis.spring.boot.autoconfigure;

public interface ShardAlgorithm {

    String shardSuffix(String... args);

}
