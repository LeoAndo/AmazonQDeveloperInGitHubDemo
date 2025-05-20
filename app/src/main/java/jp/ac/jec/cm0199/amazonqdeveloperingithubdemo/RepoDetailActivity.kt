package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.GitHubRepo

class RepoDetailActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    
    companion object {
        private const val EXTRA_REPO_URL = "extra_repo_url"
        private const val EXTRA_REPO_NAME = "extra_repo_name"
        
        fun createIntent(context: Context, repo: GitHubRepo): Intent {
            return Intent(context, RepoDetailActivity::class.java).apply {
                putExtra(EXTRA_REPO_URL, repo.htmlUrl)
                putExtra(EXTRA_REPO_NAME, repo.fullName)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_detail)
        
        val repoUrl = intent.getStringExtra(EXTRA_REPO_URL) ?: run {
            finish()
            return
        }
        
        val repoName = intent.getStringExtra(EXTRA_REPO_NAME) ?: ""
        
        supportActionBar?.apply {
            title = repoName
            setDisplayHomeAsUpEnabled(true)
        }
        
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(repoUrl)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}