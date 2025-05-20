package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.GitHubRepo
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.Owner
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.SearchResponse
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GitHubViewModelTest {

    // Executes each task synchronously using Architecture Components
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var repository: GitHubRepository

    private lateinit var viewModel: GitHubViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Create a test version of the ViewModel that uses the mock repository
        viewModel = GitHubViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `searchRepositories with valid query returns success state`() = testDispatcher.runBlockingTest {
        // Given
        val query = "android"
        val sort = "stars"
        val order = "desc"
        
        val mockRepo = GitHubRepo(
            id = 1,
            name = "Test Repo",
            fullName = "user/test-repo",
            owner = Owner(
                login = "user",
                id = 1,
                avatarUrl = "https://example.com/avatar.png"
            ),
            description = "Test description",
            htmlUrl = "https://github.com/user/test-repo",
            stars = 100,
            forks = 50,
            language = "Kotlin",
            updatedAt = "2023-01-01"
        )
        
        val mockResponse = SearchResponse(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(mockRepo)
        )
        
        Mockito.`when`(repository.searchRepositories(query, sort, order))
            .thenReturn(Result.success(mockResponse))
        
        // When
        viewModel.searchRepositories(query, sort, order)
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is GitHubViewModel.UiState.Success)
        assertEquals(1, (state as GitHubViewModel.UiState.Success).repositories.size)
        assertEquals("Test Repo", state.repositories[0].name)
    }

    @Test
    fun `searchRepositories with error returns error state`() = testDispatcher.runBlockingTest {
        // Given
        val query = "android"
        val errorMessage = "Network error"
        
        Mockito.`when`(repository.searchRepositories(query, null, null))
            .thenReturn(Result.failure(Exception(errorMessage)))
        
        // When
        viewModel.searchRepositories(query)
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is GitHubViewModel.UiState.Error)
        assertEquals(errorMessage, (state as GitHubViewModel.UiState.Error).message)
    }

    @Test
    fun `searchRepositories with empty results returns empty state`() = testDispatcher.runBlockingTest {
        // Given
        val query = "xyznonexistentrepo123456789"
        
        val mockResponse = SearchResponse(
            totalCount = 0,
            incompleteResults = false,
            items = emptyList()
        )
        
        Mockito.`when`(repository.searchRepositories(query, null, null))
            .thenReturn(Result.success(mockResponse))
        
        // When
        viewModel.searchRepositories(query)
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is GitHubViewModel.UiState.Empty)
    }

    @Test
    fun `retry calls searchRepositories with last used parameters`() = testDispatcher.runBlockingTest {
        // Given
        val query = "android"
        val sort = "stars"
        val order = "desc"
        
        val mockRepo = GitHubRepo(
            id = 1,
            name = "Test Repo",
            fullName = "user/test-repo",
            owner = Owner(
                login = "user",
                id = 1,
                avatarUrl = "https://example.com/avatar.png"
            ),
            description = "Test description",
            htmlUrl = "https://github.com/user/test-repo",
            stars = 100,
            forks = 50,
            language = "Kotlin",
            updatedAt = "2023-01-01"
        )
        
        val mockResponse = SearchResponse(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(mockRepo)
        )
        
        // First call fails
        Mockito.`when`(repository.searchRepositories(query, sort, order))
            .thenReturn(Result.failure(Exception("Error")))
            .thenReturn(Result.success(mockResponse)) // Second call succeeds
        
        // When - first search fails
        viewModel.searchRepositories(query, sort, order)
        
        // Then - error state
        assertTrue(viewModel.uiState.value is GitHubViewModel.UiState.Error)
        
        // When - retry
        viewModel.retry()
        
        // Then - success state
        val state = viewModel.uiState.value
        assertTrue(state is GitHubViewModel.UiState.Success)
    }
}