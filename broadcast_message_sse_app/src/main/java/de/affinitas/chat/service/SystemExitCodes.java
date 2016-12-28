package de.affinitas.chat.service;

public enum SystemExitCodes {

    SUCCESSFUL_TERMINATION(0),
    BASE_VALUE_FOR_ERROR_MESSAGES(64),
    COMMAND_LINE_USAGE_ERROR(64),
    DATA_FORMAT_ERROR(65),
    CANNOT_OPEN_INPUT(66),
    USER_UNKNOWN(67),
    HOST_NAME_UNKNOWN(68),
    SERVICE_UNAVAILABLE(69),
    INTERNAL_SOFTWARE_ERROR(70),
    SYSTEM_ERROR(71), /* such as can't fork etc */
    CRITICAL_OS_FILE_MISSING(72),
    CANNOT_CREATE_OUTPUT_FILE(73),
    IO_ERROR(74),
    TEMP_FAILURE_CAN_RETRY(75),
    REMOTE_ERROR_IN_PROTOCOL(76),
    PERMISSION_DENIED(77),
    CONFIGURATION_ERROR(78)
    ;

    private int code;

    SystemExitCodes(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
