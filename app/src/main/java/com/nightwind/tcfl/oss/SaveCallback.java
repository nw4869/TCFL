package com.nightwind.tcfl.oss;

public abstract class SaveCallback {
    public abstract void onSuccess(String paramString);

    public abstract void onProgress(String paramString, int paramInt1, int paramInt2);

    public abstract void onFailure(String paramString);
}
