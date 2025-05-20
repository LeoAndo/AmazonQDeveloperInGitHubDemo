package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.adapter.GitHubRepoAdapter
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.viewmodel.GitHubViewModel

class MainActivity : AppCompatActivity() {
    
    private val viewModel: GitHubViewModel by viewModels()
    private lateinit var adapter: GitHubRepoAdapter
    
    // UI components
    private lateinit var etSearch: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var spinnerSort: Spinner
    private lateinit var spinnerOrder: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var tvError: TextView
    private lateinit var btnRetry: Button
    private lateinit var tvEmpty: TextView
    
    // Sorting options
    private lateinit var sortValues: Array<String>
    private lateinit var orderValues: Array<String>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupRecyclerView()
        setupSortingSpinners()
        setupListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        spinnerSort = findViewById(R.id.spinnerSort)
        spinnerOrder = findViewById(R.id.spinnerOrder)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        tvError = findViewById(R.id.tvError)
        btnRetry = findViewById(R.id.btnRetry)
        tvEmpty = findViewById(R.id.tvEmpty)
        
        sortValues = resources.getStringArray(R.array.sort_values)
        orderValues = resources.getStringArray(R.array.order_values)
    }
    
    private fun setupRecyclerView() {
        adapter = GitHubRepoAdapter { repo ->
            // Handle item click - open repository detail
            startActivity(RepoDetailActivity.createIntent(this, repo))
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupSortingSpinners() {
        // Setup sort spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSort.adapter = adapter
        }
        
        // Setup order spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.order_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOrder.adapter = adapter
        }
    }
    
    private fun setupListeners() {
        // Search button click
        btnSearch.setOnClickListener {
            performSearch()
        }
        
        // Enter key on keyboard
        etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }
        
        // Retry button click
        btnRetry.setOnClickListener {
            viewModel.retry()
        }
        
        // Sorting spinners
        val sortingListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (etSearch.text.toString().isNotBlank()) {
                    updateSorting()
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        spinnerSort.onItemSelectedListener = sortingListener
        spinnerOrder.onItemSelectedListener = sortingListener
    }
    
    private fun performSearch() {
        val query = etSearch.text.toString().trim()
        if (query.isNotBlank()) {
            updateSorting()
        }
    }
    
    private fun updateSorting() {
        val query = etSearch.text.toString().trim()
        val sortPosition = spinnerSort.selectedItemPosition
        val orderPosition = spinnerOrder.selectedItemPosition
        
        val sort = sortValues[sortPosition].takeIf { it.isNotBlank() }
        val order = orderValues[orderPosition]
        
        viewModel.searchRepositories(query, sort, order)
    }
    
    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is GitHubViewModel.UiState.Initial -> {
                    showInitialState()
                }
                is GitHubViewModel.UiState.Loading -> {
                    showLoadingState()
                }
                is GitHubViewModel.UiState.Success -> {
                    showSuccessState(state)
                }
                is GitHubViewModel.UiState.Empty -> {
                    showEmptyState()
                }
                is GitHubViewModel.UiState.Error -> {
                    showErrorState(state)
                }
            }
        }
    }
    
    private fun showInitialState() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }
    
    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }
    
    private fun showSuccessState(state: GitHubViewModel.UiState.Success) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        
        adapter.submitList(state.repositories)
    }
    
    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
    
    private fun showErrorState(state: GitHubViewModel.UiState.Error) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.GONE
        
        tvError.text = state.message
    }
}