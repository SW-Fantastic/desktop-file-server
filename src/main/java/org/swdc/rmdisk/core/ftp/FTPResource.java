package org.swdc.rmdisk.core.ftp;

public class FTPResource {

    private String group;

    private String name;

    private String lastModified;

    private Long size;

    private String owner;

    private boolean isDirectory;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 序列化为List指令的目录项
     * @return FTP LIST指令的目录项格式字符串
     */
    public String asFTPFileListEntry() {
        String perms = "rw-------";
        return String.format("%c%s   %d %s %s %s %s %s\r\n", (isDirectory ? 'd': '-'),perms, 1,
                "user", "group",(isDirectory ? 0 : getSize()), lastModified, getName());
    }

    /**
     * 序列化为MLST指令的目录项
     * @param absoluteParentPath 父目录的绝对路径，通过PathAccessor可以获得。
     * @return FTP MLST指令的目录项格式字符串
     * @see org.swdc.rmdisk.core.ftp.PathAccessor
     */
    public String asFTPMListEntry(String absoluteParentPath) {
        if (absoluteParentPath.endsWith("/")) {
            absoluteParentPath = absoluteParentPath.substring(
                    0, absoluteParentPath.length() - 1
            );
        }
        String prefix = "Type=" + (isDirectory ? "dir" : "file") + ";";
        String perm = isDirectory ? "elrwfd": "radfw";
        String name = isDirectory ? getName() + "/" : getName();
        return String.format("%sSize=%d;Perm=%s;Modify=%s; %s/%s\r\n", prefix,
                (isDirectory ? 0 : getSize()),perm, lastModified, absoluteParentPath, name);
    }

}
