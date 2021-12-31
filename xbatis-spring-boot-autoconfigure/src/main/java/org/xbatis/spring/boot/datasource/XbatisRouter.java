package org.xbatis.spring.boot.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class XbatisRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }


}
