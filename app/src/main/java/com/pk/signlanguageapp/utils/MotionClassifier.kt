import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.flex.FlexDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MotionClassifier(context: Context, modelPath: String) {

    private var interpreter: Interpreter
    val classes: List<String>
    val NumberOfClasses : Int
    val threshold = 0.5f
    init {
        val options = Interpreter.Options().apply {
            // Menambahkan Flex Delegate
            addDelegate(FlexDelegate())
        }
        classes = getClasses("MotionClasses.json", context)
        NumberOfClasses = classes.count()
        interpreter = Interpreter(loadModelFile(context, modelPath), options)
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(input: List<List<Float>>): String {
        try {
            val inputFloatArray = list2DToFloatArray(input)
            val inputBuffer = prepareInputBuffer(inputFloatArray, intArrayOf(1, 30, 126))

            val outputShape = intArrayOf(1, 217)
            val outputBuffer = ByteBuffer.allocateDirect(4 * outputShape.reduce { acc, i -> acc * i })
            outputBuffer.order(ByteOrder.nativeOrder())
            interpreter.run(inputBuffer, outputBuffer)

            outputBuffer.rewind()
            val outputArray = FloatArray(outputShape.reduce { acc, i -> acc * i })
            outputBuffer.asFloatBuffer().get(outputArray)
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

            if (outputArray[maxIndex] < threshold){
                return "none"
            }
            return classes[maxIndex]
        }catch (e: Exception){
            return e.message.toString()
        }
    }

    fun close() {
        interpreter.close()
    }

    private fun getClasses(fileName: String, context: Context): List<String> {
        val gson = Gson()
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val listItemType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(jsonString, listItemType)
    }

    fun list2DToFloatArray(list: List<List<Float>>): FloatArray {
        if (list.size != 30 || list.any { it.size != 126 }) {
            throw IllegalArgumentException("Shape of input list must be [30, 126]")
        }

        val floatArray = FloatArray(30 * 126)
        var index = 0

        for (i in list.indices) {
            for (j in list[i].indices) {
                floatArray[index] = list[i][j]
                index++
            }
        }

        return floatArray
    }

    fun prepareInputBuffer(floatArray: FloatArray, shape: IntArray): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * shape.reduce { acc, i -> acc * i })
        byteBuffer.order(ByteOrder.nativeOrder())

        for (value in floatArray) {
            byteBuffer.putFloat(value)
        }

        return byteBuffer
    }
}