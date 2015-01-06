package com.nightwind.tcfl.oss;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicBoolean;


public class MeasuableInputStream extends FilterInputStream {
    private SaveCallback ossCallback;
    private String caller;
    private int byteRead = 0;

    private int totalSize = 0;

    private AtomicBoolean isCancel = null;

    public MeasuableInputStream(String caller, InputStream in, SaveCallback ossCallback, int totalSize) {
        super(in);
        this.caller = caller;
        this.ossCallback = ossCallback;
        this.totalSize = totalSize;
    }

    public MeasuableInputStream(String caller, InputStream in, SaveCallback ossCallback, int current, int totalSize) {
        super(in);
        this.caller = caller;
        this.ossCallback = ossCallback;
        this.totalSize = totalSize;
        this.byteRead = current;
    }

    public void setSwitch(AtomicBoolean isCancel) {
        this.isCancel = isCancel;
    }

    public int read()
            throws IOException {
        byte[] buff = new byte[1];
        int readn = super.read(buff, 0, 1);
        if (readn != -1) {
            return buff[0];
        }
        return readn;
    }

    public int read(byte[] buffer)
            throws IOException {
        return super.read(buffer);
    }

    public int read(byte[] buffer, int byteOffset, int byteCount)
            throws IOException {
        if ((this.isCancel != null) && (this.isCancel.get())) {
            super.close();
            throw new InterruptedIOException();
        }
        int readn = super.read(buffer, byteOffset, byteCount);
        if (readn != -1) {
            this.byteRead += readn;
            this.ossCallback.onProgress(this.caller, this.byteRead, this.totalSize);
        }
        return readn;
    }

    public long skip(long byteCount)
            throws IOException {
        if ((this.isCancel != null) && (this.isCancel.get())) {
            super.close();
            throw new InterruptedIOException();
        }
        return super.skip(byteCount);
    }
}

