package org.swdc.rmdisk.core.dav.locks;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.swdc.rmdisk.core.xmlns.XmlProperty;
import org.swdc.rmdisk.core.xmlns.XmlProvider;

public interface DLockScopes {

    DLockScopes SHARED = new LockScopeShared();
    DLockScopes EXCLUSIVE = new LockScopeExclusive();

    class LockScopeShared implements DLockScopes {

        @XmlProperty("D:shared")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    class LockScopeExclusive implements DLockScopes {

        @XmlProperty("D:exclusive")
        private String value;

        public String getValue() {
            return value;
        }

    }

    class LockScopeFactory implements XmlProvider {
        @Override
        public Object create(Element element) {
            Element exclude = element.element(
                    new QName("exclusive",new Namespace("D","DAV:"))
            );
            if (exclude == null) {
                return SHARED;
            }
            return EXCLUSIVE;
        }
    }

}
