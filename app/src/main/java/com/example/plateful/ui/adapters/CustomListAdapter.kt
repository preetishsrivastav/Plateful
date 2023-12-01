package com.example.plateful.ui.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.plateful.databinding.ItemCustomListLayoutBinding
import com.example.plateful.ui.activities.AddUpdateDishesActivity
import com.example.plateful.ui.fragments.notifications.dish.AllDishesFragment

class CustomListItemAdapter(
    private val activity: Activity,
    private val fragment:Fragment,
    private val listItems: List<String>,
    private val selection: String
) :
    RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCustomListLayoutBinding =
            ItemCustomListLayoutBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listItems[position]

        holder.itemCustomListLayoutBinding.tvTitle.text = item

        holder.itemCustomListLayoutBinding.root.setOnClickListener {
            if (fragment is AllDishesFragment){
                fragment.filteredList(item)
            }

        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }


    inner class ViewHolder(val itemCustomListLayoutBinding: ItemCustomListLayoutBinding) :
        RecyclerView.ViewHolder(itemCustomListLayoutBinding.root)
}