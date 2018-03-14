import java.util.Arrays;
import java.util.Optional;

public enum ErrorCode {
    UNKNOWN("unknown_error"),
    INVALID_PARAMETER("invalid_parameter"),
    SERVICE_REQUEST_ERROR("service_request_error"),
    NOT_FOUND("not_found"),
    ACCESS_DENIED("access_denied"),
    INVALID_FORMAT("invalid_format"),
    DOS_INTEGRATION_ERROR("dos_integration_error", "订单状态更新失败"),
    DOS_WIP_NOT_FOUND("dos_wip_not_found", "订单状态更新失败"),
    OTR_AS_INTERNAL_ERROR("otr_as_internal_error");

    private String value;

    private String descriptionCn;

    ErrorCode(String errCode) {
        this.value = errCode;
    }

    ErrorCode(String errCode, String descriptionCn) {
        this.value = errCode;
        this.descriptionCn = descriptionCn;
    }

    public static ErrorCode fromValue(String value) {
        Optional<ErrorCode> searchResult = Arrays.stream(ErrorCode.values())
            .filter(errorCode -> errorCode.value.equals(value)).findFirst();


        return searchResult.orElse(null);
    }

    public String getValue() {
        return value;
    }

    public String getDescriptionCn() {
        return descriptionCn;
    }
}
