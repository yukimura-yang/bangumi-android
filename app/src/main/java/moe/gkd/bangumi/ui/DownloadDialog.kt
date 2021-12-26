package moe.gkd.bangumi.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.databinding.ObservableLong
import moe.gkd.bangumi.databinding.DialogDownloadBinding

class DownloadDialog(
    fileName: String,
    length: Long,
    context: Context,
    progress: ObservableLong
) :
    Dialog(context) {
    private val binding = DialogDownloadBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        binding.fileName = fileName
        binding.length = length
        binding.progress = progress
        binding.cancel.setOnClickListener {
            listener?.onClick(this, BUTTON_NEGATIVE)
        }
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    private var listener: DialogInterface.OnClickListener? = null
    fun setOnClickedListener(listener: DialogInterface.OnClickListener): DownloadDialog {
        this.listener = listener
        return this
    }
}