package com.example.httptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.httptest.databinding.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class UserData {
    @SerializedName("account")
    var account: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("id")
    var id: Int? = null
}


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // test connect
        binding.reqButton.setOnClickListener{
            sendReq(binding.urlText.text.toString())
        }

        binding.jsonButton.setOnClickListener {
            findUser(binding.urlText.text.toString())
        }

        binding.jsonHttpButton.setOnClickListener {
            addUser(binding.urlText.text.toString(), binding.accountText.text.toString(), binding.passText.text.toString())
        }
    }


    private fun sendReq(url:String) {
        if(url.isEmpty()) {
            Toast.makeText(this, "Input url", Toast.LENGTH_LONG).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { binding.resText.text = e.message }
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body()?.string()
                runOnUiThread { binding.resText.text = resStr }
            }
        })
    }


    private fun findUser(url:String) {
        if (url.isEmpty()) {
            Toast.makeText(this, "Somethong Wrong!", Toast.LENGTH_LONG).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
                .url("$url/users/list").get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { binding.resText.text = e.message }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body()?.string()
                val jsonData = Gson().fromJson<List<UserData>>(resStr, object : TypeToken<List<UserData>>() {}.type)

                runOnUiThread {
                    val sb = StringBuffer()
                    for (json in jsonData) {
                        sb.append("account:")
                        sb.append(json.account)
                        sb.append("\n")
                        sb.append("password:")
                        sb.append(json.password)
                        sb.append("\n")
                        sb.append("id:")
                        sb.append(json.id)
                        sb.append("\n\n")
                    }
                    binding.resText.text = sb.toString()
                }

            }
        })
    }


    private fun addUser(url: String, account: String, password: String) {
        if(url.isEmpty() || account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Check url, account, and password", Toast.LENGTH_LONG).show()
            return
        }

        val jsonObject = JSONObject()
        jsonObject.put("account", account)
        jsonObject.put("password", password)
        val jsonObjectString = jsonObject.toString()
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObjectString)

//        Toast.makeText(this, requestBody.toString(), Toast.LENGTH_LONG).show()


        val client = OkHttpClient()
        val request = Request.Builder()
                .url("$url/users/add").post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { binding.resText.text = e.message }
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val resStr = response.body()?.string()
//                val jsonData = gson.fromJson<UserJsonData>(resStr, object : TypeToken<UserJsonData>() {}.type)

                runOnUiThread {
//                    val sb = StringBuffer()
//                    sb.append("Account: ")
//                    sb.append(jsonData.account)
//                    sb.append("\n")
//                    sb.append("Password: ")
//                    sb.append(jsonData.pass)
//                    sb.append("\n")
                    binding.resText.text = resStr.toString()
                }

            }
        })

    }
}