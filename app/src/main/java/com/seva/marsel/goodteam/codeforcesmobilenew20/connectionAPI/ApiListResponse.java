package com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiListResponse<T> {

    @SerializedName("result")
    @Expose
    public List<T> result;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}