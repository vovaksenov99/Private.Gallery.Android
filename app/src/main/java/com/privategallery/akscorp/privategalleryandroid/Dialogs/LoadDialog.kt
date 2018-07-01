package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.DialogFragment;
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.progress_dialog.*
import android.os.Handler
import android.support.constraint.ConstraintLayout
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.runOnUiThread


val LOAD_DIALOG_TAG = "LOAD_DIALOG_TAG"

class LoadDialog : DialogFragment()
{


    var progressBarShow: Boolean = true

    override fun onStart()
    {
        super.onStart()

        dialog.setCancelable(false)

        if (!progressBarShow)
            (dialog.progress_dialog_root as ConstraintLayout).removeView(dialog.progressBar)

        if (dialog != null)
        {
            dialog.window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun delayDismiss(action: () -> Unit = {})
    {
        launch(UI) {
            Handler().postDelayed({
                dialog.dismiss()
                action()
            }, 1500)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View?
    {
        super.onCreateView(inflater, parent, state)

        return activity!!.layoutInflater.inflate(R.layout.progress_dialog, null)
    }

    fun progressBroadcastReceiverInit(progressDialog: LoadDialog)
    {
        val intentFilter = IntentFilter(PROGRESS_BROADCAST_RECEIVER_TAG)
        val mReceiver = ProgressBroadcastReceiver(
            progressDialog)
        activity!!.registerReceiver(mReceiver, intentFilter)
    }

    fun sentProgressToReceiver(progress: Int)
    {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(
            CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        activity!!.sendBroadcast(intent)
    }
}

val PROGRESS_BROADCAST_RECEIVER_TAG = "PROGRESS_BROADCAST_RECEIVER_TAG"
val CURRENT_PROGRESS_BROADCAST_RECEIVER = "CURRENT_PROGRESS_BROADCAST_RECEIVER"

class ProgressBroadcastReceiver(val dialog: LoadDialog) : BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent)
    {
        context.runOnUiThread {
            val progress = intent.getIntExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, 100)
            if (dialog.dialog != null)
            {
                if (dialog.dialog.progressBar != null)
                {
                    dialog.dialog.progressBar.progress = progress

                    if (progress == 100)
                    {
                        val animAccelerate = AnimationUtils.loadAnimation(
                            context,
                            R.anim.done_anim
                        )
                        dialog.dialog.progress_status.text = context.getString(R.string.done)
                        dialog.dialog.done_point.startAnimation(animAccelerate)
                    }
                }
            }
        }
    }
}