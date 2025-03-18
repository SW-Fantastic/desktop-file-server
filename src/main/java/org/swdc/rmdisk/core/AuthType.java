package org.swdc.rmdisk.core;

public enum AuthType {

    /**
     * 微软的NTLM，
     * 用于和WindowsExplorer集成，Windows的Explorer
     * 客户端支持NTLMv2。
     */
    NTLM,

    /**
     * Bearer令牌认证，用于处理JWT（尚未实现）
     */
    Bearer,

    /**
     * 未知认证方式。
     */
    Unknown;

}
