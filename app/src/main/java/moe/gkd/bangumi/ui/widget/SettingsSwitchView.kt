package moe.gkd.bangumi.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ViewSettingsSwitchBinding

class SettingsSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var binding: ViewSettingsSwitchBinding
    private val checked = ObservableBoolean()
    private val title = ObservableField("")


    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_settings_switch, this)
        } else {
            binding = ViewSettingsSwitchBinding.inflate(LayoutInflater.from(context), this, true)
            initViews()
        }
    }

    private fun initViews() {
        binding.title = title
        binding.checked = checked
        binding.switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            setChecked(isChecked)
            listener?.onCheckedChanged(buttonView, isChecked)
        }
    }

    private var listener: CompoundButton.OnCheckedChangeListener? = null
    fun setOnCheckedChangeListener(listener: (buttonView: CompoundButton?, isChecked: Boolean) -> Unit) {
        this.listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            listener.invoke(
                buttonView,
                isChecked
            )
        }
    }

    fun setTitle(str: String) {
        title.set(str)
    }

    fun setChecked(checked: Boolean) {
        this.checked.set(checked)
    }

    fun getChecked(): Boolean {
        return checked.get()
    }
}