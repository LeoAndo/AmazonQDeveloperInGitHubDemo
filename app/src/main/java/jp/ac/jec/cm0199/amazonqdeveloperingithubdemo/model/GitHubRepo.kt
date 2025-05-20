package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model

import com.google.gson.annotations.SerializedName

data class GitHubRepo(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val owner: Owner,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("stargazers_count")
    val stars: Int,
    @SerializedName("forks_count")
    val forks: Int,
    val language: String?,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Owner(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String
)

data class SearchResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<GitHubRepo>
)