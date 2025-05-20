package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.api

import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>
}