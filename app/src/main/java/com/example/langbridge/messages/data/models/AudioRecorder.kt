package com.example.langbridge.messages.data.models

import android.content.Context
import android.media.MediaRecorder
import android.util.Base64
import java.io.File
import java.io.FileInputStream

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTime: Long = 0

    fun startRecording() {
        outputFile = File.createTempFile("audio_message_", ".3gp", context.cacheDir)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile?.absolutePath)
            prepare()
            start()
        }
        startTime = System.currentTimeMillis()
    }

    fun stopRecording(): RecordingResult? {
        try {
            recorder?.apply {
                stop()
                release()
            }
            val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val fileInputStream = FileInputStream(outputFile)
            val bytes = fileInputStream.readBytes()
            fileInputStream.close()
            val base64Audio = Base64.encodeToString(bytes, Base64.DEFAULT)
            outputFile?.delete() // delete temp file

            return RecordingResult(base64Audio, duration)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            outputFile = null
        }
        return null
    }

    data class RecordingResult(val base64Audio: String, val durationSeconds: Int)
}
