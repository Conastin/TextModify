package indi.conastin.textmodify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import indi.conastin.textmodify.databinding.AppMethodListviewItemBinding
import indi.conastin.textmodify.databse.HookMethodEntity

class AppMethodAdapter(private val onRuleLongClicked: (HookMethodEntity) -> Unit) :
    ListAdapter<HookMethodEntity, AppMethodAdapter.PackageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageViewHolder {
        return PackageViewHolder(
            AppMethodListviewItemBinding.inflate(
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
            onRuleLongClicked(current)
        }
        holder.bind(current)
    }

    class PackageViewHolder(private var binding: AppMethodListviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(packageInfoEntity: HookMethodEntity) {
            binding.originText.text = packageInfoEntity.originText
            binding.newText.text = packageInfoEntity.newText
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<HookMethodEntity>() {
            override fun areItemsTheSame(
                oldItem: HookMethodEntity,
                newItem: HookMethodEntity
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: HookMethodEntity,
                newItem: HookMethodEntity
            ): Boolean {
                return oldItem.originText == newItem.originText
            }
        }
    }

}