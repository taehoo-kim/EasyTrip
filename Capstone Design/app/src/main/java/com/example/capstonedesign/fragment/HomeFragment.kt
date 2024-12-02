package com.example.capstonedesign.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.capstonedesign.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var client: OkHttpClient
    private lateinit var buttonSend: Button
    private lateinit var etQuestion: EditText
    private lateinit var buttonDepart: Button
    private lateinit var buttonArrive: Button
    private var selectedDepartDate: String? = null
    private var selectedArriveDate: String? = null

    private val serverUrl = "http://10.0.2.2:5000/process"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        buttonSend = view.findViewById(R.id.btn_send)
        etQuestion = view.findViewById(R.id.input_text)
        buttonDepart = view.findViewById(R.id.btn_depart_calender)
        buttonArrive = view.findViewById(R.id.btn_arrive_calender)
        client = OkHttpClient()

        buttonDepart.setOnClickListener {
            Log.d("HomeFragment", "Departure button clicked")
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // 선택된 날짜를 문자열로 변환
                selectedDepartDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                buttonDepart.text = selectedDepartDate // 선택된 날짜를 버튼에 표시
            }, year, month, day)
            datePickerDialog.show()
        }

        buttonArrive.setOnClickListener {
            Log.d("HomeFragment", "Arrival button clicked")
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // 선택된 날짜를 문자열로 변환
                selectedArriveDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                buttonArrive.text = selectedArriveDate // 선택된 날짜를 버튼에 표시
            }, year, month, day)
            datePickerDialog.show()
        }

        buttonSend.setOnClickListener {

            // question 이 부분이 보내지는거
            val question = "${etQuestion.text.toString()}, $selectedDepartDate, $selectedArriveDate"
            sendQuestionToServer(question)

            val resultFragment = ResultFragment()

            // Fragment 전환
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, resultFragment)
            transaction?.addToBackStack(null) // 이전 Fragment로 돌아가기 위한 스택에 추가
            transaction?.commit()
        }

        return view
    }

    private fun sendQuestionToServer(question: String) {
        val requestBody = """{"question": "$question"}""".toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to send request", e)
                activity?.runOnUiThread {
//                    Toast.makeText(requireContext(), "Failed to send request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    activity?.runOnUiThread {
                        // 보내졌으면 화면에 Toast 띄우기
//                        Toast.makeText(requireContext(), "Response: $responseData", Toast.LENGTH_LONG).show()
                    }
                } else {
                    activity?.runOnUiThread {

                        // 실패했으면 failed 코드 띄우기
//                        Toast.makeText(requireContext(), "Request failed with code: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }




    private fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = "sk-proj-lXW2M00jXHBZj1EEGpsUT3BlbkFJxJq9EVx4dhhBDdIC78oq"
        val url = "https://api.openai.com/v1/completions"

        val requestBody = """
            {
            "model": "gpt-3.5-turbo-instruct",
            "prompt": "$question",
            "max_tokens": 1000,
            "temperature": 0.5
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
