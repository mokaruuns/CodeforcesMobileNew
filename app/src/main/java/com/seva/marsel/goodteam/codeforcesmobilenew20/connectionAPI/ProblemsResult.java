package com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProblemsResult {

    @SerializedName("problems")
    @Expose
    private List<ProblemResult> problems = null;

    public List<ProblemResult> getProblems() {
        return problems;
    }

    public void setProblems(List<ProblemResult> problems) {
        this.problems = problems;
    }

}