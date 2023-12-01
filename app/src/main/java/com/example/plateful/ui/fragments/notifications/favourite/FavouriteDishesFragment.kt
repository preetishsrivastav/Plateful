package com.example.plateful.ui.fragments.notifications.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plateful.application.PlatefulApplication
import com.example.plateful.databinding.FragmentFavouriteDishesBinding
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.ui.activities.MainActivity
import com.example.plateful.ui.adapters.AllDishesAdapters
import com.example.plateful.viewmodel.PlatefulViewModel
import com.example.plateful.viewmodel.PlatefulViewModelFactory

class FavouriteDishesFragment : Fragment() {

    private var _binding: FragmentFavouriteDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val favouriteViewModel: PlatefulViewModel by viewModels {
        PlatefulViewModelFactory((requireActivity().application as PlatefulApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavouriteDishesBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFavoriteDishesList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        val adapter = AllDishesAdapters(this)
        binding.rvFavoriteDishesList.adapter = adapter

        favouriteViewModel.favouriteDishList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                binding.rvFavoriteDishesList.visibility = View.GONE
            } else {
                adapter.dishes = it
                binding.tvNoFavoriteDishesAvailable.visibility = View.GONE
                binding.rvFavoriteDishesList.visibility = View.VISIBLE
            }
        }



    }
     fun toDishDetail(dish:PlatefulModel){
         findNavController()
             .navigate(FavouriteDishesFragmentDirections.actionNavigationFavouritesToNavigationDishDetail(dish))

         if (requireActivity() is MainActivity) {
             (activity as MainActivity).hideBottomNavView()
         }
    }



    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity).showBottomNavView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}