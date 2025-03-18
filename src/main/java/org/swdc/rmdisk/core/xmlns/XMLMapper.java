package org.swdc.rmdisk.core.xmlns;

import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.swdc.ours.common.type.ClassTypeAndMethods;
import org.swdc.ours.common.type.Converter;
import org.swdc.ours.common.type.Converters;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XMLMapper {

    private static final Converters converters = new Converters();

    private static final ConcurrentHashMap<Class, XmlProvider> xmlProviders = new ConcurrentHashMap<>();

    /**
     * 将Object转换为XML字符串
     * @param object 对象
     * @return XML字符串
     */
    public static String writeObjectAsString(Object object) {
        DocumentFactory factory = DocumentFactory.getInstance();

        Element rootElement = null;
        XmlProperty property = object.getClass().getAnnotation(XmlProperty.class);
        if(property != null) {
            rootElement = factory.createElement(property.value());
        } else {
            rootElement = factory.createElement(object.getClass().getSimpleName());
        }

        reverseWriteProperty(rootElement, object);

        return rootElement.asXML();
    }


    public static <T> T readObjectFromString(Class<T> clazz, String xml) {

        try {
            Element root = DocumentHelper.parseText(xml)
                    .getRootElement();

            String name = null;
            XmlProperty currentProp = clazz.getAnnotation(XmlProperty.class);
            if (currentProp != null) {
                name = currentProp.value();
            } else {
                name = clazz.getSimpleName();
            }
            if (!root.getQualifiedName().equals(name)) {
                return null;
            }

            return reverseReadProperty(root, clazz);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 从XML元素中反向读取属性并创建Java对象。
     *
     * @param current     当前处理的XML元素。
     * @param objectType  需要创建的目标Java类的类型。
     * @param <T>         目标Java类的类型。
     * @return            根据XML元素创建并填充的Java对象。
     * @throws NoSuchMethodException     如果找不到构造方法。
     * @throws InvocationTargetException 如果反射调用方法时发生异常。
     * @throws InstantiationException    如果无法实例化对象。
     * @throws IllegalAccessException    如果无法访问指定对象。
     */
    public static <T> T reverseReadProperty(Element current, Class<T> objectType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (objectType == null) {
            // 空对象不处理
            return null;
        }

        if (ClassTypeAndMethods.isBasicType(objectType) || ClassTypeAndMethods.isBoxedType(objectType) || objectType == String.class) {
            // 基本类型或String，直接读取文本并转换
            if (objectType == String.class) {
                return (T)current.getText();
            } else {
                Converter converter = converters.getConverter(String.class, objectType);
                return (T)converter.convert(current.getText());
            }
        } else if (ClassTypeAndMethods.isCollectionType(objectType)) {
            throw new UnsupportedOperationException("集合类型不支持直接读取，请使用集合的元素类型作为目标类型。");
        }

        Object object = objectType.getConstructor()
                .newInstance();

        List<Field> fieldList = ClassTypeAndMethods.findAllFields(objectType);
        for (Field field : fieldList) {
            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                // 静态属性不处理
                continue;
            }

            try {
                XmlProperty fieldProp = field.getAnnotation(XmlProperty.class);
                String tagName = null;
                if (fieldProp != null) {
                    tagName = fieldProp.value();
                    if (fieldProp.tagProperty()) {
                        // 是标签的属性，读取当前节点的属性
                        String attr = current.attributeValue(tagName);
                        if (attr == null) {
                            continue;
                        }
                        if (field.getType() == String.class) {
                            field.set(object, attr);
                        } else if (ClassTypeAndMethods.isBasicType(field.getType()) || ClassTypeAndMethods.isBoxedType(field.getType())) {
                            Converter converter = converters.getConverter(String.class, field.getType());
                            field.set(object, converter.convert(attr));
                        }
                        // 复杂类型不能作为标签的属性，忽略即可。
                        continue;
                    }
                } else {
                    tagName = field.getName();
                }

                field.setAccessible(true);

                if (field.getType() == String.class || ClassTypeAndMethods.isBasicType(field.getType()) || ClassTypeAndMethods.isBoxedType(field.getType())) {
                    // 基本类型或String，直接读取文本并转换
                    Element value = current.element(getTagQueryName(tagName,field,current));
                    if (value == null) {
                        continue;
                    }
                    if (field.getType() == String.class) {
                        field.set(object, value.getText());
                    } else {
                        Converter converter = converters.getConverter(String.class, field.getType());
                        field.set(object, converter.convert(value.getText()));
                    }
                } else if (ClassTypeAndMethods.isCollectionType(field.getType())) {
                    // 集合类型，特殊处理
                    boolean wrapCollection = true;
                    if (fieldProp != null) {
                        wrapCollection = fieldProp.wrapCollection();
                    }
                    Object fieldValue = reverseReadCollection(
                            current,
                            field.getType(),
                            ClassTypeAndMethods.getFieldParameters(field),
                            tagName,
                            wrapCollection
                    );
                    field.set(object, fieldValue);
                } else {
                    Element value = current.element(getTagQueryName(tagName,field,current));
                    if (value == null) {
                        continue;
                    }
                    XmlFactory xmlFactory = field.getAnnotation(XmlFactory.class);
                    if(xmlFactory != null) {
                        XmlProvider provider;
                        if (xmlProviders.contains(xmlFactory.value())) {
                            provider = xmlProviders.get(xmlFactory.value());
                        } else {
                            provider = xmlFactory.value().newInstance();
                            xmlProviders.put(xmlFactory.value(), provider);
                        }
                        Object fieldValue = provider.create(value);
                        field.set(object, fieldValue);
                    } else {
                        Object fieldValue = reverseReadProperty(value, field.getType());
                        field.set(object, fieldValue);
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (T)object;
    }


    /**
     * 从XML元素中反向读取集合类型的对象。
     *
     * @param current         当前处理的XML元素。
     * @param type            需要读取的集合类型。
     * @param parameterTypes  集合元素的类型参数列表。
     * @param tagName         用于定位集合元素的标签名。
     * @param wrapCollection  是否将集合包装在另一个元素中。
     * @return                反序列化后的集合对象。
     * @throws InvocationTargetException 如果在反射调用方法时发生异常。
     * @throws NoSuchMethodException     如果找不到指定的方法。
     * @throws InstantiationException    如果无法实例化对象。
     * @throws IllegalAccessException    如果无法访问指定的类或方法。
     */
    private static Object reverseReadCollection(Element current, Class type, List<Class> parameterTypes, String tagName, boolean wrapCollection) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 集合类型，特殊处理
        if (Map.class.isAssignableFrom(type)) {
            // Map类型，特殊处理
            current = current.element(getTagQueryName(tagName,type, current));
            if (current == null || parameterTypes.size() < 2) {
                return Collections.emptyMap();
            }
            Class keyType = parameterTypes.get(0);
            Class valueType = parameterTypes.get(1);
            XmlProperty fieldProp = (XmlProperty) valueType.getAnnotation(XmlProperty.class);
            String itemTagName = null;
            if (fieldProp != null) {
                itemTagName = fieldProp.value();
            } else {
                itemTagName = valueType.getSimpleName();
            }
            Converter keyConverter = null;
            if (keyType == String.class || ClassTypeAndMethods.isBasicType(keyType) || ClassTypeAndMethods.isBoxedType(keyType)) {
                if (keyType == String.class) {
                    keyConverter = (s) -> (String)s;
                } else {
                    keyConverter = converters.getConverter(String.class, keyType);
                }
            } else {
                return Collections.emptyMap();
            }
            Map<Object, Object> objects = new HashMap<>();
            List<Element> elements = current.elements();
            if (elements.isEmpty()) {
                return Collections.emptyMap();
            }
            for (Element element : elements) {
                String tag = element.getQualifiedName();
                Object key = keyConverter.convert(tag);
                if (fieldProp != null && fieldProp.wrapCollection()) {
                    element = element.element(getTagQueryName(itemTagName,valueType, current));
                }
                Object value = reverseReadProperty(element, valueType);
                objects.put(key, value);
            }
            return objects;
        } else {
            // List类型，特殊处理
            Class elementType = parameterTypes.get(0);
            List<Object> objects = new ArrayList<>();
            if (wrapCollection) {
                current = current.element(getTagQueryName(tagName,type, current));
            }

            String elementTagName = null;
            XmlProperty fieldProp = (XmlProperty) elementType.getAnnotation(XmlProperty.class);

            if (fieldProp != null ) {
                elementTagName = fieldProp.value();
            } else {
                elementTagName = elementType.getSimpleName();
            }

            List<Element> elements = current.elements(getTagQueryName(elementTagName,elementType, current));
            if (elements.isEmpty()) {
                return Collections.emptyList();
            }

            if (elementType == String.class || ClassTypeAndMethods.isBasicType(elementType) || ClassTypeAndMethods.isBoxedType(elementType)) {
                if (elementType == String.class) {
                    for (Element element : elements) {
                        objects.add(element.getText());
                    }
                    return objects;
                } else {
                    Converter converter = converters.getConverter(String.class, elementType);
                    for (Element element : elements) {
                        objects.add(converter.convert(element.getText()));
                    }
                    return objects;
                }
            } else {
                for (Element element : elements) {
                    Object fieldValue = reverseReadProperty(element, elementType);
                    objects.add(fieldValue);
                }
                return objects;
            }
        }
    }

    /**
     * 根据提供的标签名、注解元素和XML元素获取QName对象。
     *
     * @param qTagName          带前缀的标签名，如 "ns:tagName"。
     * @param annotatedElement  包含XML属性注解的Java元素。
     * @param element           XML元素对象，用于查找命名空间。
     * @return                  根据给定的标签名和命名空间生成的QName对象。
     */
    private static QName getTagQueryName(String qTagName,AnnotatedElement annotatedElement, Element element) {
        if (qTagName.contains(":")) {
            String prefix = qTagName.substring(0, qTagName.indexOf(":"));
            String realTagName = qTagName.substring(qTagName.indexOf(":") + 1);;
            org.dom4j.Namespace domNs = element.getNamespaceForPrefix(prefix);
            if (domNs != null) {
                return new QName(realTagName, domNs);
            }
            for (Namespace ns : annotatedElement.getAnnotation(XmlProperty.class).namespace()) {
                if (ns.prefix().equals(prefix)) {
                    return new QName(realTagName, new org.dom4j.Namespace(ns.prefix(), ns.uri()));
                }
            }
            return new QName(realTagName);
        } else {
            return new QName(qTagName);
        }
    }


    /**
     * 写入Object的各个属性
     * @param current 当前节点
     * @param object   对象
     */
    private static void reverseWriteProperty(Element current, Object object) {

        if (object == null) {
            // 空对象不处理
            return;
        }

        XmlProperty currentProp = object.getClass().getAnnotation(XmlProperty.class);
        if(currentProp != null) {
            // Object存在注解，此时Tag已经被上一层递归创建，所以只需要添加namespace
            for(Namespace ns : currentProp.namespace()) {
                if (ns.prefix().isBlank() && ns.uri().isBlank()) {
                    continue;
                }
                current.addNamespace(ns.prefix(), ns.uri());
            }
        }

        if (ClassTypeAndMethods.isBasicType(object.getClass()) || ClassTypeAndMethods.isBoxedType(object.getClass()) || object instanceof String) {
            // 基本类型或String，直接写入
            if (object.getClass() == String.class) {
                current.setText((String) object);
            } else {
                Converter converter = converters.getConverter(object.getClass(), String.class);
                current.setText((String) converter.convert(object));
            }
            return;
        } else if (ClassTypeAndMethods.isCollectionType(object.getClass())) {
            // 集合类型，特殊处理
            reverseWriteCollection( object, current);
            return;
        }

        // 普通对象，递归写入属性

        List<Field> fieldList = ClassTypeAndMethods.findAllFields(object.getClass());
        for(Field field : fieldList) {

            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                // 静态属性不处理
                continue;
            }

            try {

                Boolean isBasicType = ClassTypeAndMethods.isBasicType(field.getType()) || ClassTypeAndMethods.isBoxedType(field.getType()) || field.getType() == String.class;
                XmlProperty property = field.getAnnotation(XmlProperty.class);

                boolean ignoreOnEmpty = true;

                String name = null;
                // 获取Tag名称，如果没有注解则使用字段名
                if (property == null) {
                    name = field.getName();
                } else {
                    ignoreOnEmpty = property.ignoreEmptyValue();
                    name = property.value();
                }

                field.setAccessible(true);
                Object value = field.get(object);
                if (value == null && ignoreOnEmpty) {
                    continue;
                }

                if(property != null ) {
                    // 有注解，根据tagProperty属性决定是写入属性还是子元素
                    if (property.tagProperty()) {
                        // 写入Tag属性，只能是基本类型或String
                        if (!isBasicType) {
                            throw new IllegalArgumentException("Tag property must be a basic type or String");
                        }

                        if (value.getClass() == String.class) {
                            current.addAttribute(name, (String) value);
                        } else {
                            Converter converter = converters.getConverter(field.getType(), String.class);
                            current.addAttribute(name, (String) converter.convert(value));
                        }

                    } else {
                        // 写入子元素，可以是基本类型、String或Object
                        Element child = null;
                        if(ClassTypeAndMethods.isCollectionType(field.getType())) {
                            // 集合类型，特殊处理
                            if (Map.class.isAssignableFrom(field.getType())) {
                                // Map类型，额外追加一层tag，无视wrapCollection属性
                                child = current.addElement(name);
                            } else {
                                // 其他集合类型，根据wrapCollection属性决定是否包裹一层tag
                                if (property.wrapCollection()) {
                                    child = current.addElement(name);
                                } else {
                                    child = current;
                                }
                            }
                            // 递归写入集合元素
                            reverseWriteCollection(value, child);
                        } else {
                            // 由于没有默认生成tag，所以在这里生成该属性的tag。
                            child = current.addElement(name);
                            // 递归写入子元素
                            reverseWriteProperty(child, value);
                        }

                    }
                } else {
                    // 没有注解，默认包裹一层tag
                    Element child = current.addElement(name);
                    if (isBasicType) {
                        // 基本类型直接写入文本
                        if (value.getClass() == String.class) {
                            child.setText((String) value);
                        } else {
                            Converter converter = converters.getConverter(field.getType(), String.class);
                            child.setText((String) converter.convert(value));
                        }
                    } else {
                        // Object类型，递归写入子元素
                        Element item = null;
                        if(ClassTypeAndMethods.isCollectionType(field.getType())) {
                            // 集合类型，特殊处理
                            if (Map.class.isAssignableFrom(field.getType())) {
                                // Map类型，额外追加一层tag，无视wrapCollection属性
                                item = child.addElement(name);
                            } else {
                                // 其他集合类型，根据wrapCollection属性决定是否包裹一层tag
                                if (property.wrapCollection()) {
                                    item = child.addElement(name);
                                } else {
                                    item = child;
                                }
                            }
                            // 递归写入集合元素
                            reverseWriteCollection(value, item);
                        } else {
                            // 由于默认生成了tag，直接递归写入子元素
                            reverseWriteProperty(child, value);
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 递归写入集合元素
     * @param value 集合对象
     * @param current 当前Tag
     */
    private static void reverseWriteCollection(Object value, Element current) {
        if (Map.class.isAssignableFrom(value.getClass())) {
            Map map = (Map) value;
            for (Object key: map.keySet()) {
                if (key.getClass() != String.class && !ClassTypeAndMethods.isBoxedType(key.getClass()) && !ClassTypeAndMethods.isBasicType(key.getClass())) {
                    throw new IllegalArgumentException("Map key must be a String or basic type");
                }
                Converter converter = converters.getConverter(key.getClass(), String.class);
                Element keyElement = current.addElement((String) converter.convert(key));
                Object valueOfKey = map.get(key);
                if (valueOfKey.getClass() != String.class && !ClassTypeAndMethods.isBoxedType(valueOfKey.getClass()) && !ClassTypeAndMethods.isBasicType(valueOfKey.getClass())) {
                    Element valueElement = null;
                    XmlProperty property = valueOfKey.getClass().getAnnotation(XmlProperty.class);
                    if (property != null ) {
                        valueElement = property.wrapCollection() ?
                                keyElement.addElement(property.value()) : keyElement;
                    } else {
                        valueElement = keyElement.addElement(valueOfKey.getClass().getSimpleName());
                    }
                    reverseWriteProperty(valueElement, valueOfKey);
                } else {
                    if (valueOfKey.getClass() == String.class) {
                        keyElement.setText((String) valueOfKey);
                    } else {
                        converter = converters.getConverter(valueOfKey.getClass(), String.class);
                        keyElement.setText((String) converter.convert(valueOfKey));
                    }
                }
            }
        } else {
            Iterable iterable = (Iterable) value;
            for (Object item : iterable) {
                Element itemElement = null;

                if (item.getClass() != String.class && !ClassTypeAndMethods.isBoxedType(item.getClass()) && !ClassTypeAndMethods.isBasicType(item.getClass())) {
                    XmlProperty property = item.getClass().getAnnotation(XmlProperty.class);
                    if (property != null) {
                        itemElement = current.addElement(property.value());
                    } else {
                        itemElement = current.addElement(item.getClass().getSimpleName());
                    }
                    reverseWriteProperty(itemElement, item);
                } else {
                    itemElement = current.addElement(item.getClass().getSimpleName());
                    if (item.getClass() == String.class) {
                        itemElement.setText((String) item);
                    } else {
                        Converter converter = converters.getConverter(item.getClass(), String.class);
                        itemElement.setText((String) converter.convert(item));
                    }
                }
            }
        }
    }

}
