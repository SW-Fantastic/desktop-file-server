package org.swdc.rmdisk.core.dav.multiple;


import org.swdc.rmdisk.core.xmlns.XmlFactory;
import org.swdc.rmdisk.core.xmlns.XmlProperty;

public class DProperties {

    /**
     * 文件夹或文件的名称，例如：folder1, file.txt等。
     * Windows会使用路径而不是这里的名称，
     * 标准的WebDAV客户端应该使用该名称来显示文件夹或文件的名称。
     */
    @XmlProperty("D:displayname")
    private String displayName;

    /**
     * 资源的类型，例如：文件夹或文件，
     * 文件夹的时候填写固定的Collection类型，文件的时候该项留空
     */
    @XmlProperty("D:resourcetype")
    @XmlFactory(DResourceType.ResourceTypeFactory.class)
    private DResourceType resourceType;

    /**
     * 使用ISO-8601日期时间表示法，格式如下：
     * yyyy-MM-ddTHH:mm:ss
     * 这样的格式后续一般需要加上时区（+HH:mm表示东部时区，-HH:mm表示西部时区，Z表示UTC标准时间）
     * 例如：1970-01-01T00:00:00Z，表示UTC标准时间下的1970年1月1日0时0分0秒。
     *
     * 在Java中，可以使用java.time包下的LocalDateTime类来解析和生成这样的日期时间字符串，
     * 使用DateTimeFormatter.ISO_OFFSET_DATE_TIME可以得到该格式的日期时间字符串。
     *
     */
    @XmlProperty("D:creationdate")
    private String creationDate;

    /**
     * 使用特定的日期时间表示法，格式如下：
     * DateTimeFormatter.ofPattern("EEE, dd MMM yyyy hh:mm:ss zzz", Locale.ENGLISH)
     */
    @XmlProperty("D:getlastmodified")
    private String lastModified;

    /**
     * 字节为单位，例如：1024表示1KB。
     */
    @XmlProperty("D:getcontentlength")
    private Long contentLength;

    /**
     * 该文件的Mime类型，文件夹不需要填写
     * 例如：text/plain, application/xml等。
     */
    @XmlProperty("D:getcontenttype")
    private String contentType;

    /**
     * 用户的可用空间
     * 字节为单位，例如：1024表示1KB。
     */
    @XmlProperty("D:quota-available-bytes")
    private Long availableBytes;

    /**
     * 用户已使用的空间
     * 字节为单位，例如：1024表示1KB。
     */
    @XmlProperty("D:quota-used-bytes")
    private Long usedBytes;

    public Long getUsedBytes() {
        return usedBytes;
    }

    public void setUsedBytes(Long usedBytes) {
        this.usedBytes = usedBytes;
    }

    public Long getAvailableBytes() {
        return availableBytes;
    }

    public void setAvailableBytes(Long availableBytes) {
        this.availableBytes = availableBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setResourceType(DResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public DResourceType getResourceType() {
        return resourceType;
    }


    public String getDisplayName() {
        return displayName;
    }
}
