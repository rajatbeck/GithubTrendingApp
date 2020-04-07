package com.rajat.zomatotest.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.rajat.zomatotest.R
import com.rajat.zomatotest.models.Repository
import kotlinx.android.synthetic.main.layout_repo_row.view.*


class MainAdapter constructor(private val onItemClick: (Int,Repository) -> Unit) :
    ListAdapter<Repository, MainAdapter.RepositoryViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Repository>(){
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return "${oldItem.name}${oldItem.author}" == "${newItem.name}${newItem.author}"
            }

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bindTo(getItem(position))
        holder.itemView.setOnClickListener { onItemClick.invoke(position,getItem(position)) }
    }

    inner class RepositoryViewHolder(parent: ViewGroup):
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_repo_row,parent,false)){

        fun bindTo(repository: Repository){
            val circleCrop = RequestOptions.circleCropTransform()
            itemView.apply {
                tvAuthor.text = repository.author
                tvRepoName.text = repository.name
                Glide.with(this)
                    .load(repository.avatar)
                    .apply(circleCrop)
                    .transition(withCrossFade())
                    .into(ivAvatar)
                if(repository.isExpanded){
                    expandedView.visibility = View.VISIBLE
                }else{
                    expandedView.visibility = View.GONE
                }
                tvDescriptionAndLink.text = "${repository.description}(${repository.url})"
                Glide.with(this)
                    .load(ColorDrawable(Color.parseColor(repository.languageColor?:"#FFFFFF")))
                    .apply(circleCrop)
                    .into(languageColorCode)
                tvLanguage.text = repository.language
                tvStarCount.text = "${repository.stars}"
                tvForkName.text = "${repository.forks}"
            }
        }

    }

}