package com.example.plateful.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plateful.R
import com.example.plateful.databinding.ItemDishLayoutBinding
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.ui.activities.AddUpdateDishesActivity
import com.example.plateful.ui.fragments.notifications.dish.AllDishesFragment
import com.example.plateful.ui.fragments.notifications.favourite.FavouriteDishesFragment
import com.example.plateful.utils.Constants

class AllDishesAdapters(private val fragment:Fragment) : RecyclerView.Adapter<AllDishesAdapters.MainViewHolder>() {

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<PlatefulModel>() {
        override fun areItemsTheSame(oldItem: PlatefulModel, newItem: PlatefulModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlatefulModel, newItem: PlatefulModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    var dishes: List<PlatefulModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }


    inner class MainViewHolder(val itemDishLayoutBinding: ItemDishLayoutBinding) :
        RecyclerView.ViewHolder(itemDishLayoutBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemDishLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        holder.itemDishLayoutBinding.apply {
            val dish = dishes[position]

            // Load the dish image in the ImageView.

            Glide.with(fragment)
                .load(dish.image)
                .into(ivDishImage)

            tvDishTitle.text =dish.title


        }
        holder.itemDishLayoutBinding.root.setOnClickListener {
            if (fragment is AllDishesFragment){
                fragment.dishDetails(dishes[position])
            }else if (fragment is FavouriteDishesFragment){
                fragment.toDishDetail(dishes[position])
            }
        }


        holder.itemDishLayoutBinding.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context,holder.itemDishLayoutBinding.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter,popup.menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_edit){
                    val intent =Intent(fragment.requireActivity(),AddUpdateDishesActivity::class.java)
                    intent.putExtra(Constants.DISH_DETAILS,dishes[position])
                    fragment.requireActivity().startActivity(intent)
                }else if (it.itemId == R.id.menu_delete){
                    (fragment as AllDishesFragment).deleteDish(dishes[position])
                    Log.i("Popup","Popup button clicked")
                }
                true
            }
            popup.show()

        }
        if (fragment is AllDishesFragment){
            holder.itemDishLayoutBinding.ibMore.visibility =View.VISIBLE
        }
        if (fragment is FavouriteDishesFragment){
            holder.itemDishLayoutBinding.ibMore.visibility = View.GONE

        }

    }

    override fun getItemCount(): Int {
      return dishes.size
     }
}