package com.nkonda.greenthumb.data

data class Task constructor(
    var plantId: String,
    var isCompleted: Boolean,
    var type: String, // todo change to enum of water/prune
    var day: String,
    var time: String,
    var repeats: String,
    var customType: String = ""
) {
}