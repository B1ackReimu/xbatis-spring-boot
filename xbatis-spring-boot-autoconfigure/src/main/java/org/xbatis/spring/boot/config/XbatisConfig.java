package org.xbatis.spring.boot.config;

import java.util.HashSet;
public final class XbatisConfig {

    private final HashSet<XbatisNamespace> namespaces = new HashSet<>();

    public XbatisNamespace addNamespace(String name){
        XbatisNamespace xbatisNamespace = new XbatisNamespace(name);
        namespaces.add(xbatisNamespace);
        return xbatisNamespace;
    }

    private class XbatisNamespace {
        private String name;
        private HashSet<XbatisGroup> groups;

        public XbatisNamespace(String name) {
            this.name = name;
        }

        public XbatisGroup addGroup(String name){
            XbatisGroup xbatisGroup = new XbatisGroup();
            groups.add(xbatisGroup);
            return xbatisGroup;
        }

        private class XbatisGroup {
            private String name;
            private HashSet<XbatisDatabase> masters;
            private HashSet<XbatisDatabase> slaves;
            private class XbatisDatabase{

            }
        }
    }
}