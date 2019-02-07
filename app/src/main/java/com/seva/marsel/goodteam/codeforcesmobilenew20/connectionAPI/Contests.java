package com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Contests {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<ContestResult> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ContestResult> getResult() {
        return result;
    }

    public void setResult(List<ContestResult> result) {
        this.result = result;
    }

}
