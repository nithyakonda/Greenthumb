package com.nkonda.greenthumb.ui

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.CareLevel
import com.nkonda.greenthumb.data.Sunlight
import com.nkonda.greenthumb.data.Watering
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
    view.setImageResource((if (saved) R.drawable.ic_delete else R.drawable.ic_save))
}

@BindingAdapter("careLevelIconAndContentDesc")
fun bindCareLevelIconAndContentDesc(button: MaterialButton, careLevel: CareLevel?) {
    careLevel?.let {
        button.contentDescription = String.format(
            button.context.getString(R.string.cd_care_level_button),
            careLevel.toString()
        )
        button.setIconResource(
            when (careLevel) {
                CareLevel.Low -> R.drawable.ic_care_level_low
                CareLevel.Medium -> R.drawable.ic_care_level_medium
                CareLevel.High -> R.drawable.ic_care_level_high
                CareLevel.Unknown -> R.drawable.ic_unknown
            }
        )
    } ?: run {
        button.visibility = View.INVISIBLE
    }
}

@BindingAdapter("sunlightIconAndContentDesc")
fun bindSunlightIconAndContentDesc(button: MaterialButton, sunlightList: List<Sunlight>?) {
    sunlightList?.let {
        button.contentDescription = String.format(button.context.getString(R.string.cd_sunlight_button), sunlightList.toString())
        val sunlight = if (sunlightList.size != 1 &&
                sunlightList.contains(Sunlight.PartShade) ||
                sunlightList.contains(Sunlight.SunPartShade)) {
            Sunlight.PartShade
        } else {
            sunlightList[0]
        }
        button.setIconResource(
            when(sunlight) {
                Sunlight.FullShade -> R.drawable.ic_full_shade
                Sunlight.PartShade -> R.drawable.ic_part_sun
                Sunlight.SunPartShade -> R.drawable.ic_part_sun
                Sunlight.FullSun -> R.drawable.ic_full_sun
                Sunlight.Unknown -> R.drawable.ic_unknown
            }
        )
    }?: run {
        button.visibility = View.INVISIBLE
    }
}

@BindingAdapter("wateringIconAndContentDesc")
fun bindWateringIconAndContentDesc(button: MaterialButton, watering: Watering?) {
    button.setIconResource(R.drawable.ic_water_minimum)
    watering?.let {
        button.contentDescription = String.format(button.context.getString(R.string.cd_watering_button), watering)
        button.setIconResource(
            when(watering) {
                Watering.Frequent -> R.drawable.ic_water_frequent
                Watering.Average -> R.drawable.ic_water_average
                Watering.Minimum -> R.drawable.ic_water_minimum
                Watering.None -> R.drawable.ic_water_none
                Watering.Unknown -> R.drawable.ic_unknown
            }
        )
    }
}

@BindingAdapter("reminderButtonState")
fun bindReminderButtonState(view: Button, saved: Boolean) {
    view.isEnabled = saved
}

@BindingAdapter("viewVisibility")
fun bindViewVisibility(view: View, saved: Boolean) {
    view.visibility = if (saved) View.VISIBLE else View.INVISIBLE
}