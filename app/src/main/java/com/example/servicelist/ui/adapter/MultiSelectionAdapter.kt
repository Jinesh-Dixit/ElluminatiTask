package com.example.servicelist.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servicelist.databinding.ItemMultipleSelectionBinding
import com.example.servicelist.model.ListItem
import com.example.servicelist.model.SpecificationsItem
import com.example.servicelist.utils.Utils
import com.example.servicelist.utils.showHide
import java.util.*

class MultiSelectionAdapter(
    var listItem: ArrayList<ListItem>,
    var specificationsItem: SpecificationsItem,
    val mContext: Context,
    private val itemClick: (result: ListItem) -> Unit
) :
    RecyclerView.Adapter<MultiSelectionAdapter.DataViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemMultipleSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(position)

        holder.binding.chkSelection.setOnClickListener {
            checkBoxClick(position, holder)
        }

        holder.binding.tvTitle.setOnClickListener {
            itemClick(holder, position)
        }

        holder.binding.tvPrice.setOnClickListener {
            itemClick(holder, position)
        }

        holder.binding.imgPlus.setOnClickListener {
            listItem[position].count = listItem[position].count + 1
            holder.binding.tvCount.text = listItem[position].count.toString()
            itemClick.invoke(listItem[position])
            notifyDataSetChanged()
        }
        holder.binding.imgMinus.setOnClickListener {
            if (listItem[position].count != 1) {
                listItem[position].count = listItem[position].count - 1
                holder.binding.tvCount.text = listItem[position].count.toString()
                itemClick.invoke(listItem[position])
                notifyDataSetChanged()
            }

        }
    }

    private fun checkBoxClick(
        position: Int,
        holder: DataViewHolder
    ) {
        if (!specificationsItem.isMultipleAllowed) {
            for (i in listItem.indices) {
                listItem[position].count = 1
                listItem[i].isDefaultSelected = false
            }
        } else {
            if (!holder.binding.chkSelection.isChecked) {
                listItem[position].count = 1
            }
        }
        listItem[position].isDefaultSelected = holder.binding.chkSelection.isChecked
        itemClick.invoke(listItem[position])
        notifyDataSetChanged()
    }

    private fun itemClick(
        holder: DataViewHolder,
        position: Int
    ) {
        holder.binding.chkSelection.isChecked = !holder.binding.chkSelection.isChecked
        if (!holder.binding.chkSelection.isChecked) {
            listItem[position].count = 1
        }
        if (!specificationsItem.isMultipleAllowed) {
            for (i in listItem.indices) {
                listItem[i].isDefaultSelected = false
            }
        }
        listItem[position].isDefaultSelected = holder.binding.chkSelection.isChecked
        itemClick.invoke(listItem[position])
        notifyDataSetChanged()
    }

    inner class DataViewHolder constructor(val binding: ItemMultipleSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val value = listItem[position]
            binding.tvTitle.text = value.name!![0].toString()
            showHide(mContext, binding.tvPrice, value.price != 0)
            binding.tvPrice.text = "${Utils.getCurrencySymbol("INR")} ${value.price.toString()}"
            /*binding.chkSelection.isClickable = false*/
            binding.chkSelection.isChecked = value.isDefaultSelected
            showHide(
                mContext,
                binding.llPlusMinus,
                binding.chkSelection.isChecked && value.price != 0 && specificationsItem.userCanAddSpecificationQuantity == true
            )
            binding.tvCount.text = value.count.toString()
            binding.executePendingBindings()
        }
    }

}