package indi.conastin.textmodify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import indi.conastin.textmodify.databinding.AppListListviewItemBinding
import indi.conastin.textmodify.databse.AppInfo

class AppListAdapter(private val onPackageClicked: (AppInfo) -> Unit) :
    ListAdapter<AppInfo, AppListAdapter.PackageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageViewHolder {
        return PackageViewHolder(
            AppListListviewItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onPackageClicked(current)
        }
        holder.bind(current)
    }

    class PackageViewHolder(private var binding: AppListListviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfo) {
            binding.appListItemName.text = appInfo.appName
            binding.appListItemPackageName.text = appInfo.packageName
            binding.appListItemIcon.setImageDrawable(appInfo.icon)
        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppInfo>() {
            override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
                return oldItem.packageName == newItem.packageName
            }
        }
    }

}