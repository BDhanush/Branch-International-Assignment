package com.example.branchinternational

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.branchinternational.databinding.ActivityLoginBinding
import com.example.branchinternational.model.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        if (supportActionBar != null) {
            supportActionBar?.hide();
        }

        val view = binding.root
        setContentView(view)
        pref = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)

        resetFields()

        binding.loginButton.setOnClickListener{
            if(checkFields()) {
                lockButton()
                login()
            }
        }

    }

    private fun login() {
        val retrofitBuffer= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiInterface::class.java)
        val username=binding.usernameInput.text.toString().trim()
        val password=binding.passwordInput.text.toString()

        val retrofitData = retrofitBuffer.login(Login(username,password))

        retrofitData.enqueue(object : Callback<Map<String,String>> {
            override fun onResponse(call: Call<Map<String,String>>, response: Response<Map<String,String>>) {
                if(response.isSuccessful)
                {
                    AUTH_TOKEN = response.body()?.get("auth_token").toString()
//                    Toast.makeText(applicationContext,authToken,Toast.LENGTH_SHORT).show()
                    val editor: Editor = pref.edit()
                    editor.putString("auth_token", AUTH_TOKEN)
                    editor.apply()
                    Toast.makeText(applicationContext,"Login successful",Toast.LENGTH_SHORT).show()
                    finish()
                    Intent(applicationContext,MessageThreadsActivity::class.java).also {
                        startActivity(it)
                    }
                }else{
                    Toast.makeText(applicationContext,"Username or password is invalid",Toast.LENGTH_SHORT).show()
                    unlockButton()
                }
            }
            override fun onFailure(call: Call<Map<String,String>>, t: Throwable) {
                Toast.makeText(applicationContext,t.message?:"Error authenticating",Toast.LENGTH_SHORT).show()
                unlockButton()
            }
        })
    }

    private fun checkFields():Boolean
    {
        var check:Boolean=true
        if (binding.usernameInput.text.toString().trim().isEmpty()) {
            binding.usernameLayout.error = "This field is required"
            check = false
        }
        if (binding.passwordInput.text.toString().isEmpty()) {
            binding.passwordLayout.error = "This field is required"
            check = false
        }
        // after all validation return true.
        return check
    }

    private fun lockButton()
    {
        binding.loginButton.text = getString(R.string.loggingIn)
        binding.loginButton.isEnabled = false
    }
    private fun unlockButton()
    {
        binding.loginButton.text = getString(R.string.log_in)
        binding.loginButton.isEnabled = true
    }

    private fun resetFields() {
        binding.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                if (binding.usernameInput.text.toString().trim().isEmpty()) {
                    binding.usernameLayout.error = "This field is required"
                }
                else {
                    binding.usernameLayout.error = null
                }
            }
        })
        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                if (binding.passwordInput.text.toString().trim().isEmpty()) {
                    binding.passwordLayout.error = "This field is required"
                }
                else {
                    binding.passwordLayout.error = null
                }
            }
        })
    }

    public override fun onStart() {

        super.onStart()

        AUTH_TOKEN = pref.getString("auth_token", null)

        // Check if user is signed in (non-null) and update UI accordingly.
        if(AUTH_TOKEN != null){
            Intent(this,MessageThreadsActivity::class.java).also{
                finish()
                startActivity(it)
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
