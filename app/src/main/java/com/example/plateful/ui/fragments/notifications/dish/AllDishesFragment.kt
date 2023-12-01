package com.example.plateful.ui.fragments.notifications.dish

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plateful.R
import com.example.plateful.application.PlatefulApplication
import com.example.plateful.databinding.CustomListLayoutBinding
import com.example.plateful.databinding.FragmentAllDishesBinding
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.ui.adapters.AllDishesAdapters
import com.example.plateful.ui.activities.AddUpdateDishesActivity
import com.example.plateful.ui.activities.MainActivity
import com.example.plateful.ui.adapters.CustomListItemAdapter
import com.example.plateful.utils.Constants
import com.example.plateful.viewmodel.PlatefulViewModel
import com.example.plateful.viewmodel.PlatefulViewModelFactory

class AllDishesFragment : Fragment() {

    private var _binding: FragmentAllDishesBinding? = null
     private lateinit var mCustomDialog: Dialog
     private lateinit var mAllDishesAdapter: AllDishesAdapters


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val allDishesViewModel: PlatefulViewModel by viewModels {
        PlatefulViewModelFactory((requireActivity().application as PlatefulApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_all_dishes, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_filter_dishes -> {
                        filterListCustomDialog()
                        return true
                    }

                }
                return false
            }
        }, viewLifecycleOwner)

        binding.fab.setOnClickListener {
            val intent = Intent(requireActivity(), AddUpdateDishesActivity::class.java)
            startActivity(intent)
        }
        binding.rvDishes.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        mAllDishesAdapter = AllDishesAdapters(this)
        binding.rvDishes.adapter = mAllDishesAdapter

        allDishesViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (dishes.isNotEmpty()) {
                    binding.rvDishes.visibility = View.VISIBLE
                    binding.tvNoDishesAddedYet.visibility = View.GONE
                    mAllDishesAdapter.dishes = it
                } else {
                    binding.rvDishes.visibility = View.GONE
                    binding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }


    }

    private fun filterListCustomDialog() {
        mCustomDialog= Dialog(requireActivity())
        val binding: CustomListLayoutBinding = CustomListLayoutBinding.inflate(layoutInflater)

        val dishType = Constants.dishTypes()
        dishType.add(0, Constants.ALL_ITEMS)
        mCustomDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        binding.rvList.layoutManager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvList.adapter = CustomListItemAdapter(
            requireActivity(), this@AllDishesFragment,
            dishType, Constants.FILTER_SELECTION
        )
        mCustomDialog.show()
    }

     fun filteredList(dishType:String){
        mCustomDialog.dismiss()

        if (dishType == Constants.ALL_ITEMS){
            allDishesViewModel.allDishesList.observe(viewLifecycleOwner){
                dishes->
                if (dishes.isEmpty()){
                    binding.rvDishes.visibility = View.GONE
                    binding.tvNoDishesAddedYet.visibility = View.VISIBLE

                }else{
                    binding.rvDishes.visibility = View.VISIBLE
                    binding.tvNoDishesAddedYet.visibility = View.GONE
                    mAllDishesAdapter.dishes = dishes
                }


            }

        }else{
            allDishesViewModel.filteredList(dishType).observe(viewLifecycleOwner){

                filteredDishes ->

                if (filteredDishes.isEmpty()){
                    binding.rvDishes.visibility = View.GONE
                    binding.tvNoDishesAddedYet.visibility = View.VISIBLE

                }else{
                    binding.rvDishes.visibility = View.VISIBLE
                    binding.tvNoDishesAddedYet.visibility = View.GONE
                    mAllDishesAdapter.dishes = filteredDishes
                }

            }

        }


    }

    //    This function will take us to the dish detail screen
    fun dishDetails(dish: PlatefulModel) {
        findNavController().navigate(
            AllDishesFragmentDirections.actionNavigationDishesToNavigationDishDetail(
                dish
            )
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).hideBottomNavView()
        }

    }

    fun deleteDish(dish: PlatefulModel) {
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(resources.getString(R.string.title_delete_dish))
            .setIcon(R.drawable.ic_warning)
            .setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
            .setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
                allDishesViewModel.deleteDish(dish)
                dialogInterface.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity).showBottomNavView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}