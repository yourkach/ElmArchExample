package com.ybond.elmtest

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


inline fun <reified VM : ViewModel> Fragment.viewModelFactory(
    crossinline create: () -> VM
): Lazy<VM> = viewModels {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return create() as T
        }
    }
}
