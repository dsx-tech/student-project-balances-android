package com.example.portfolio.view

import android.app.*
import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.example.portfolio.R
import com.example.portfolio.viewmodel.UploadViewModel
import java.math.BigDecimal
import java.text.SimpleDateFormat

import java.util.*

class DialogMinus {
    fun setMinus(context: Context, balances : MutableMap<String, BigDecimal?>?) {
        var balText = ""
        balances?.keys?.forEach {
            balText +="$it : ${balances[it]}\n"
        }
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setPositiveButton("OK"){
                dialog, _ ->
                dialog.dismiss()
            }
            setTitle("You have less then zero with that actives:")
            setMessage(balText)
            show()
        }





    }



    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}