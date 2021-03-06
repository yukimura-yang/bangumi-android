package moe.gkd.bangumi.ui.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.DialogEditTextBinding
import moe.gkd.bangumi.databinding.ViewSettingsTextBinding

class SettingsTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var binding: ViewSettingsTextBinding
    private val isEnter = ObservableBoolean()
    private val title = ObservableField("")
    private var content: String = ""
    private var inputType = 0

    fun setContent(str: String) {
        content = str
        isEnter.set(content.isNotEmpty())
        dialogBinding.editText.setText(str)
    }

    fun getContent(): String {
        return content
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_settings_text, this)
        } else {
            binding = ViewSettingsTextBinding.inflate(LayoutInflater.from(context), this, true)
            initViews()
        }
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.SettingsTextView, defStyleAttr, 0)
        inputType = array.getInt(R.styleable.SettingsTextView_inputType, 0)
        setTitle(array.getString(R.styleable.SettingsTextView_title) ?: "")
        array.recycle()
    }

    private var listener: OnInputListener? = null

    /**
     * 保存回调
     */
    public fun setInputListener(listener: (text: String) -> Unit) {
        this.listener = object : OnInputListener {
            override fun onInput(text: String) {
                listener.invoke(text)
            }
        }
    }

    private val dialogBinding by lazy {
        val binding = DialogEditTextBinding.inflate(LayoutInflater.from(context))
        when (inputType) {
            1 -> {
                binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            2 -> {
                binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            else -> {
                binding.editText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
        binding
    }

    private val editTextDialog by lazy {
        AlertDialog.Builder(context)
            .setTitle(title.get())
            .setView(dialogBinding.root)
            .setPositiveButton(
                "确定"
            ) { dialog, _ ->
                val text = dialogBinding.editText.text.toString()
                setContent(text)
                listener?.onInput(text)
                dialog.dismiss()
            }.setNegativeButton("取消", null)
            .create()
    }

    private fun initViews() {
        binding.title = title
        binding.isEnter = isEnter
        binding.button.setOnClickListener {
            editTextDialog.show()
            dialogBinding.editText.setSelection(dialogBinding.editText.text.length)
        }
    }

    fun setTitle(str: String) {
        if (isInEditMode) {
            findViewById<TextView>(R.id.itemTitle).setText(str)
        } else {
            title.set(str)
        }
    }

    public interface OnInputListener {
        fun onInput(text: String)
    }
}