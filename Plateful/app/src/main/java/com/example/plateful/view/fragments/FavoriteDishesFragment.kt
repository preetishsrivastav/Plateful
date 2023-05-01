package com.example.plateful.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.plateful.application.FavDishApplication
import com.example.plateful.databinding.FragmentFavoriteDishesBinding
import com.example.plateful.model.entities.FavDish
import com.example.plateful.view.activities.MainActivity
import com.example.plateful.view.adapters.FavDishAdapter
import com.example.plateful.viewmodel.FavDishViewModel
import com.example.plateful.viewmodel.FavDishViewModelFactory


class FavoriteDishesFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishesBinding? = null

    /**
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Add an observer on the LiveData returned by getFavoriteDishesList.
         * The onChanged() method fires when the observed data changes and the activity is in the foreground.
         */
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                // Set the LayoutManager that this RecyclerView will use.
                mBinding!!.rvFavoriteDishesList.layoutManager =
                    GridLayoutManager(requireActivity(), 2)
                // Adapter class is initialized and list is passed in the param.
                val adapter = FavDishAdapter(this@FavoriteDishesFragment)
                // adapter instance is set to the recyclerview to inflate the items.
                mBinding!!.rvFavoriteDishesList.adapter = adapter

                if (it.isNotEmpty()) {
                    mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE

                    adapter.dishesList(it)
                } else {
                    mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    /**
     * A function to navigate to the Dish Details Fragment.
     *
     * @param favDish
     */
    fun dishDetails(favDish: FavDish) {

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

        findNavController()
            .navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(favDish))
    }
}