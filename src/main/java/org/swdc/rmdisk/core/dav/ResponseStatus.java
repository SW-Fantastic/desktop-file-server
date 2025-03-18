package org.swdc.rmdisk.core.dav;

public interface ResponseStatus {

    String OK = "HTTP/1.1 200 OK";

    String NOT_FOUND = "HTTP/1.1 404 NOT FOUND";

    String NOT_LOGIN = "HTTP/1.1 401 UNAUTHORIZED";

    String FORBIDDEN = "HTTP/1.1 403 FORBIDDEN";

    String LOCKED = "HTTP/1.1 423 Locked";

}
