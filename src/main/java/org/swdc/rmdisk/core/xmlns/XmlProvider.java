package org.swdc.rmdisk.core.xmlns;

import org.dom4j.Element;

public interface XmlProvider {

    Object create(Element element);

}
