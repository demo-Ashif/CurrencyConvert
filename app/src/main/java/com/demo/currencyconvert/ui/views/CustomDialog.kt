package com.demo.currencyconvert.ui.views

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.demo.currencyconvert.R

class CustomDialog {
    companion object {
        fun showSuccessDialog(activity: Activity?, fromAmount : String, toAmount : String, commissionAmount : String ) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val dialogMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
            var formattedText = activity.getString(R.string.tv_dialog_message, fromAmount, toAmount, commissionAmount)
            dialogMessage.text = formattedText
            val dialogDone = dialog.findViewById<TextView>(R.id.txt_done)
            dialogDone.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}