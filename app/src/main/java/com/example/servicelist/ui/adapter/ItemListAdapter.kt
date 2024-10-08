package com.example.servicelist.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicelist.databinding.ItemMainServiceBinding
import com.example.servicelist.databinding.ItemSingleSelectionBinding
import com.example.servicelist.model.ListItem
import com.example.servicelist.model.SpecificationsItem
import com.example.servicelist.utils.Utils
import com.example.servicelist.utils.showHide

class ItemListAdapter (
    var itemList: List<Any>,
    val mContext: Context,// Listener interface to handle child item clicks
    val itemClickChild : (ListItem,Boolean)-> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PARENT = 1
        const val VIEW_TYPE_CHILD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is SpecificationsItem -> VIEW_TYPE_PARENT
            is ListItem -> VIEW_TYPE_CHILD
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PARENT -> {
                val view = ItemMainServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ParentViewHolder(view)
            }
            VIEW_TYPE_CHILD -> {
                val view = ItemSingleSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ChildViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ParentViewHolder -> {
                holder.bind(itemList[position] as SpecificationsItem)
             }
            is ChildViewHolder -> {
                holder.bind(itemList[position] as ListItem)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    // ViewHolder for Parent Item
    inner class ParentViewHolder(private val binding : ItemMainServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parentItem: SpecificationsItem) {
            binding.apply {
                tvTitle.text = parentItem.name!![0].toString()
                showHide(
                    mContext,
                    tvRequired,
                    parentItem.isRequired
                )
                for (i in parentItem.list as ArrayList<ListItem>) {
                    if (parentItem.isParentAssociate == true) {
                        i.isItemRequired = true
                        i.isUserCanAddSpecificationQuantity = false
                    }else{
                        i.isUserCanAddSpecificationQuantity = true
                    }
                    i.isChildMultipleAllowed = parentItem.isMultipleAllowed == true
                }
                if (parentItem.isParentAssociate == true) {
                    tvChooseTitle.text = "Choose 1"
                } else if (parentItem.isAssociated == true) {
                    if (parentItem.range == 0 && parentItem.maxRange == 1) {
                        tvChooseTitle.text = "Choose up to 1"
                    } else if (parentItem.range == 1 && parentItem.maxRange == 3) {
                        tvChooseTitle.text = "Choose minimum 1 upto 3"
                    }
                }
                // Bind child RecyclerView
                rvServiceList.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
                // Flatten child list into the same adapter using the same adapter for parent and child
                rvServiceList.adapter = ItemListAdapter(parentItem.list,mContext,itemClickChild) // Pass the listener here
            }
        }
    }

    // ViewHolder for Child Item
    inner class ChildViewHolder(private val binding : ItemSingleSelectionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(childItem: ListItem) {
            binding.apply {
                showHide(
                    mContext,
                    llPlusMinus,
                    chkSelection.isChecked && childItem.price != 0 && childItem.isUserCanAddSpecificationQuantity
                )
                showHide(mContext, tvPrice, childItem.price != 0)
                tvTitle.text = childItem.name.toString()
                tvPrice.text = "${Utils.getCurrencySymbol("INR")} ${childItem.price.toString()}"
                tvCount.text = childItem.count.toString()
                rbSelection.isClickable = false
                rbSelection.isChecked = childItem.isDefaultSelected
                chkSelection.isChecked = childItem.isDefaultSelected
                if (childItem.isItemRequired) {
                    rbSelection.visibility = View.VISIBLE
                    chkSelection.visibility = View.GONE
                } else {
                    rbSelection.visibility = View.GONE
                    chkSelection.visibility = View.VISIBLE
                }

                rbSelection.setOnClickListener {
                    if (!childItem.isDefaultSelected) {
                        (itemList as ArrayList<ListItem>).map {
                            it.isDefaultSelected = false
                        }
                        childItem.isDefaultSelected = true
                        rbSelection.isChecked = childItem.isDefaultSelected
                        itemClickChild.invoke(childItem, true)
                        notifyDataSetChanged()
                    }
                }

                imgPlus.setOnClickListener {
                    childItem.count = childItem.count + 1
                    tvCount.text = childItem.count.toString()
                    itemClickChild.invoke(childItem, false)
                    notifyDataSetChanged()
                }

                imgMinus.setOnClickListener {
                    if (childItem.count != 1) {
                        childItem.count = childItem.count - 1
                        tvCount.text = childItem.count.toString()
                        itemClickChild.invoke(childItem, false)
                        notifyDataSetChanged()
                    }
                }

                tvTitle.setOnClickListener {
                    if (!childItem.isItemRequired) {
                        itemClick(childItem, chkSelection)
                    }
                }

                tvPrice.setOnClickListener {
                    if (!childItem.isItemRequired) {
                        itemClick(childItem, chkSelection)
                    }
                }

                // Set up checkbox listener
                chkSelection.setOnClickListener {
                    itemClick(childItem, chkSelection)
                }
            }
        }
    }

    private fun itemClick(childItem : ListItem,chkSelection : CheckBox){
        if (!childItem.isDefaultSelected){
            (itemList as ArrayList<ListItem>).map {
                it.isDefaultSelected = false
                childItem.isDefaultSelected = true
                childItem.count = 1
                chkSelection.isChecked = childItem.isDefaultSelected
                it.isUserCanAddSpecificationQuantity = false
                childItem.isUserCanAddSpecificationQuantity = chkSelection.isChecked
                itemClickChild.invoke(childItem, false)
                notifyDataSetChanged()
            }
        }else{
            (itemList as ArrayList<ListItem>).map {
                it.isUserCanAddSpecificationQuantity = false
                childItem.isDefaultSelected = false
                childItem.count = 1
                chkSelection.isChecked = childItem.isDefaultSelected
                itemClickChild.invoke(childItem, false)
                notifyDataSetChanged()
            }
        }
    }

    fun setData(specifications: java.util.ArrayList<SpecificationsItem>) {
        this.itemList = specifications
        notifyDataSetChanged()
    }
}