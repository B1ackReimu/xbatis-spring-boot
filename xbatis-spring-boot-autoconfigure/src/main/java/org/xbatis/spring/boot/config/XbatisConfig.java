package org.xbatis.spring.boot.config;

import java.util.HashSet;
public final class XbatisConfig {

    private final HashSet<XbatisNamespace> namespaces = new HashSet<>();

    public XbatisNamespace addNamespace(String name){
        XbatisNamespace xbatisNamespace = new XbatisNamespace(name);
        namespaces.add(xbatisNamespace);
        return xbatisNamespace;
    }

    public static class XbatisNamespace {
        private String name;
        private HashSet<XbatisGroup> groups;

        public XbatisNamespace(String name) {
            this.name = name;
        }

        public XbatisGroup addGroup(String name){
            XbatisGroup xbatisGroup = new XbatisGroup(name);
            groups.add(xbatisGroup);
            return xbatisGroup;
        }

        public static class XbatisGroup {
            private String name;
            private HashSet<XbatisDatabase> masters;
            private HashSet<XbatisDatabase> slaves;

            public XbatisGroup(String name) {
                this.name = name;
            }

            public XbatisDatabase addDatabase(){
                return null;
            }

            private class XbatisDatabase{

            }
        }
    }
}