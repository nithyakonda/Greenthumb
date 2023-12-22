package com.nkonda.greenthumb.ui

import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.ui.home.TasksListAdapter
import timber.log.Timber

@BindingAdapter("plantImage")
fun bindPlantImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Timber.d("Image Uri ::" + imgUri)
        Glide.with(imageView.context)
            .load(imgUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imageView)
    }
}

@BindingAdapter("plantDetailsFabContentDescription")
fun bindPlantDetailsFabContentDescription(view: FloatingActionButton, saved: Boolean) {
    view.contentDescription = view.context.getString(
        if (saved) R.string.cd_fab_action_remove else R.string.cd_fab_action_add
    )
}

@BindingAdapter("plantDetailsFabImage")
fun bindFabImage(view: FloatingActionButton, saved: Boolean){
    view.setImageResource((if (saved) R.drawable.ic_delete else R.drawable.ic_add))
}

@BindingAdapter("reminderButtonState")
fun bindReminderButtonState(view: ImageButton, saved: Boolean) {
    view.isEnabled = saved
}