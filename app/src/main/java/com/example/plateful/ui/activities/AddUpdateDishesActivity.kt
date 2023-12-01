package com.example.plateful.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.plateful.R
import com.example.plateful.application.PlatefulApplication
import com.example.plateful.databinding.ActivityAddDishesBinding
import com.example.plateful.databinding.DialogCustomImageSelectionBinding
import com.example.plateful.model.entities.PlatefulModel
import com.example.plateful.utils.Constants
import com.example.plateful.viewmodel.PlatefulViewModel
import com.example.plateful.viewmodel.PlatefulViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddUpdateDishesActivity : AppCompatActivity() {

    private var mDishDetails: PlatefulModel? = null
    private lateinit var mBinding: ActivityAddDishesBinding
    private var mImagePath: String = ""
    private val mPlatefulViewModel: PlatefulViewModel by viewModels {
        PlatefulViewModelFactory((application as PlatefulApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddDishesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.DISH_DETAILS)) {
            mDishDetails = intent.getParcelableExtra(Constants.DISH_DETAILS)
        }

        setupActionBar()

        mDishDetails?.let {
            if (it.id != 0) {
                mImagePath = it.image
                Glide.with(this)
                    .load(mImagePath)
                    .centerCrop()
                    .into(mBinding.ivDishImage)

                mBinding.etTitle.setText(it.title)
                mBinding.tvType.setText(it.type)
                mBinding.tvCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.tvCookingTime.setText(it.cooking_time)
                mBinding.etDirectionToCook.setText(it.direction_to_cook)

                mBinding.btnAddDish.text = resources.getString(R.string.update_dish)
            }
        }

        mBinding.ivAddDishImage.setOnClickListener {
            customImageSelectionDialog()
        }


        val dishType = resources.getStringArray(R.array.dish_type)
        val dishTypeAdapter = ArrayAdapter(this, R.layout.drop_down_menu, dishType)

        val dishCategory = resources.getStringArray(R.array.dish_category)
        val dishCategoryAdapter = ArrayAdapter(this, R.layout.drop_down_menu, dishCategory)

        val cookingTime = resources.getStringArray(R.array.cooking_time)
        val dishCookingAdapter = ArrayAdapter(this, R.layout.drop_down_menu, cookingTime)

//        Setting Up Dish Type Options
        mBinding.tvType.setOnClickListener {

            mBinding.tvType.setAdapter(dishTypeAdapter)
        }
//        Setting Up Dish Category Options

        mBinding.tvCategory.setOnClickListener {

            mBinding.tvCategory.setAdapter(dishCategoryAdapter)
        }
//        Setting Up Cooking Time Options
        mBinding.tvCookingTime.setOnClickListener {

            mBinding.tvCookingTime.setAdapter(dishCookingAdapter)
        }

        mBinding.btnAddDish.setOnClickListener {
            validateTextFieldsAndInsertData()
        }

    }

    //    This Function will first validate all the entries and then insert it in the Database
    private fun validateTextFieldsAndInsertData() {
        val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
        val type = mBinding.tvType.text.toString().trim { it <= ' ' }
        val category = mBinding.tvCategory.text.toString().trim { it <= ' ' }
        val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }
        val cookingTimeInMinutes = mBinding.tvCookingTime.text.toString().trim { it <= ' ' }
        val cookingDirection = mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }


        when {

            TextUtils.isEmpty(mImagePath) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_image),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(title) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_title),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(type) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_type),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(category) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_category),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(ingredients) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_ingredients),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(cookingTimeInMinutes) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_cooking_time),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(cookingDirection) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                var dishId = 0
                var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                var favouriteDish = false

                mDishDetails?.let {
                    if (it.id != 0)
                        dishId = it.id
                    imageSource = it.imageSource
                    favouriteDish = it.favorite_dish
                }

                // START
                val favDishDetails: PlatefulModel = PlatefulModel(
                    mImagePath,
                    imageSource,
                    title,
                    type,
                    category,
                    ingredients,
                    cookingTimeInMinutes,
                    cookingDirection,
                    favouriteDish,
                    dishId
                )
                // END

                // START
                if (dishId == 0) {
                    mPlatefulViewModel.insertDishDetails(favDishDetails)

                    Toast.makeText(
                        this,
                        "You successfully added your favorite dish details.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // You even print the log if Toast is not displayed on emulator
                    Log.e("Insertion", "Success")
                } else {
                    mPlatefulViewModel.updateDishDetails(favDishDetails)
                    Toast.makeText(
                        this,
                        "You successfully Updated your favorite dish details.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                // Finish the Activity
                finish()
                // END
            }

        }
    }


    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if (mDishDetails != null && mDishDetails!!.id != 0) {
            supportActionBar?.let {
                it.title = resources.getString(R.string.edit_dish)
            }
        } else {
            supportActionBar?.let {
                it.title = resources.getString(R.string.add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }

    //    Showing Rationale If user Denied the permission
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishesActivity)

        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this@AddUpdateDishesActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Here after all the permission are granted launch the CAMERA to capture an image.
                        report?.let {
                            if (report.areAllPermissionsGranted()) {

                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {

            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        // START
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                        // END
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            this@AddUpdateDishesActivity,
                            "You have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalDialogForPermissions()
                    }
                })
                .onSameThread()
                .check()

            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap =
                        data.extras!!.get("data") as Bitmap //Bitmap from Camera

//                    Setting Image from camera to the image view
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mImagePath = saveFileInternally(thumbnail)

                }

            }
            if (requestCode == GALLERY) {
                data?.let {
                    val selectedImageUri = data.data


                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("LOAD Failed", "FIle Cannot be loaded")
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
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveFileInternally(bitmap)
                                    Log.i("ImagePath", mImagePath)
                                }
                                return false
                            }
                        })
                        .into(mBinding.ivDishImage)
                }


            }


        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }

    }

    private fun saveFileInternally(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(this)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.i("File Path", file.absolutePath)

        return file.absolutePath

    }


    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "PlatefulImages"

        // END
    }
}