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

    private var _numberMessages = MutableLiveData<Int>()
    val numberMessages: LiveData<Int>
        get() = _numberMessages

    init {
        _selectedVideo.value = app.getString(R.string.video)
        Log.i("KOIN_VIDEO", _selectedVideo.value)
        _messagesList.value = ArrayList()
        _numberMessages.value = 0
    }

    fun setArratListMainActivityMessages(list: ArrayList<FirebaseMainActivityMessages?>) {
        _messagesList.value = list
        //Log.e("Messages_List", _messagesList.value!![1]?.message)
        Log.i("Messages_List", _messagesList.value!!.size.toString())
        _numberMessages.value = _messagesList.value?.size
    }
}