package com.george.fs_korinthias

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    private val _inputText = MutableLiveData<String>()

    // The external LiveData for the SelectedNews
    val inputText: LiveData<String>
        get() = _inputText

    private val _selectedVideo = MutableLiveData<String>()

    private var _messagesList = MutableLiveData<ArrayList<FirebaseMainActivityMessages?>>()
    val titleMessages: LiveData<ArrayList<FirebaseMainActivityMessages?>>
        get() = _messagesList

    private var _numberMessages = MutableLiveData<Int>()
    val numberMessages: LiveData<Int>
        get() = _numberMessages

    private lateinit var interpreter: Interpreter
    private var vocabMap: Map<String, Int>? = null
    private var finalWords: ArrayList<String>? = null
    private var wordsTrancuated: List<String>? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        _selectedVideo.value = app.getString(R.string.video)
        Log.i("KOIN_VIDEO", _selectedVideo.value)
        _messagesList.value = ArrayList()
        _numberMessages.value = 0

        // Initialize Interpreter
        viewModelScope.launch {
            initializeInterpreter(app)
        }

    }

    /*fun setTextFromEditText(text: String, context: Context) {
        _inputText.value = text
        Log.e("InputText", inputText.value)

    }*/

    private fun classifyText(floatArray: FloatArray?) {
        coroutineScope.launch {

            val arrayFinal = Array(1) { floatArray }

            val m = Array(1) {
                floatArrayOf(
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    0F,
                    164F,
                    1F,
                    757F,
                    10F,
                    245F,
                    501F
                )
            }
            Log.e("INPUT_TENSOR_M", arrayFinal[0]?.contentToString())
            Log.e("INPUT_TENSOR_M_SIZE", arrayFinal[0]?.size.toString())
            val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
            interpreter.run(arrayFinal, output)

            // Post-processing: find the digit that has the highest probability
            // and return it a human-readable string.
            val result = output[0]
            Log.e("RESULT", Arrays.toString(result))
            val maxIndex = result.indices.maxBy { result[it] } ?: -1
            Log.e("MAX_INDEX", maxIndex.toString())

        }
    }

    fun setArrayListMainActivityMessages(
        list: ArrayList<FirebaseMainActivityMessages?>,
        context: Context
    ) {
        _messagesList.value = list
        //Log.e("Messages_List", _messagesList.value!![1]?.message)
        Log.i("Messages_List", _messagesList.value!!.size.toString())
        _numberMessages.value = _messagesList.value?.size

        // Transform text
        // Classify text from the last input message from list
        if(list.size!=0){
            classifyText(transformText(list[list.size - 1]?.message!!, context))
        }

    }

    @Throws(IOException::class)
    private suspend fun initializeInterpreter(app: Application) = withContext(Dispatchers.IO) {
        // Load the TF Lite model from asset folder and initialize TF Lite Interpreter with NNAPI enabled.
        val assetManager = app.assets
        val model = loadModelFile(assetManager, "greek_smart_reply_model.tflite")
        val options = Interpreter.Options()
        options.setUseNNAPI(false)
        interpreter = Interpreter(model, options)
        // Reads type and shape of input and output tensors, respectively.
        val imageTensorIndex = 0
        val imageShape: IntArray =
            interpreter.getInputTensor(imageTensorIndex).shape() // {1, length}
        Log.i("INPUT_TENSOR_WHOLE", Arrays.toString(imageShape))
        val imageDataType: DataType =
            interpreter.getInputTensor(imageTensorIndex).dataType()
        Log.i("INPUT_DATA_TYPE", imageDataType.toString())
        val probabilityTensorIndex = 0
        val probabilityShape =
            interpreter.getOutputTensor(probabilityTensorIndex).shape()// {1, NUM_CLASSES}
        Log.i("OUTPUT_TENSOR_SHAPE", Arrays.toString(probabilityShape))
        val probabilityDataType: DataType =
            interpreter.getOutputTensor(probabilityTensorIndex).dataType()
        Log.i("OUTPUT_DATA_TYPE", probabilityDataType.toString())
        Log.i(TAG, "Initialized TFLite interpreter.")
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, filename: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun transformText(textToFormat: String, context: Context): FloatArray? {

        Log.i("TEXT", textToFormat)
        //Replace Upper case letters,remove punctuation and split string
        /*val words =
            textToFormat//.replace("[^a-zA-Z ]".toRegex(), "").toLowerCase()
                .split("\\s+")
                .toTypedArray()*/
        val words = textToFormat.split("\\s+".toRegex()).map { word ->
            word.replace("""^[,;!?.]|[,;!?.]$""".toRegex(), "").toLowerCase()
        }

        for (word in words) {
            Log.i("WORDS", word)
        }

        //Initialize an input array with maxSize length
        val input =
            FloatArray(MainActivity.maxLenght) // 1 sentence by maxLenWords
        //Make every position 0
        for (l in 0 until MainActivity.maxLenght) {
            input[l] = 0F
        }

        val vocabJson: String?
        return try {
            //Open .json file
            val br =
                BufferedReader(InputStreamReader(context.assets.open(MainActivity.vocabFilename)))
            var line: String?
            val sb = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            vocabJson = sb.toString()
            br.close()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val type =
                object : TypeToken<Map<String?, Int?>?>() {}.type
            //Create mapped vocabulary
            vocabMap = gson.fromJson<Map<String, Int>>(vocabJson, type)

            //////////////////////////////////////////////////////////////
            //Find words that exist in vocabulary
            var p = 0
            finalWords = ArrayList()
            for (word in words) {
                if (vocabMap!!.containsKey(word)) {
                    finalWords?.add(word)
                    p++
                }
            }
            Log.i("LENGTH", finalWords?.size.toString())
            //////////////////////////////////////////////////////////////
            //Trancuate
            if (finalWords!!.size >= 40) {
                wordsTrancuated = finalWords?.subList(0, 40)
            } else {
                wordsTrancuated = finalWords?.subList(0, finalWords!!.size)
            }
            /////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////
            //Padding sequence of maxSize length with integers of final words
            var j = 0
            for (word in wordsTrancuated!!) {
                if (j == MainActivity.maxLenght) break

                if (vocabMap!!.containsKey(word)) {
                    val index = vocabMap?.get(word)

                    //Making integer to float
                    input[input.size - wordsTrancuated!!.size + j] = index!!.toFloat()
                    j++
                }
            }
            /////////////////////////////////////////////////////

            //Check all input array
            for (k in input) {
                Log.i("ArrayWords", k.toString())
            }

            Log.i("SIZE_INPUT", input.size.toString())
            Log.i("Words_INPUT_whole", input.contentToString())

            input
        } catch (e: Exception) {
            Log.e("exception", e.toString())
            FloatArray(40) { 0F }
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"

        private const val FLOAT_TYPE_SIZE = 4
        private const val PIXEL_SIZE = 1

        private const val OUTPUT_CLASSES_COUNT = 3
        private const val INPUT_CLASSES_COUNT = 40
    }
}