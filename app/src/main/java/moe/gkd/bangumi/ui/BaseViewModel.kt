package moe.gkd.bangumi.ui

import androidx.lifecycle.ViewModel
import moe.gkd.bangumi.TAG

abstract class BaseViewModel : ViewModel() {
    protected val TAG = this.TAG()
}