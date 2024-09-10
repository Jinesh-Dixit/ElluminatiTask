package com.example.servicelist.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servicelist.databinding.ItemMainServiceBinding
import com.example.servicelist.model.ListItem
import com.example.servicelist.model.SpecificationsItem
import com.example.servicelist.utils.showHide

class ServiceAdapter(
    var specificationsItem: ArrayList<SpecificationsItem>,
    val mContext: Context,
    private val itemClick: (result: ListItem) -> Unit
) :
    RecyclerView.Adapter<ServiceAdapter.DataViewHolder>() {


    inner class DataViewHolder constructor(val binding: ItemMainServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val value = specificationsItem[position]
            binding.tvTitle.text = value.name!![0].toString()

            showHide(
                mContext,
                binding.tvRequired,
                value.isRequired
            )

            chooseTitleSet(value)

            if (value.isParentAssociate == true) {
                val singleSelectionAdapter =
                    SingleSelectionAdapter(value.list.sortedBy { it.sequenceNumber }.toList() as ArrayList<ListItem>, mContext) {
                        it.isDefaultSelected = true
                        itemClick.invoke(it)
                        notifyDataSetChanged()
                    }
                binding.rvServiceList.apply {
                    adapter = singleSelectionAdapter
                }
            } else {
                val multiSelectionAdapter =
                    MultiSelectionAdapter(value.list.sortedBy { it.sequenceNumber }.toList() as ArrayList<ListItem>,value, mContext) {
                        itemClick.invoke(it)
                    }
                binding.rvServiceList.apply {
                    adapter = multiSelectionAdapter
                }
            }
            binding.executePendingBindings()
        }
    }

    private fun DataViewHolder.chooseTitleSet(
        value: SpecificationsItem
    ) {
        if (value.isParentAssociate == true) {
            binding.tvChooseTitle.text = "Choose 1"
        } else if (value.isAssociated == true) {
            if (value.range == 0 && value.maxRange == 1) {
                binding.tvChooseTitle.text = "Choose up to 1"
            } else if (value.range == 1 && value.maxRange == 3) {
                binding.tvChooseTitle.text = "Choose minimum 1 upto 3"
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataViewHolder {
        return DataViewHolder(
            ItemMainServiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return specificationsItem.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(specifications: ArrayList<SpecificationsItem>) {
        this.specificationsItem = specifications as ArrayList<SpecificationsItem>
        notifyDataSetChanged()
    }


}
