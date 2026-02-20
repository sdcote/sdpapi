package com.sdcote.sdp;


import coyote.commons.dataframe.DataFrame;

import java.net.http.HttpRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Used to carry data relating to the response of the requests we make
 * <p>
 * 200  Success - Success with response body.
 * 201  Created - Success with response body.
 * 204  Success - Success with no response body.
 * 400  Bad Request - The request URI does not match the APIs in the system,
 * or the operation failed for unknown reasons. Invalid headers can also
 * cause this error.
 * 401  Unauthorized - The user is not authorized to use the API.
 * 403  Forbidden - The requested operation is not permitted for the user.
 * This error can also be caused by ACL failures, or business rule or data
 * policy constraints.
 * 404  Not found - The requested resource was not found. This can be caused
 * by an ACL constraint or if the resource does not exist.
 * 405  Method not allowed - The HTTP action is not allowed for the requested
 * REST API, or it is not supported by any API.
 */
public class ApiResponse {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,##0.00");
    private final List<DataFrame> results = new ArrayList<>();
    private HttpRequest request = null;
    private int httpStatusCode = 0;
    private DataFrame errorFrame = null;
    private int totalCount = -1;
    private int serverTime = 0;
    private String link = null; // multipurpose attribute normally used with 300 series errors containing a link to the redirected location
    private long requestStart = 0;
    private long requestEnd = 0;
    private long parseStart = 0;
    private long parseEnd = 0;
    private long txnStart = 0;
    private long txnEnd = 0;
    private DataFrame responseFrame = null;



    private DataFrame listInfoFrame = null;


    /**
     * @param request the HTTP request that was sent
     */
    public ApiResponse(final HttpRequest request) {
        this.request = request;
    }

    /**
     * Append a right-aligned and zero-padded numeric value to a `StringBuilder`.
     */
    private static void append(StringBuilder tgt, String pfx, int dgt, long val) {
        tgt.append(pfx);
        if (dgt > 1) {
            int pad = (dgt - 1);
            for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
                pad--;
            }
            for (int xa = 0; xa < pad; xa++) {
                tgt.append('0');
            }
        }
        tgt.append(val);
    }

    /**
     * @return the dataframe containing the error results if present
     */
    public DataFrame getErrorFrame() {
        return errorFrame;
    }

    /**
     * @param frame the dataframe containing the error results if present
     */
    public void setErrorFrame(final DataFrame frame) {
        errorFrame = frame;
    }

    /**
     * @return the HTTP status code (the 200 part of "200 OK")
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }


    /**
     * @return the request
     */
    public HttpRequest getRequest() {
        return request;
    }

    public List<DataFrame> getResults() {
        return results;
    }


    /**
     * Add the frame to the result list.
     *
     * @param frame The next frame to add
     */
    public void add(final DataFrame frame) {
        if (frame != null) {
            results.add(frame);
        }
    }

    /**
     * Get a particular frame from the result set.
     *
     * @param index the 0 based index into the result set
     * @return the dataframe at that position or null if not found
     * @see #getResultSize() for the number of result frames in the response
     */
    public DataFrame getFrame(int index) {
        if (index < results.size()) {
            return results.get(index);
        } else {
            return null;
        }
    }

    /**
     * @return how many result records were returned from the request
     */
    public int getResultSize() {
        return results.size();
    }

    /**
     * @param status the HTTP status code
     */
    public void setStatusCode(final int status) {
        httpStatusCode = status;
    }


    /**
     * Some responses (e.g. 301, 302) contain a link to the location of where the
     * request should go for the requested resource.
     *
     * <p>Not all responses will contain a link. The most common scenario is when
     * the status code is in the 300 series.</p>
     *
     * @return the link set in this response
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Informational - Request received, continuing process.
     *
     * @return true if the status code is in the 1XX range
     */
    public boolean isInformational() {
        return (httpStatusCode < 200);
    }

    /**
     * Success - The action requested by the client was received, understood,
     * accepted and processed successfully.
     *
     * @return true if the status code is in the 2XX range
     */
    public boolean isSuccessful() {
        return (httpStatusCode >= 200 && httpStatusCode < 300);
    }

    /**
     * Redirection - The client must take additional action to complete the
     * request.
     *
     * @return true if the status code is in the 3XX range
     */
    public boolean isRedirect() {
        return (httpStatusCode >= 300 && httpStatusCode < 400);
    }

    /**
     * Error - Either client or server error.
     *
     * @return true if the status code is greater than or equal to 400.
     */
    public boolean isError() {
        return (httpStatusCode >= 400);
    }

    /**
     * Client Error-Indicate cases in which the client seems to have erred.
     *
     * @return true if the status code is in the 4XX range
     */
    public boolean isClientError() {
        return (httpStatusCode >= 400 && httpStatusCode < 500);
    }

    /**
     * Server Error - The server failed to fulfill an apparently valid request.
     *
     * @return true if the status code is in the 5XX range
     */
    public boolean isServerError() {
        return (httpStatusCode >= 500);
    }

    /**
     * When using the LIMIT parameter, the server will send back the size of the
     * query, allowing for subsequent calls with the OFFSET parameter to paginate
     * through the entire result set.
     *
     * @return the total record count of the result set, -1 if the total count
     * was not returned by the server
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param count the number of records in the result set, normally set by the
     *              worker
     */
    public void setTotalCount(int count) {
        this.totalCount = count;
    }

    /**
     * Set the time (in milliseconds) when the request was sent to the current time.
     */
    public void requestStart() {
        requestStart = System.currentTimeMillis();
    }

    /**
     * @return the time in milliseconds when the request was sent
     */
    public long getRequestStart() {
        return requestStart;
    }

    /**
     * Set the time (in milliseconds) when the request was received to the current time.
     */
    public void requestEnd() {
        requestEnd = System.currentTimeMillis();
    }

    /**
     * @return the time in milliseconds when the request was received
     */
    public long getRequestEnd() {
        return requestEnd;
    }

    /**
     * @return number of milliseconds between the start and end of the HTTP request.
     */
    public long getRequestElapsed() {
        return requestEnd - requestStart;
    }

    /**
     * Set the time (in milliseconds) when the data parsing began to the current time.
     */
    public void parseStart() {
        parseStart = System.currentTimeMillis();
    }

    /**
     * @return the time in milliseconds when the response data parsing began
     */
    public long getParseStart() {
        return parseStart;
    }

    /**
     * Set the time (in milliseconds) when the data parsing ended to the current time.
     */
    public void parseEnd() {
        parseEnd = System.currentTimeMillis();
    }

    /**
     * @return number of milliseconds between the start and end of parsing.
     */
    public long getParsingElapsed() {
        return parseEnd - parseStart;
    }

    /**
     * @return the time in milliseconds when the response data parsing ended
     */
    public long getParseEnd() {
        return parseEnd;
    }

    /**
     * Formats the given number of milliseconds into hours, minutes and seconds
     * and if requested the remaining milliseconds.
     *
     * @param val the interval in milliseconds
     * @return the time interval in hh:mm:ss format.
     */
    public String formatElapsedMillis(long val, boolean millis) {
        StringBuilder buf = new StringBuilder(20);
        String sgn = "";

        if (val < 0) {
            sgn = "-";
            val = Math.abs(val);
        }

        append(buf, sgn, 0, (val / 3600000));
        append(buf, ":", 2, ((val % 3600000) / 60000));
        append(buf, ":", 2, ((val % 60000) / 1000));
        if (millis) append(buf, ".", 3, (val % 1000));

        return buf.toString();
    }

    public String getResponseTime() {
        return formatElapsedMillis(requestEnd - requestStart, true);
    }


    public String getParsingTime() {
        return formatElapsedMillis(getParsingElapsed(), true);
    }


    public String getParsingTimePerRecord() {
        return !results.isEmpty() ? DECIMAL_FORMAT.format((double) getParsingElapsed() / (double) results.size()) : "?";
    }

    /**
     * @return the number of milliseconds the server spent processing the request
     */
    public int getServerElapsed() {
        return serverTime;
    }

    public String getServerTime() {
        return formatElapsedMillis(getServerElapsed(), true);
    }

    /**
     * @param millis Number of milliseconds the server spent processing the request
     */
    public void setServerTime(int millis) {
        serverTime = millis;
    }

    /**
     * Set the time (in milliseconds) when the transaction started.
     */
    public void transactionStart() {
        txnStart = System.currentTimeMillis();
    }


    /**
     * @return the time in milliseconds  when the transaction started.
     */
    public long getTransactionStart() {
        return txnStart;
    }


    /**
     * Set the time (in milliseconds) when the transaction ended to the current time.
     */
    public void transactionEnd() {
        txnEnd = System.currentTimeMillis();
    }


    /**
     * @return the time in milliseconds when the transaction ended
     */
    public long getTransactionEnd() {
        return txnEnd;
    }


    /**
     * @return number of milliseconds between the start and end of the transaction.
     */
    public long getTransactionElapsed() {
        return txnEnd - txnStart;
    }


    /**
     * @return formatted time of how long the entire request took to process including HTTP exchange, parsing and other processing.
     */
    public String getTransactionTime() {
        return formatElapsedMillis(txnEnd - txnStart, true);
    }


    /**
     * @return formatted number of records per second for the entire transaction
     */
    public String getRecordsPerSecond() {
        if (!results.isEmpty()) {
            return DECIMAL_FORMAT.format((double) results.size() / (getTransactionElapsed() / (double) 1000));
        } else {
            return "?";
        }
    }

    public DataFrame getResponseFrame() {
        return responseFrame;
    }

    public void setResponseFrame(DataFrame responseFrame) {
        this.responseFrame = responseFrame;
    }

    public DataFrame getListInfoFrame() {
        return listInfoFrame;
    }

    public void setListInfoFrame(DataFrame listInfoFrame) {
        this.listInfoFrame = listInfoFrame;
    }
}