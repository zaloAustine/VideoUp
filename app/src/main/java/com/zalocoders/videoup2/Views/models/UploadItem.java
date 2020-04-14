package com.zalocoders.videoup2.Views.models;

public class UploadItem {
    String name;
    String url;
    String decs;

    public UploadItem() {
    }

    public UploadItem(String name, String url, String decs) {
        this.name = name;
        this.url = url;
        this.decs = decs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDecs() {
        return decs;
    }

    public void setDecs(String decs) {
        this.decs = decs;
    }
}
