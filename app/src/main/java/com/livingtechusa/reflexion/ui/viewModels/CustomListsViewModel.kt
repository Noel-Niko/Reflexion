package com.livingtechusa.reflexion.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.livingtechusa.reflexion.data.localService.LocalServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CustomListsViewModel @Inject constructor(
    private val localServiceImpl: LocalServiceImpl,
    private val state: SavedStateHandle
) : ViewModel() {

}