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
        fun showSuccessDialog(
            activity: Activity?,
            from: String,
            fromAmount: String,
            to: String,
            toAmount: String,
            commissionMsg: String
        ) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvTransferredAmount = dialog.findViewById<TextView>(R.id.tvTransferredAmount)
            val tvReceivedAmount = dialog.findViewById<TextView>(R.id.tvReceivedAmount)
            val tvDialogCommission = dialog.findViewById<TextView>(R.id.tvDialogCommission)

            tvTransferredAmount.text =
                activity.getString(R.string.tv_dialog_transfer_message, from, fromAmount)
            tvReceivedAmount.text =
                activity.getString(R.string.tv_dialog_receive_message, to, toAmount)

            tvDialogCommission.text = commissionMsg


            val dialogDone = dialog.findViewById<TextView>(R.id.txt_done)
            dialogDone.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}