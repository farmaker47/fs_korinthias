/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.george.fs_korinthias.classifier

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.call
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SmartReplyClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    var isInitialized = false
        private set

    /** Executor to run inference task in the background. */
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    private var inputVector: Int = 0 // will be inferred from TF Lite model.
    private var inputImageHeight: Int = 0 // will be inferred from TF Lite model.
    private var modelInputSize: Int = 0 // will be inferred from TF Lite model.

    fun initialize(): Task<Void> {
        return call(
            executorService,
            Callable<Void> {
                initializeInterpreter()
                null
            }
        )
    }

    @Throws(IOException::class)
    private fun initializeInterpreter() {
        // Load the TF Lite model from asset folder and initialize TF Lite Interpreter with NNAPI enabled.
        val assetManager = context.assets
        val model = loadModelFile(assetManager, "greek_smart_reply_model.tflite")
        val options = Interpreter.Options()
        options.setUseNNAPI(false)
        val interpreter = Interpreter(model, options)

        // Reads type and shape of input and output tensors, respectively.
        val imageTensorIndex = 0
        val imageShape: IntArray =
            interpreter.getInputTensor(imageTensorIndex).shape() // {1, length}
        //inputVector = imageShape[0]
        Log.e("INPUT_TENSOR_WHOLE", Arrays.toString(imageShape))
        //Log.e("INPUT_TENSOR_0", imageShape[0].toString())
        //Log.e("INPUT_TENSOR_1", imageShape[1].toString())
        val imageDataType: DataType =
            interpreter.getInputTensor(imageTensorIndex).dataType()

        Log.e("INPUT_DATA_TYPE", imageDataType.toString())


        val probabilityTensorIndex = 0
        val probabilityShape =
            interpreter.getOutputTensor(probabilityTensorIndex).shape()// {1, NUM_CLASSES}
        //interpreter.getOutputIndex("output_1")

        /*val floatArray = FloatArray(probabilityShape.length)
        for (i in 0 until doubleArray.length) {
            floatArray[i] = doubleArray.get(i)
        }*/

        Log.e("OUTPUT_TENSOR_SHAPE", Arrays.toString(probabilityShape))

        //Log.e("OUTPUT_TENSOR_0", probabilityShape!![0].toString())
        //Log.e("OUTPUT_TENSOR_1", probabilityShape[1].toString())
        val probabilityDataType: DataType =
            interpreter.getOutputTensor(probabilityTensorIndex).dataType()
        Log.e("OUTPUT_DATA_TYPE", probabilityDataType.toString())

        // Creates the input tensor.
        //inputImageBuffer = TensorImage(imageDataType)
        // Creates the output tensor and its processor.
        //outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)


        //val inputShape = interpreter.getInputTensor(0).shape()
        //inputVector = inputShape[0]
        //inputImageHeight = inputShape[2]
        //modelInputSize = FLOAT_TYPE_SIZE * inputVector * inputImageHeight * PIXEL_SIZE
        // Finish interpreter initialization.
        this.interpreter = interpreter

        isInitialized = true
        Log.e(TAG, "Initialized TFLite interpreter.")


        //val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        //val output = FloatArray(0)

        val byteBuffer = ByteBuffer.allocateDirect(4 * 40)
        byteBuffer.order(ByteOrder.nativeOrder())
        /*byteBuffer.putFloat(0F)
        byteBuffer.putFloat(0F)*/


        //val pixels = IntArray(40)
        for (i in 0..39) {
            val value = 0F
            byteBuffer.putFloat(value)
        }
        byteBuffer.rewind();
        //val outputBuffer = byteBuffer.asFloatBuffer().array();

        // Run inference with the input data.
        val flatArray = FloatArray(40) { 1F }
        //Log.e("INPUT_TENSOR_MINE", flatArray.contentToString())

        // 2D
        //val m = Array(1) { FloatArray(40) { 0F } }
        // val m = Array(1) { FloatArray(INPUT_CLASSES_COUNT){1570F} }
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
        Log.e("INPUT_TENSOR_M", Arrays.toString(m[0]))
        Log.e("INPUT_TENSOR_M_SIZE", m[0].size.toString())
        val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        interpreter.run(m, output)

        // Post-processing: find the digit that has the highest probability
        // and return it a human-readable string.
        val result = output[0]
        Log.e("RESULT", Arrays.toString(result))
        val maxIndex = result.indices.maxBy { result[it] } ?: -1
        Log.e("MAX_INDEX", maxIndex.toString())
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

    private fun classify(bitmap: Bitmap): String {
        check(isInitialized) { "TF Lite Interpreter is not initialized yet." }

        // TODO: Add code to run inference with TF Lite.
        // Pre-processing: resize the input image to match the model input shape.
        val resizedImage = Bitmap.createScaledBitmap(
            bitmap,
            inputVector,
            inputImageHeight,
            true
        )
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        // Define an array to store the model output.
        val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }

        // Run inference with the input data.
        interpreter?.run(byteBuffer, output)

        // Post-processing: find the digit that has the highest probability
        // and return it a human-readable string.
        val result = output[0]
        val maxIndex = result.indices.maxBy { result[it] } ?: -1
        val resultString =
            "Prediction Result: %d\nConfidence: %2f"
                .format(maxIndex, result[maxIndex])

        return resultString
    }

    fun classifyAsync(bitmap: Bitmap): Task<String> {
        return call(executorService, Callable<String> { classify(bitmap) })
    }

    fun close() {
        call(
            executorService,
            Callable<String> {
                // TODO: close the TF Lite interpreter here
                interpreter?.close()

                Log.d(TAG, "Closed TFLite interpreter.")
                null
            }
        )
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputVector * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            // Convert RGB to grayscale and normalize pixel value to [0..1].
            val normalizedPixelValue = (r + g + b) / 3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }

    companion object {
        private const val TAG = "DigitClassifier"

        private const val FLOAT_TYPE_SIZE = 4
        private const val PIXEL_SIZE = 1

        private const val OUTPUT_CLASSES_COUNT = 3
        private const val INPUT_CLASSES_COUNT = 40
    }
}
