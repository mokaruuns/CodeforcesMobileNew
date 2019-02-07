package com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BlogInterface {
    @GET("/api/blogEntry.view")
     Call<Blog> getBlog(@Query("blogEntryId") String blogEntryId);
}
