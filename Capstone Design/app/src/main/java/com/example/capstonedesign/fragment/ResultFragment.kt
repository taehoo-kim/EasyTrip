package com.example.capstonedesign.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.capstonedesign.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ResultFragment : Fragment() {

    private lateinit var client: OkHttpClient
    private lateinit var detailButton1: Button
    private lateinit var detailButton2: Button
    private lateinit var detailButton3: Button
    private lateinit var detailButton4: Button

    private var receivedData: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        detailButton1 = view.findViewById(R.id.detail_button1)
        detailButton2 = view.findViewById(R.id.detail_button2)
        detailButton3 = view.findViewById(R.id.detail_button3)
        detailButton4 = view.findViewById(R.id.detail_button4)

        client = OkHttpClient()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        detailButton1.setOnClickListener {
            showDetailDialog1(receivedData)
        }
        detailButton2.setOnClickListener {
            showDetailDialog2(receivedData)
        }
        detailButton3.setOnClickListener {
            showDetailDialog3(receivedData)
        }
        detailButton4.setOnClickListener {
            showDetailDialog4(receivedData)
        }

        fetchDataFromServer()
    }
    private fun fetchDataFromServer() {
        val client = OkHttpClient()

        val url = "http://10.0.2.2:5000/process"
        val requestBody = JSONObject()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 실패 시 처리할 코드
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    receivedData = responseBody.string()
                    receivedData = decodeUnicodeEscape(receivedData)
                    // 받은 데이터를 변수에 저장하는 코드
                }
            }
        })
    }

    // 유니코드 이스케이프 문자열 decode 함수
    private fun decodeUnicodeEscape(input: String): String {
        val pattern = Regex("\\\\u([0-9a-fA-F]{4})")
        return pattern.replace(input) { match ->
            val unicode = Integer.parseInt(match.groupValues[1], 16)
            unicode.toChar().toString()
        }
    }




    private fun showDetailDialog1(detailMessage: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_detail_view1, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("코스 정보")

        val detailTextView = mDialogView.findViewById<TextView>(R.id.detail_text)
        detailTextView.text = detailMessage

        val mAlertDialog = mBuilder.show()

        val noButton = mDialogView.findViewById<Button>(R.id.closeButton)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun showDetailDialog2(detailMessage: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_detail_view2, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("코스 정보")

        val detailTextView = mDialogView.findViewById<TextView>(R.id.detail_text)
        detailTextView.text = detailMessage

        val mAlertDialog = mBuilder.show()

        val noButton = mDialogView.findViewById<Button>(R.id.closeButton)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun showDetailDialog3(detailMessage: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_detail_view3, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("코스 정보")

        val detailTextView = mDialogView.findViewById<TextView>(R.id.detail_text)
        detailTextView.text = detailMessage

        val mAlertDialog = mBuilder.show()

        val noButton = mDialogView.findViewById<Button>(R.id.closeButton)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun showDetailDialog4(detailMessage: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_detail_view4, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("코스 정보")

        val detailTextView = mDialogView.findViewById<TextView>(R.id.detail_text)
        detailTextView.text = detailMessage

        val mAlertDialog = mBuilder.show()

        val noButton = mDialogView.findViewById<Button>(R.id.closeButton)
        noButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = "sk-proj-lXW2M00jXHBZj1EEGpsUT3BlbkFJxJq9EVx4dhhBDdIC78oq"
        val url = "https://api.openai.com/v1/completions"

        val requestBody = """
            {
            "model": "gpt-3.5-turbo-instruct",
            "prompt": "$question",
            "max_tokens": 1000,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
                callback("API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data", body)
                } else {
                    Log.v("data", "empty response body")
                    callback("Empty response body")
                    return
                }

                try {
                    val jsonObject = JSONObject(body)
                    if (jsonObject.has("choices")) {
                        val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                        val textResult = jsonArray.getJSONObject(0).getString("text")
                        callback(textResult)
                    } else {
                        Log.e("error", "No value for choices in response")
                        callback("No value for choices in response")
                    }
                } catch (e: JSONException) {
                    Log.e("error", "JSON parsing error", e)
                    callback("JSON parsing error: ${e.message}")
                }
            }
        })
    }
}
