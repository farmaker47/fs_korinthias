package com.george.fs_korinthias

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {
    private val _selectedVideo = MutableLiveData<String>()

    private var _messagesList = MutableLiveData<ArrayList<FirebaseMainActivityMessages?>>()
    val titleMessages: LiveData<ArrayList<FirebaseMainActivityMessages?>>
        get() = _messagesList

    init {
        _selectedVideo.value = app.getString(R.string.video)
        _messagesList.value = ArrayList()
    }

    fun setArratListMainActivityMessages(list: ArrayList<FirebaseMainActivityMessages?>) {
        _messagesList.value = list
        Log.e("Messages_List", _messagesList.value!![0]?.message)
        Log.e("Messages_List", _messagesList.value!!.size.toString())
    }
}