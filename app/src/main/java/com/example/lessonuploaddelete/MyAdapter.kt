package com.example.lessonuploaddelete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val items: List<ItemData>,
                private val itemInteractionListener: ItemInteractionListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.textViewName.text = currentItem.name
        holder.textViewDescription.text = currentItem.description

        holder.buttonEdit.setOnClickListener {
            itemInteractionListener.onEditItem(currentItem)
        }

        holder.buttonDelete.setOnClickListener {
            itemInteractionListener.onDeleteItem(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    interface ItemInteractionListener {
        fun onEditItem(item: ItemData)
        fun onDeleteItem(item: ItemData)
    }
}
