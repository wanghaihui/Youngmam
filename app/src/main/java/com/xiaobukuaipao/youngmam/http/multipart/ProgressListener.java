package com.xiaobukuaipao.youngmam.http.multipart;
/**
 * Progress call back interface
 */
public interface ProgressListener {
    /**
     * Callback method thats called on each byte transfer.
     */
    void onProgress(String key, long transferredBytes, long totalSize);
}
