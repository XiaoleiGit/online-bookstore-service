package org.company.common;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BusinessException extends ResponseStatusException {
    public BusinessException(HttpStatusCode status) {
        super(status);
    }

    public BusinessException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public BusinessException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }

    public BusinessException(HttpStatusCode status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    protected BusinessException(HttpStatusCode status, String reason, Throwable cause, String messageDetailCode, Object[] messageDetailArguments) {
        super(status, reason, cause, messageDetailCode, messageDetailArguments);
    }
}
