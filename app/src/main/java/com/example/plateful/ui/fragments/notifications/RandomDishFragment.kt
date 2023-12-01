package com.example.plateful.ui.fragments.notifications

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.plateful.R
import com.example.plateful.application.PlatefulApplication
import com.example.plateful.databinding.FragmentRandomDishBinding
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.model.entities.Recipes
import com.example.plateful.viewmodel.PlatefulViewModel
import com.example.plateful.viewmodel.PlatefulViewModelFactory
import com.example.plateful.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var _binding: FragmentRandomDishBinding? = null

    private var mProgressDialog: Dialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private val mPlatefulDishViewModel: PlatefulViewModel by viewModels {
        PlatefulViewModelFactory((requireActivity().application as PlatefulApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]


        mRandomDishViewModel.getRandomDishFromAPI()

        randomDishViewModelObserver()

        binding.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomDishFromAPI()
        }

    }

    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner) { recipe ->
            Log.i("Recipes", "$recipe")
            if (binding.srlRandomDish.isRefreshing) {
                binding.srlRandomDish.isRefreshing = false
            }
            setRandomDishResponseInUi(recipe.recipes[0])
        }
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner) { loading ->

            Log.i("Loading Random Dish", "$loading")
            if (loading && !binding.srlRandomDish.isRefreshing){
                showCustomProgressDialog()
            }else{
                dismissCustomProgressDialog()
            }

        }
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner) { errorLoading ->
            if (binding.srlRandomDish.isRefreshing) {
                binding.srlRandomDish.isRefreshing = false
            }
            Log.i("Error Loading Random Dish", "$errorLoading")
        }

    }


    private fun setRandomDishResponseInUi(recipe: Recipes.RecipeX) {

        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding.ivDishImage)

        binding.tvTitle.text = recipe.title

        // Default Dish Type
        var dishType: String = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding.tvType.text = dishType
        }
        binding.tvCategory.text = "Other"

        var ingredients = ""

        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients += ",\n" + value.original

            }

        }
        binding.tvIngredients.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions, Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        binding.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )

        binding.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavourites = false

        binding.ivFavoriteDish.setOnClickListener {

            if (addedToFavourites) {
                Toast.makeText(
                    requireActivity(), resources.getString(R.string.already_added_to_Favourites),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val randomDishDetail = PlatefulModel(
                    recipe.image,
                    com.example.plateful.utils.Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                mPlatefulDishViewModel.insertDishDetails(randomDishDetail)
                addedToFavourites = true

                binding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                Toast.makeText(requireActivity(), "Dish Added To Favourites", Toast.LENGTH_SHORT)
                    .show()

            }


        }
    }

    fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }
    fun dismissCustomProgressDialog() {
         mProgressDialog?.let {
             it.dismiss()
         }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}