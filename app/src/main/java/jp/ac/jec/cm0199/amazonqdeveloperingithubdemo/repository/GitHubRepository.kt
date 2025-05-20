package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.repository

import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.api.RetrofitClient
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.GitHubRepo
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GitHubRepository {
    private val apiService = RetrofitClient.gitHubApiService
    
    suspend fun searchRepositories(
        query: String,
        sort: String? = null,
        order: String? = null
    ): Result<SearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchRepositories(
                    query = query,
                    sort = sort,
                    order = order
                )
                
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}