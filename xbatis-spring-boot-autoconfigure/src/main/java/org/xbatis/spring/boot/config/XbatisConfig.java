package org.xbatis.spring.boot.config;

import java.util.HashSet;
public final class XbatisConfig {

    private HashSet<XbatisNamespace> namespaces;

    public XbatisNamespace addNamespace(String name){
    }

    private class XbatisNamespace {
        private String name;
        private HashSet<XbatisGroup> groups;

        private class XbatisGroup {
            private String name;
            private HashSet<XbatisDatabase> masters;
            private HashSet<XbatisDatabase> slaves;
            private class XbatisDatabase{

            }
        }
    }
}