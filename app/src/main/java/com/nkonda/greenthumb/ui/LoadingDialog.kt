package com.nkonda.greenthumb.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import com.nkonda.greenthumb.R


class LoadingDialog(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
            window?.let {
                it.apply {
                    setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                    )
                    setBackgroundDrawableResource(R.drawable.loading_background)
                    setGravity(Gravity.CENTER)
                }
            }

    }
}

open class LoadingUtils {
    companion object {
        private var loadingDialog: LoadingDialog? = null
        fun showDialog(context: Context) {
            hideDialog()
            try {
                loadingDialog = LoadingDialog(context)
                loadingDialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hideDialog() {
            if(loadingDialog != null && loadingDialog?.isShowing!!) {
                loadingDialog = try {
                    loadingDialog?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}