package com.example.portfolio.view

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.example.portfolio.R
import com.example.portfolio.constants.Currencies
import com.example.portfolio.model.Trade
import com.example.portfolio.model.Transaction
import com.example.portfolio.viewmodel.UploadViewModel
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DialogUpload{
    fun setUploadTr(flag: Int, context: Context, id: String, token: String, activity: MainActivity) {
       val  uploadViewModel = ViewModelProviders.of(activity).get(UploadViewModel::class.java)
        when (flag) {
            111 -> {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.upload_trade)

                val time = dialog.findViewById(R.id.time) as TextView
                time.text =
                    SimpleDateFormat("HH:mm").format(System.currentTimeMillis())
                val date = dialog.findViewById(R.id.date) as TextView
                date.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                date.setOnClickListener {
                    setDate(date, context)
                }
                val currencies1: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur1)
                setCur(Currencies.currenciesArray, currencies1, context)
                val currencies2: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur2)
                setCur(Currencies.currenciesArray, currencies2, context)
                val currencies3: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur3)
                val layout : LinearLayout = dialog.findViewById(R.id.layout)
                layout.setOnClickListener { hideKeyboardFrom(context, it) }
                setCur(Currencies.currenciesArray, currencies3, context)
                time.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        time.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }
                    TimePickerDialog(
                        context,
                        timeSetListener,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }

                val yesBtn = dialog.findViewById(R.id.upload) as Button

                val radio1: RadioButton = dialog.findViewById(R.id.radio1)
                val radio2: RadioButton = dialog.findViewById(R.id.radio2)
                var type = ""
                val tradedQ: EditText = dialog.findViewById(R.id.tradedq)
                val tradePrice: EditText = dialog.findViewById(R.id.tradePrice)
                val commission: EditText = dialog.findViewById(R.id.commission)
                val tradeId: EditText = dialog.findViewById(R.id.tradeId)
                val cancel = dialog.findViewById(R.id.cancel) as Button

                cancel.setOnClickListener { dialog.dismiss() }
                yesBtn.setOnClickListener {
                    when {
                        radio1.isChecked -> type = "Buy"
                        radio2.isChecked -> type = "Sell"
                    }
                    val dat = LocalDate.parse(
                        date.text.toString(),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    )


                    try {
                        uploadViewModel.uploadTrade(
                            id.toInt(), token, Trade(
                                id = 0,
                                instrument = "${currencies1.text.toString().toLowerCase()}-${currencies2.text.toString().toLowerCase()}",
                                tradeType = type,
                                tradedQuantity = BigDecimal(tradedQ.text.toString()),
                                tradedPrice = BigDecimal(tradePrice.text.toString()),
                                commission = BigDecimal(commission.text.toString()),
                                tradeValueId = tradeId.text.toString().toInt(),
                                tradedQuantityCurrency = currencies1.text.toString().toLowerCase(),
                                tradedPriceCurrency = currencies2.text.toString().toLowerCase(),
                                commissionCurrency = currencies2.text.toString().toLowerCase(),
                                dateTime = "${dat}T${time.text}:00.000Z"
                            )
                        )
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                dialog.show()
            }
            222 -> {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.upload_transaction)

                val time = dialog.findViewById(R.id.time) as TextView
                val radio1: RadioButton = dialog.findViewById(R.id.radio1)
                val radio2: RadioButton = dialog.findViewById(R.id.radio2)
                val commission: EditText = dialog.findViewById(R.id.commission)
                val amount: EditText = dialog.findViewById(R.id.amount)
                var type = ""
                val transactionId: EditText = dialog.findViewById(R.id.transactionId)
                val cancel = dialog.findViewById(R.id.cancel) as Button
                val layout : LinearLayout = dialog.findViewById(R.id.layout)
                layout.setOnClickListener { hideKeyboardFrom(context, it) }
                time.text =
                    SimpleDateFormat("HH:mm").format(System.currentTimeMillis())
                val date = dialog.findViewById(R.id.date) as TextView
                date.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                date.setOnClickListener {
                    setDate(date, context)
                }
                val currencies1: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur)
                setCur(Currencies.currenciesArray, currencies1, context)

                time.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        time.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }
                    TimePickerDialog(
                        context,
                        timeSetListener,
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }
                val yesBtn = dialog.findViewById(R.id.upload) as Button
                val dat = LocalDate.parse(
                    date.text.toString(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
                cancel.setOnClickListener { dialog.dismiss() }
                yesBtn.setOnClickListener {
                    when {
                        radio1.isChecked -> type = "Deposit"
                        radio2.isChecked -> type = "Withdraw"
                    }

                    try {
                        uploadViewModel.uploadTransaction(
                            id!!.toInt(), token, Transaction(
                                id = 0,
                                commission = BigDecimal(commission.text.toString()),
                                transactionType = type,
                                currency = currencies1.text.toString().toLowerCase(),
                                amount = BigDecimal(amount.text.toString()),
                                transactionStatus = "Complete",
                                transactionValueId = transactionId.text.toString().toInt(),
                                dateTime = "${dat}T${time.text}:00.000Z"
                            )
                        )
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                dialog.show()
            }
        }
    }
    private fun setDate(date: TextView, context: Context) {
        val cal = Calendar.getInstance()
        val myFormat = "dd.MM.yyyy"
        val dateSetListenerFrom =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                date.text = sdf.format(cal.time)
            }
        date.setOnClickListener {
            DatePickerDialog(
                context, dateSetListenerFrom,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setCur(array: Array<String>, currencies1: AutoCompleteTextView, context: Context) {


        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            array
        )
        currencies1.threshold = 0
        currencies1.setAdapter(adapter)
        currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }
    }
    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }




}