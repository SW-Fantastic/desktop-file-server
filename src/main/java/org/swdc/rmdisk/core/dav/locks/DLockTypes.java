package org.swdc.rmdisk.core.dav.locks;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.swdc.rmdisk.core.xmlns.XmlProperty;
import org.swdc.rmdisk.core.xmlns.XmlProvider;

public interface DLockTypes {

    DLockTypes WRITE_LOCK = new WriteLockType();

    class WriteLockType implements DLockTypes {
        @XmlProperty("D:write")
        private String write;

        public String getWrite() {
            return write;
        }

        public void setWrite(String write) {
            this.write = write;
        }
    }

    class LockTypeFactory implements XmlProvider {

        @Override
        public Object create(Element element) {
            Element target = element.element(
                    new QName("write",new Namespace("D","DAV:"))
            );
            if (target != null) {
                return new WriteLockType();
            }
            return null;
        }
    }

}
