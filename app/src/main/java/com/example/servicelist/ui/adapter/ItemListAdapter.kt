package com.example.servicelist.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicelist.R
import com.example.servicelist.model.ListItem
import com.example.servicelist.model.SpecificationsItem

class ItemListAdapter (
    val itemList: List<Any>,
    val mContext: Context,// Listener interface to handle child item clicks
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Interface to handle checkbox/radio button clicks from child items
    interface OnChildItemCheckedListener {
        fun onChildItemChecked(childItem: ListItem, isChecked: Boolean)
    }
    val view : LayoutInflater ? = null

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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_service, parent, false)
                ParentViewHolder(view)
            }
            VIEW_TYPE_CHILD -> {
                if((itemList[0] as SpecificationsItem).type == 1){
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_multiple_selection, parent, false)
                    ChildViewHolder(view)
                }else{
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_single_selection, parent, false)
                    ChildViewHolder(view)
                }
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ParentViewHolder -> holder.bind(itemList[position] as SpecificationsItem)
            is ChildViewHolder -> holder.bind(itemList[position] as ListItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

    // ViewHolder for Parent Item
    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val parentTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.rvServiceList)

        fun bind(parentItem: SpecificationsItem) {
            parentTitle.text = parentItem.name!![0].toString()

            // Bind child RecyclerView
            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)

            // Flatten child list into the same adapter using the same adapter for parent and child
            val flatChildList = parentItem.list
            Log.e("jinesh","${parentItem.list.size}")

            childRecyclerView.adapter = ItemListAdapter(flatChildList,mContext) // Pass the listener here
        }
    }

    // ViewHolder for Child Item
    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         // Assuming checkbox

        fun bind(childItem: ListItem) {
            var childName: TextView? =null
            var radioButton: RadioButton? =null
            var checkBox: CheckBox? =null
            if ((itemList[0] as SpecificationsItem).type == 1){
                childName = itemView.findViewById(R.id.tvTitle)
                radioButton = itemView.findViewById(R.id.rbSelection)
            }else{
                childName =  itemView.findViewById(R.id.tvTitle)
                checkBox =  itemView.findViewById(R.id.rbSelection)
            }

            childName.text = childItem.name.toString()

            // Set up checkbox listener
            /*checkBox.setOnCheckedChangeListener { _, isChecked ->
//                listener.onChildItemChecked(childItem, isChecked) // Notify the listener in Activity
            }*/
        }
    }
}