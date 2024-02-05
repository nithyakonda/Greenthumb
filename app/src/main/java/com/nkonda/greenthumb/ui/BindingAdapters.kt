package com.nkonda.greenthumb.ui

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.*
import timber.log.Timber

// Shared
@BindingAdapter("plantImage")
fun bindPlantImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Timber.d("Image Uri ::%s", imgUri)
        Glide.with(imageView.context)
            .load(imgUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.img_default_plant))
            .into(imageView)
    }
}

// Search Screen
@BindingAdapter("searchResultPlantImage")
fun bindSearchResultPlantImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Timber.d("Image Uri ::%s", imgUri)
        Glide.with(imageView.context)
            .load(imgUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.img_default_plant))
            .into(imageView)
    }
}

// Plant Details Screen
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
        button.visibility = if (careLevel != CareLevel.Unknown) View.VISIBLE else View.GONE
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

@BindingAdapter("careLevelTvVisibility")
fun bindCareLevelTvVisibility(view: TextView, careLevel: CareLevel?) {
    view.visibility = View.GONE
    careLevel?.let {
        if( it != CareLevel.Unknown) view.visibility = View.VISIBLE
    }
}

@BindingAdapter("sunlightIconAndContentDesc")
fun bindSunlightIconAndContentDesc(button: MaterialButton, sunlightList: List<Sunlight>?) {
    sunlightList?.let {
        button.visibility = if (sunlightList.isNotEmpty() && !it.any { sunlight -> sunlight == Sunlight.Unknown }) View.VISIBLE else View.GONE
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

@BindingAdapter("sunlightTvVisibility")
fun bindSunlightTvVisibility(view: TextView, sunlightList: List<Sunlight>?) {
    view.visibility = View.GONE
    sunlightList?.let {
        if(it.isNotEmpty() && !it.any { sunlight -> sunlight == Sunlight.Unknown }) view.visibility = View.VISIBLE
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
    view.visibility = if (saved) View.VISIBLE else View.GONE
}

@BindingAdapter("actualScheduleText")
fun bindActualScheduleText(textView: TextView, schedule: Schedule?) {
     schedule?.let {
        if (it.toString().isEmpty()) {
            textView.text = textView.context.getString(R.string.tv_error_actual_schedule)
            textView.setTextAppearance(R.style.GTErrorTextViewStyle)
        } else {
            textView.text = String.format(textView.context.getString(R.string.tv_text_actual_schedule), it.actualScheduleString())
            textView.setTextAppearance(R.style.GTTextViewStyle)
        }
    }
}

// Scheduling Dialog

@BindingAdapter("chipGroupVisibility")
fun bindChipGroupVisibility(chipGroup: ChipGroup, schedule: Schedule?) {
    schedule?.let {
        chipGroup.visibility = if (chipGroup.id == R.id.dayChipGroup) {
            if (schedule is WateringSchedule) View.VISIBLE else View.GONE
        } else {
            if (schedule is PruningSchedule) View.VISIBLE else View.GONE
        }
    }
}
@BindingAdapter("chipChecked")
fun bindChipChecked(chip: Chip, schedule: Schedule?) {
    chip.isChecked = when(chip.id) {
        R.id.sunChip -> schedule?.days?.contains(Day.Sunday) == true
        R.id.monChip -> schedule?.days?.contains(Day.Monday) == true
        R.id.tueChip -> schedule?.days?.contains(Day.Tuesday) == true
        R.id.wedChip -> schedule?.days?.contains(Day.Wednesday) == true
        R.id.thuChip -> schedule?.days?.contains(Day.Thursday) == true
        R.id.friChip -> schedule?.days?.contains(Day.Friday) == true
        R.id.satChip -> schedule?.days?.contains(Day.Saturday) == true
        R.id.janChip -> schedule?.months?.contains(Month.January) == true
        R.id.febChip -> schedule?.months?.contains(Month.February) == true
        R.id.marChip -> schedule?.months?.contains(Month.March) == true
        R.id.aprChip -> schedule?.months?.contains(Month.April) == true
        R.id.mayChip -> schedule?.months?.contains(Month.May) == true
        R.id.junChip -> schedule?.months?.contains(Month.June) == true
        R.id.julChip -> schedule?.months?.contains(Month.July) == true
        R.id.augChip -> schedule?.months?.contains(Month.August) == true
        R.id.sepChip -> schedule?.months?.contains(Month.September) == true
        R.id.octChip -> schedule?.months?.contains(Month.October) == true
        R.id.novChip -> schedule?.months?.contains(Month.November) == true
        R.id.decChip -> schedule?.months?.contains(Month.December) == true
        else -> {false}
    }
}

// Home fragment
@BindingAdapter("taskType")
fun bindTaskType(materialButton: MaterialButton, taskType: TaskType?) {
    materialButton.apply {
        taskType?.let {
            when (it) {
                TaskType.Prune -> {
                    setIconResource(R.drawable.ic_prune)
                    setIconTintResource(R.color.colorOnPruneContainer)
                    setBackgroundColor(context.getColor(R.color.colorPruneContainer))
                }
                TaskType.Water -> {
                    setIconResource(R.drawable.ic_water_frequent)
                    setIconTintResource(R.color.colorOnWaterContainer)
                    setBackgroundColor(context.getColor(R.color.colorWaterContainer))
                }
                TaskType.Custom -> TODO()
            }
        }
    }
}