package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.R
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.GitHubRepo

class GitHubRepoAdapter(private val onItemClick: (GitHubRepo) -> Unit) : 
    ListAdapter<GitHubRepo, GitHubRepoAdapter.RepoViewHolder>(RepoDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_github_repo, parent, false)
        return RepoViewHolder(view, onItemClick)
    }
    
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class RepoViewHolder(itemView: View, private val onItemClick: (GitHubRepo) -> Unit) : 
        RecyclerView.ViewHolder(itemView) {
        
        private val tvRepoName: TextView = itemView.findViewById(R.id.tvRepoName)
        private val tvRepoDescription: TextView = itemView.findViewById(R.id.tvRepoDescription)
        private val tvStars: TextView = itemView.findViewById(R.id.tvStars)
        private val tvLanguage: TextView = itemView.findViewById(R.id.tvLanguage)
        private val tvOwner: TextView = itemView.findViewById(R.id.tvOwner)
        
        fun bind(repo: GitHubRepo) {
            tvRepoName.text = repo.name
            tvRepoDescription.text = repo.description ?: "No description"
            tvStars.text = "★ ${repo.stars}"
            tvLanguage.text = repo.language ?: "N/A"
            tvOwner.text = repo.owner.login
            
            itemView.setOnClickListener {
                onItemClick(repo)
            }
        }
    }
    
    class RepoDiffCallback : DiffUtil.ItemCallback<GitHubRepo>() {
        override fun areItemsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
            return oldItem == newItem
        }
    }
}