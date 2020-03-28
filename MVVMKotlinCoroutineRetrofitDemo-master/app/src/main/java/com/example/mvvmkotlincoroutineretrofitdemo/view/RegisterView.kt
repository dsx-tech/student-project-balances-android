package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.model.RegisterBody
import com.example.mvvmkotlincoroutineretrofitdemo.viewmodel.MainViewModel
import android.content.Intent
import android.widget.TextView


class RegisterView: AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var returnToMain: Intent
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        registerObservers()
        val email: EditText = findViewById(R.id.emailReg)
        val usernameReg: EditText = findViewById(R.id.usernameReg)
        val password: EditText = findViewById(R.id.passwordReg)
        val passwordRepeat: EditText = findViewById(R.id.passwordReg2)
        val backReg : RelativeLayout = findViewById(R.id.backReg)
        val backTolLog : TextView = findViewById(R.id.backToLogin)
        returnToMain = Intent(
            applicationContext,
            MainActivity::class.java
        )
        backTolLog.setOnClickListener {startActivity(returnToMain)}
        backReg.setOnClickListener {
            hideKeyboardFrom(this, it)
        }
        val createAccount: Button = findViewById(R.id.regButton)
        createAccount.setOnClickListener {
            if ((password.text.toString() == passwordRepeat.text.toString())
                and (password.text.toString() !="")
                and (email.text.toString() !="")
                and (usernameReg.text.toString() !="")){

                mainViewModel.register(RegisterBody(email.text.toString(), usernameReg.text.toString(), password.text.toString()))
            }
        }


    }
    private fun registerObservers() {

        mainViewModel.registerSuccessLiveData.observe(this, Observer {

            startActivity(returnToMain)
        }
        )
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}