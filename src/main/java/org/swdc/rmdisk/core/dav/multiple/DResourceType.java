package org.swdc.rmdisk.core.dav.multiple;


import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.swdc.rmdisk.core.xmlns.XmlProperty;
import org.swdc.rmdisk.core.xmlns.XmlProvider;

public interface DResourceType {

     Collection COLLECTION = new Collection();

     class Collection implements DResourceType {

        @XmlProperty(
                value = "D:collection",
                ignoreEmptyValue = false
        )
        private String collection;

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

    }

    static boolean isCollection(DResourceType resourceType) {
        return (resourceType instanceof Collection);
    }

    class ResourceTypeFactory implements XmlProvider {

        @Override
        public Object create(Element element) {
            Element target = element.element(
                    new QName("collection", new Namespace("D", "DAV:"))
            );
            if (target != null){
                return COLLECTION;
            }
            return null;
        }
    }

}
