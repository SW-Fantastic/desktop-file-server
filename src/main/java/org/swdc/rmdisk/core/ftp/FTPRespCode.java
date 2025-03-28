package org.swdc.rmdisk.core.ftp;

public enum FTPRespCode {

    COMMAND_OK("200"),
    NOT_EXECUTED("202"),
    NOT_RECOGNIZED("500"),
    SYNTAX_ERROR("501"),
    NOT_IMPLEMENTED("502"),
    BAD_SEQUENCE("503"),
    NOT_IMPLEMENTED_FOR_PARAMETER("504"),
    REQUEST_MARKER_REPLY("110"),
    SYSTEM_STATUS("211"),
    DIRECTORY_STATUS("212"),
    FILE_STATUS("213"),
    HELP_MESSAGE("214"),
    NAME_SYSTEM("215"),
    SERVICE_READY_IN_N_MINUTES("120"),
    SERVICE_READY("220"),
    SERVICE_CLOSING("221"),
    SERVICE_NOT_AVAILABLE("421"),
    DATA_CONN_OPENED("125"),
    DATA_CONN_IDLE_OPENED("225"),
    CAN_NOT_OPEN_DATA_CONN("425"),
    DATA_CONN_CLOSING("226"),
    ABORTED_TRANSFER("426"),
    ENTER_PASSIVE_MODE("227"),
    ENTER_EPSV_MODE("229"),
    USER_LOGGED("230"),
    USER_NOT_LOGGED("530"),
    NEED_PASSWORD("331"),
    NEED_ACCOUNT("332"),
    NEED_ACCOUNT_FOR_STORING("532"),
    FILE_STATE_OK_OPEN_DATA_CONN("150"),
    REQUESTED_FILE_ACTION_OK("250"),
    PATH_NAME_CREATED("257"),
    FILE_ACTION_PENDING("350"),
    FILE_BUSY("450"),
    FILE_UNAVAILABLE("550"),
    ACTION_LOCAL_ERROR("451"),
    ACTION_PAGE_ERROR("551"),
    ACTION_SPACE_INSUFFICIENT("452"),
    ACTION_SPACE_EXCEEDED("552"),
    FILE_NAME_NOT_ALLOWED("553"),

    ;

    private String code;

    FTPRespCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
