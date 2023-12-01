package com.example.plateful.ui.fragments.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.plateful.R
import com.example.plateful.application.PlatefulApplication
import com.example.plateful.databinding.FragmentDishDetailsBinding
import com.example.plateful.utils.Constants
import com.example.plateful.viewmodel.PlatefulViewModel
import com.example.plateful.viewmodel.PlatefulViewModelFactory
import java.io.IOException
import java.util.Locale


class DishDetailsFragment : Fragment() {
    private var mBinding: FragmentDishDetailsBinding? = null

    private val mDetailViewModel: PlatefulViewModel by viewModels {
        PlatefulViewModelFactory((requireActivity().application as PlatefulApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentDishDetailsBinding.inflate(layoutInflater)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()

        requireActivity().addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.share_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.menu_share->{
                        val type = "text/plain"
                        val subject = "Checkout This New Recepie"
                        var extraText =""
                        val shareWith = "Share With"

                        args?.let {
                            var image = ""

                            if (it.dishDetail.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE){
                                image = it.dishDetail.image
                            }

                            var cookingInstruction =""

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                cookingInstruction = Html.fromHtml(it.dishDetail.direction_to_cook,Html.FROM_HTML_MODE_COMPACT).toString()
                            }
                            else{
                                @Suppress("DEPRECATION")
                                cookingInstruction = Html.fromHtml(it.dishDetail.direction_to_cook).toString()
                            }
                            extraText =
                                "$image \n" +
                                        "\n Title:  ${it.dishDetail.title} \n\n Type: ${it.dishDetail.type} \n\n Category: ${it.dishDetail.category}" +
                                        "\n\n Ingredients: \n ${it.dishDetail.ingredients} \n\n Instructions To Cook: \n $cookingInstruction" +
                                        "\n\n Time required to cook the dish approx ${it.dishDetail.cooking_time} minutes."
                        }

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = type
                        intent.putExtra(Intent.EXTRA_SUBJECT,subject)
                        intent.putExtra(Intent.EXTRA_TEXT,extraText)
                        startActivity(Intent.createChooser(intent,shareWith))

                        return true
                    }

                }
                return false

            }
        },viewLifecycleOwner)

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetail.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.i("Pallete Image", "No image inserted")
                            return false

                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {

                                Palette.from(resource.toBitmap()).generate() { pallete ->

                                    val intColor = pallete?.vibrantSwatch?.rgb ?: 0
                                    mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                                }


                            }


                            return false
                        }
                    })
                    .into(mBinding!!.ivDishImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }

//            Check If the dish is already selected as favourites then to still show favourite as selected
            if (args.dishDetail.favorite_dish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), R.drawable.ic_favorite_selected
                    )
                )
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), R.drawable.ic_favorite_unselected
                    )
                )
            }

            mBinding!!.tvTitle.text = it.dishDetail.title
            mBinding!!.tvType.text =
                it.dishDetail.type.replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.ROOT)
                    else it.toString()
                } // Used to make first letter capital
            mBinding!!.tvCategory.text = it.dishDetail.category
            mBinding!!.tvIngredients.text = it.dishDetail.ingredients
            mBinding!!.tvCookingDirection.text = Html.fromHtml( it.dishDetail.direction_to_cook,Html.FROM_HTML_MODE_COMPACT)
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetail.cooking_time)

            mBinding!!.ivFavoriteDish.setOnClickListener {
//                Whatever be the value of favourite dish either tru or false chage
                args.dishDetail.favorite_dish = !args.dishDetail.favorite_dish
                mDetailViewModel.updateDishDetails(args.dishDetail)


                if (args.dishDetail.favorite_dish) {
                    mBinding!!.ivFavoriteDish.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(), R.drawable.ic_favorite_selected
                        )
                    )
                    (Toast.makeText(
                        requireActivity(),
                        "Item Added to favorites",
                        Toast.LENGTH_SHORT
                    )).show()
                } else {
                    mBinding!!.ivFavoriteDish.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(), R.drawable.ic_favorite_unselected
                        )
                    )
                    (Toast.makeText(
                        requireActivity(),
                        "Item Removed from favorites",
                        Toast.LENGTH_SHORT
                    )).show()
                }


            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }


}