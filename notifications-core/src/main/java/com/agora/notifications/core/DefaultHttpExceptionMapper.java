package com.agora.notifications.core;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public final class DefaultHttpExceptionMapper implements HttpExceptionMapper {
    @Override
    public NetFailure map(Throwable t) {
        Throwable r = unwrap(t);
        if (r instanceof HttpTimeoutException) return new NetFailure(NetErrorType.TIMEOUT, r);
        if (r instanceof UnknownHostException) return new NetFailure(NetErrorType.DNS, r);
        if (r instanceof ConnectException) return new NetFailure(NetErrorType.CONNECT, r);
        if (r instanceof SSLException) return new NetFailure(NetErrorType.TLS, r);
        if (r instanceof CancellationException) return new NetFailure(NetErrorType.CANCELLED, r);
        if (r instanceof IOException) return new NetFailure(NetErrorType.IO, r);
        return new NetFailure(NetErrorType.UNKNOWN, r);
    }

    private static Throwable unwrap(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null && (c instanceof CompletionException || c instanceof ExecutionException))
            c = c.getCause();
        return c;
    }
}
