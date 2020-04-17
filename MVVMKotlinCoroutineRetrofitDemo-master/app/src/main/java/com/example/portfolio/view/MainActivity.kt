package com.example.portfolio.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AAChartView
import com.example.portfolio.R
import com.example.portfolio.constants.Days
import com.example.portfolio.constants.Currencies
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.SharedPreferences
import com.anychart.scales.DateTime
import com.example.portfolio.model.*
import com.example.portfolio.repository.RepositoryForCorrelation
import com.example.portfolio.repository.RepositoryForInputOutput
import com.example.portfolio.repository.RepositoryForRelativeRates
import com.example.portfolio.viewmodel.*
import com.github.mikephil.charting.charts.PieChart
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.MediaType
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inf: MenuInflater = menuInflater
        inf.inflate(R.menu.popup_menu_with_graphs, menu)
        inf.toString()
        return true
    }

    private val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"
    private var baseCur = "eur"
    private val APP_PREFERENCES_ID = ""
    lateinit var pref: SharedPreferences
    private lateinit var mainViewModel: MainViewModel
    private lateinit var correlationViewModel: CorrelationViewModel
    private lateinit var inputViewModel: InputViewModel
    private lateinit var incomePortViewModel: IncomePortViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var columnViewModel: ColumnViewModel
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var curBalanceViewModel: CurBalanceViewModel

    private lateinit var rateAdapter: RateAdapter
    private lateinit var transAdapter: TransAdapter
    private lateinit var tradesAdapter: TradesAdapter
    private lateinit var chartAdapter: ChartAdapter
    private lateinit var ratesChartAdapter: RatesChartAdapter
    private lateinit var columnChartAdapter: ColumnChartAdapter
    private lateinit var incomeChartAdapter: IncomeChartAdapter
    private lateinit var curBalanceChartAdapter: CurBalanceChartAdapter
    private lateinit var rvPortfolioAdapter: RVPortfolioAdapter
    private lateinit var rvCorrelationAdapter: RVCorrelationAdapter
    private lateinit var rvRatesAdapter: RVRatesAdapter
    private lateinit var relativeRatesAdapter: RelativeRatesAdapter
    private lateinit var repositoryForRelativeRates: RepositoryForRelativeRates
    private lateinit var repositoryForInputOutput: RepositoryForInputOutput
    private lateinit var repositoryForCorrelation: RepositoryForCorrelation
    private lateinit var inputOutputAdapter: InputOutputAdapter

    private lateinit var dialog: Dialog
    private var aaChartView: AAChartView? = null
    private var chart: PieChart? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        setContentView(R.layout.activity_portfolios)
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        correlationViewModel = ViewModelProviders.of(this).get(CorrelationViewModel::class.java)
        inputViewModel = ViewModelProviders.of(this).get(InputViewModel::class.java)
        incomePortViewModel = ViewModelProviders.of(this).get(IncomePortViewModel::class.java)
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        columnViewModel = ViewModelProviders.of(this).get(ColumnViewModel::class.java)
        uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)
        incomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel::class.java)
        curBalanceViewModel = ViewModelProviders.of(this).get(CurBalanceViewModel::class.java)

        rateAdapter = RateAdapter()
        transAdapter = TransAdapter()
        tradesAdapter = TradesAdapter()
        chartAdapter = ChartAdapter()
        ratesChartAdapter = RatesChartAdapter()
        columnChartAdapter = ColumnChartAdapter()
        incomeChartAdapter = IncomeChartAdapter()
        curBalanceChartAdapter = CurBalanceChartAdapter()
        rvPortfolioAdapter = RVPortfolioAdapter()
        rvRatesAdapter = RVRatesAdapter()
        rvCorrelationAdapter = RVCorrelationAdapter()
        relativeRatesAdapter = RelativeRatesAdapter()
        repositoryForRelativeRates = RepositoryForRelativeRates()
        repositoryForCorrelation = RepositoryForCorrelation()
        repositoryForInputOutput = RepositoryForInputOutput()
        inputOutputAdapter = InputOutputAdapter()

        registerObservers()
        loginSetup()


    }


    private fun setRates() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = rvRatesAdapter
    }

    private fun loginSetup() {
        setContentView(R.layout.login_activity)
        val register: TextView = findViewById(R.id.register)
        val back: RelativeLayout = findViewById(R.id.back)


        back.setOnClickListener {
            hideKeyboardFrom(this, it)
        }
        register.setOnClickListener {

            startActivity(Intent(this@MainActivity, RegisterView()::class.java))
        }
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val view: View = this.layoutInflater.inflate(R.layout.full_screen_progress_bar, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
            val username: EditText = findViewById(R.id.userName)
            val passwordActual: EditText = findViewById(R.id.password)

            mainViewModel.auth(LoginBody(username.text.toString(), passwordActual.text.toString()))

        }
    }

    private fun setPortfolios() {

        setContentView(R.layout.activity_portfolios)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = rvPortfolioAdapter

    }

    private fun setCorr() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = rvCorrelationAdapter
    }

    private fun completedAuth() {

        val token = getToken()
        portfolioViewModel.getPortfolios(token)

    }

    private fun uploadTradesTrans(flag: Int){
        var info = ""
        when (flag){
            111 -> info = "trades"
            222 -> info = "transactions"
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("?")
        builder.setMessage("How you want to upload your $info")

        builder.setPositiveButton("CSV") { _, _ ->
            Dexter.withActivity(this@MainActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@MainActivity,
                            "You don't have permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                        val intent = Intent()
                            .setType("*/*")
                            .setAction(Intent.ACTION_GET_CONTENT)
                        startActivityForResult(
                            Intent.createChooser(intent, "Select a file"),
                            flag
                        )
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }

        builder.setNegativeButton("ENTER") { _, _ ->
        setUploadTr(flag)

        }
        builder.show()
    }
    private fun showGraphs() {
        setContentView(R.layout.activity_main)
        setToolbar()
        chart = findViewById(R.id.chart)
        val token = getToken()
        var id: String? = null
        if (pref.contains(APP_PREFERENCES_ID)) {
            id = pref.getString(APP_PREFERENCES_ID, "0")
        }
        mainViewModel.getTrades(token, id!!.toInt())
        mainViewModel.getTrans(token, id.toInt())

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.base_currency ->{
                lateinit var dialog:AlertDialog
                val array = Currencies.currenciesArray
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Choose base currency.")
                builder.setSingleChoiceItems(array,-1) { _, which->
                    baseCur = array[which].toLowerCase()

                        Toast.makeText(this@MainActivity,"$baseCur selected.", Toast.LENGTH_SHORT
                        ).show()
                    dialog.dismiss()
                }
                dialog = builder.create()
                dialog.show()
                true
            }
            R.id.upload_trades -> {
                uploadTradesTrans(111)
                true
            }
            R.id.upload_trans -> {
                uploadTradesTrans(222)
                true
            }
            R.id.menu1 -> {
                setContentView(R.layout.activity_main)
                setToolbar()
                chart = findViewById(R.id.chart)
                if (!mainViewModel.balancesAtTheEnd.value.isNullOrEmpty())
                    mainViewModel.getStringWithInstruments(baseCur)
                else {
                    val token = getToken()
                    var id: String? = null
                    if (pref.contains(APP_PREFERENCES_ID)) {
                        id = pref.getString(APP_PREFERENCES_ID, "0")
                    }
                    mainViewModel.getTrades(token, id!!.toInt())
                    mainViewModel.getTrans(token, id.toInt())
                }

                true
            }
            R.id.menu2 -> {

                ratesGraphDraw("btc", baseCur)
                true
            }
            R.id.menu3 -> {
                setContentView(R.layout.activity_column_graph)
                setToolbar()
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.rate_column_graph)
                val editText: EditText = findViewById(R.id.year)
                button.setOnClickListener {
                    try {

                        if ((mainViewModel.tradesSuccessLiveData.value?.isNotEmpty() == true) or (mainViewModel.transSuccessLiveData.value?.isNotEmpty() == true))
                            columnViewModel.modelingColumnGraph(
                                editText.text.toString().toInt(),
                                mainViewModel.tradesSuccessLiveData.value,
                                mainViewModel.transSuccessLiveData.value
                            )
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Please, enter year", Toast.LENGTH_SHORT).show()
                    }
                }

                true
            }
            R.id.menu4 -> {
                setContentView(R.layout.activity_income_graph)
                setCalendars()
                val textDateFrom: TextView = findViewById(R.id.dateFrom)
                val textDateTo: TextView = findViewById(R.id.dateTo)
                val button: Button = findViewById(R.id.graph_draw)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                setCur(Currencies.currenciesArray, currencies1)
                button.setOnClickListener {
                    button.clearFocus()
                    incomeViewModel.getRatesForIncome(
                        currencies1.text.toString().toLowerCase(),
                        textDateFrom.text.toString(),
                        textDateTo.text.toString(),
                        baseCur
                    )
                    hideKeyboardFrom(this, it)
                }

                true
            }
            R.id.menu5 -> {
                setContentView(R.layout.activity_curr_in_portfolio)
                setCalendars()
                val button: Button = findViewById(R.id.graph_draw)
                val textDateFrom: TextView = findViewById(R.id.dateFrom)
                val textDateTo: TextView = findViewById(R.id.dateTo)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                setCur(Currencies.currenciesArray, currencies1)
                button.setOnClickListener {
                    button.clearFocus()
                    curBalanceViewModel.getRatesForCurBalance(
                        currencies1.text.toString().toLowerCase(),
                        textDateFrom.text.toString(),
                        textDateTo.text.toString(),
                        baseCur
                    )
                    hideKeyboardFrom(this, it)
                }
                true
            }
            R.id.menu6 -> {
                setContentView(R.layout.activity_for_relative_correl)
                setToolbar()
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.graph_draw)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
                setCur(Currencies.currenciesArray, currencies1)
                setCur(Currencies.currenciesArray, currencies2)
                button.setOnClickListener {
                    button.clearFocus()
                    val timeNow = Calendar.getInstance().timeInMillis / 1000
                    val time = timeNow - Days.MONTH_IN_SEC*2
                    mainViewModel.getRatesCor(
                        Pair(
                            currencies1.text.toString().toLowerCase(),
                            currencies2.text.toString().toLowerCase()
                        ),
                        time, timeNow, baseCur
                    )
                    hideKeyboardFrom(this, it)
                }
                true
            }
            R.id.menu7 -> {
                setContentView(R.layout.activity_input_output)
                setCalendars()
                val button: Button = findViewById(R.id.graph_draw)
                val textDateFrom: TextView = findViewById(R.id.dateFrom)
                val textDateTo: TextView = findViewById(R.id.dateTo)
                button.setOnClickListener {
                    inputViewModel.filterTrans(
                        mainViewModel.transSuccessLiveData.value!!,
                        textDateFrom.text.toString(),
                        textDateTo.text.toString(),
                        baseCur
                    )
                }
                true
            }
            R.id.menu8 -> {
                setContentView(R.layout.activity_input_output)
                setCalendars()
                val button: Button = findViewById(R.id.graph_draw)
                val textDateFrom: TextView = findViewById(R.id.dateFrom)
                val textDateTo: TextView = findViewById(R.id.dateTo)
                button.setOnClickListener {
                    incomePortViewModel.filterTradesTrans(
                        mainViewModel.transSuccessLiveData.value!!,
                        mainViewModel.tradesSuccessLiveData.value!!,
                        textDateFrom.text.toString(),
                        textDateTo.text.toString(),
                        baseCur
                    )
                }
                true
            }
            R.id.menu9 -> {
                setContentView(R.layout.activity_correlation)
                setToolbar()
                val button: Button = findViewById(R.id.add_instrument)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
                val c = Currencies.currenciesArray
                val curInstr: MutableList<String> = mutableListOf()
                c.forEach {
                            curInstr.add(it)

                }
                curInstr.distinct()
                setCur(curInstr.toTypedArray(),currencies1)
                setCur(curInstr.toTypedArray(),currencies2)
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                recyclerView.adapter = rvCorrelationAdapter

                button.setOnClickListener {
                    if (currencies1.text.toString() != currencies2.text.toString().toLowerCase()) {
                        button.clearFocus()
                        if ((currencies1.text.toString().toLowerCase() == baseCur) or (currencies2.text.toString().toLowerCase() == baseCur) ){
                            val builder = AlertDialog.Builder(this)

                            with(builder)
                            {
                                setTitle("Ooops")
                                setMessage("Please, choose another base currency or active")
                                show()
                            }
                        }
                        else {
                            val timeNow = Calendar.getInstance().timeInMillis / 1000
                            val time = timeNow - Days.MONTH_IN_SEC * 2
                            correlationViewModel.getRatesForCor(
                                "${currencies1.text.toString().toLowerCase()}-$baseCur,${currencies2.text.toString().toLowerCase()}-$baseCur",
                                time,
                                timeNow
                            )
                        }

                        hideKeyboardFrom(this, it)
                    }
                }
                true
            }
            else -> false
        }

    }


    private fun ratesGraphDraw(cur1: String, cur2: String){
        setContentView(R.layout.activity_rate_graph)
        setToolbar()

        mainViewModel.getRates(
            "$cur1-$cur2",
            Calendar.getInstance().timeInMillis / 1000 - Days.MONTH_IN_SEC * 2, Calendar.getInstance().timeInMillis / 1000
        )
        val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
        currencies1.setText(cur1.toUpperCase())
        setCur(Currencies.currenciesArray, currencies1)
        val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
        currencies2.setText(cur2.toUpperCase())
        setCur(Currencies.currenciesArray, currencies2)
        val button: Button = findViewById(R.id.graph_draw)
        val radio1: RadioButton = findViewById(R.id.radio1)
        val radio2: RadioButton = findViewById(R.id.radio2)
        val radio3: RadioButton = findViewById(R.id.radio3)
        var i = 1
        button.setOnClickListener {
            when {
                radio1.isChecked -> i = 1
                radio2.isChecked -> i = 2
                radio3.isChecked -> i = 3
            }
            button.clearFocus()

            val timeNow = Calendar.getInstance().timeInMillis / 1000
            val time = timeNow - Days.MONTH_IN_SEC * i
            mainViewModel.getRates(
                "${currencies1.text.toString().toLowerCase()}-${currencies2.text.toString().toLowerCase()}",
                time, timeNow
            )
            hideKeyboardFrom(this, it)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var format =""
        lateinit var dialog:AlertDialog
        val array = arrayOf("Dsx", "Tinkoff")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose base currency.")
        builder.setSingleChoiceItems(array,-1) { _, which->
            format = array[which]

            Toast.makeText(this@MainActivity,"$format selected.", Toast.LENGTH_SHORT
            ).show()
            if (requestCode == 111 && resultCode == RESULT_OK) {
                val selectedFile = data?.data //The uri with the location of the file
                val file = File(selectedFile!!.path)
                val split = file.path.split(":")
                val filer = split[1]
                val f = File(filer)
                val type = MediaType.parse(contentResolver.getType(selectedFile))
                val token = getToken()
                var id: String? = null
                if (pref.contains(APP_PREFERENCES_ID)) {
                    id = pref.getString(APP_PREFERENCES_ID, "0")
                }
                uploadViewModel.uploadFiles(selectedFile, f, id!!.toInt(), token, type, format)
            } else
                if (requestCode == 222 && resultCode == RESULT_OK) {
                    val selectedFile = data?.data //The uri with the location of the file
                    val file = File(selectedFile!!.path)
                    val split = file.path.split(":")
                    val filer = split[1]
                    val f = File(filer)
                    val type = MediaType.parse(contentResolver.getType(selectedFile))
                    val token = getToken()
                    var id: String? = null
                    if (pref.contains(APP_PREFERENCES_ID)) {
                        id = pref.getString(APP_PREFERENCES_ID, "0")
                    }
                    uploadViewModel.uploadTrans(selectedFile, f, id!!.toInt(), token, type, format)
                }
            dialog.dismiss()
        }
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()

    }

    private fun setUploadTr(flag: Int){
        when (flag){
            111 ->{
                val dialog = Dialog(this)
                dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog .setCancelable(true)
                dialog .setContentView(R.layout.upload_trade)

                val time = dialog.findViewById(R.id.time) as TextView
                time.text =
                    SimpleDateFormat("HH:mm").format(System.currentTimeMillis())
                val date = dialog.findViewById(R.id.date) as TextView
                date.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                date.setOnClickListener {
                    setDate(date)
                }
                val currencies1: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur1)
               setCur(Currencies.currenciesArray, currencies1)
                val currencies2: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur2)
                setCur(Currencies.currenciesArray, currencies2)
                val currencies3: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur3)
                setCur(Currencies.currenciesArray, currencies3)
                time.setOnClickListener {  val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        time.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }
                    TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show() }
                val yesBtn = dialog .findViewById(R.id.upload) as Button

                val radio1: RadioButton = dialog.findViewById(R.id.radio1)
                val radio2: RadioButton = dialog.findViewById(R.id.radio2)
                var type = ""
                val tradedQ : EditText = dialog.findViewById(R.id.tradedq)
                val tradePrice : EditText = dialog.findViewById(R.id.tradePrice)
                val commission : EditText = dialog.findViewById(R.id.commission)
                val tradeId : EditText = dialog.findViewById(R.id.tradeId)



                yesBtn.setOnClickListener {
                    when {
                        radio1.isChecked -> type = "Buy"
                        radio2.isChecked -> type = "Sell"
                    }
                    val token = getToken()
                    var dat = LocalDate.parse(date.text.toString(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))

                    var id: String? = null
                    if (pref.contains(APP_PREFERENCES_ID)) {
                        id = pref.getString(APP_PREFERENCES_ID, "0")
                    }
                    try{uploadViewModel.uploadTrade(id!!.toInt(), token, Trade(id = 0, instrument = "${currencies1.text.toString().toLowerCase()}-${currencies2.text.toString().toLowerCase()}",
                        tradeType = type, tradedQuantity = BigDecimal(tradedQ.text.toString()), tradedPrice = BigDecimal(tradePrice.text.toString()), commission = BigDecimal(commission.text.toString()),
                        tradeValueId = tradeId.text.toString().toInt(), tradedQuantityCurrency = currencies1.text.toString().toLowerCase(), tradedPriceCurrency = currencies2.text.toString().toLowerCase(), commissionCurrency = currencies2.text.toString().toLowerCase(),
                        dateTime = "${dat}T${time.text}:00.000Z"
                        ))
                    dialog .dismiss()}
                    catch (e:Exception){
                        Toast.makeText( this,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog .show()
            }
            222 ->{
                val dialog = Dialog(this)
                dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog .setCancelable(true)
                dialog .setContentView(R.layout.upload_transaction)

                val time = dialog.findViewById(R.id.time) as TextView
                val radio1: RadioButton = dialog.findViewById(R.id.radio1)
                val radio2: RadioButton = dialog.findViewById(R.id.radio2)
                val commission : EditText = dialog.findViewById(R.id.commission)
                val amount : EditText = dialog.findViewById(R.id.amount)
                var type = ""
                val transactionId : EditText = dialog.findViewById(R.id.transactionId)
                time.text =
                    SimpleDateFormat("HH:mm").format(System.currentTimeMillis())
                val date = dialog.findViewById(R.id.date) as TextView
                date.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                date.setOnClickListener {
                  setDate(date)
                }
                val currencies1: AutoCompleteTextView = dialog.findViewById(R.id.autoCoCur)
                setCur(Currencies.currenciesArray, currencies1)

                time.setOnClickListener {  val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        time.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }
                    TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show() }
                val yesBtn = dialog .findViewById(R.id.upload) as Button
                yesBtn.setOnClickListener {
                    when {
                        radio1.isChecked -> type = "Deposit"
                        radio2.isChecked -> type = "Withdraw"
                    }
                    val token = getToken()
                    var dat = LocalDate.parse(date.text.toString(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))

                    var id: String? = null
                    if (pref.contains(APP_PREFERENCES_ID)) {
                        id = pref.getString(APP_PREFERENCES_ID, "0")
                    }
                   try{ uploadViewModel.uploadTransaction(id!!.toInt(), token, Transaction(id = 0,  commission = BigDecimal(commission.text.toString()), transactionType = type, currency = currencies1.text.toString().toLowerCase(),
                        amount = BigDecimal(amount.text.toString()), transactionStatus = "Complete", transactionValueId = transactionId.text.toString().toInt(),
                        dateTime = "${dat}T${time.text}:00.000Z"
                    ))
                    dialog .dismiss()}
                   catch (e:Exception){
                       Toast.makeText( this,
                           "Something went wrong",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
                }
                dialog .show()
            }
        }
    }


    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setTitleTextColor(getColor(R.color.white))
        toolbar.setNavigationIcon(R.drawable.lock)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            setPortfolios()
        }
    }

    private fun setCalendars() {
        setToolbar()
        aaChartView = findViewById(R.id.AAChartView)
        val textDateFrom: TextView = findViewById(R.id.dateFrom)
        val textDateTo: TextView = findViewById(R.id.dateTo)
        textDateFrom.text =
            SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        textDateTo.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
       setDate(textDateFrom)
        setDate(textDateTo)
    }
    private fun setDate(date: TextView){
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
                this, dateSetListenerFrom,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    private fun setCur(array: Array<String>, currencies1: AutoCompleteTextView) {


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            array
        )
        currencies1.threshold = 0
        currencies1.setAdapter(adapter)
        currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }
    }

    private fun registerObservers() {

        mainViewModel.rateSuccessLiveData.observe(this, Observer { rates ->
            aaChartView = findViewById(R.id.AAChartView)

            rates?.let {
                rateAdapter.setRates(it)
            }
            val cur1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
            val cur2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
            if (rates.isNotEmpty())
                ratesChartAdapter.setRatesChart(
                    mainViewModel.rateSuccessLiveData.value!!,
                    aaChartView,
                    cur1.text.toString(),
                    cur2.text.toString()
                )
        })

        mainViewModel.tradesSuccessLiveData.observe(this, Observer { tradesList ->

            tradesList?.let {
                tradesAdapter.setTrades(it)
            }
        })
        mainViewModel.transSuccessLiveData.observe(this, Observer { transactionList ->

            transactionList?.let {
                transAdapter.setTrans(it)
            }
        })
        portfolioViewModel.addPortfolioSuccessLiveData.observe(this, Observer { portfolioList ->

            portfolioList?.let {
                rvPortfolioAdapter.addPortfolio(it)
                setPortfolios()
            }

        })
        portfolioViewModel.deletePortfolioSuccessLiveData.observe(this, Observer { portfolioList ->

            portfolioList?.let {
                rvPortfolioAdapter.deletePortfolio()
                setPortfolios()
            }

        })
        mainViewModel.tradesFailureLiveData.observe(this, Observer { isFailed ->

            isFailed?.let {
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
        mainViewModel.transFailureLiveData.observe(this, Observer { isFailed ->

            isFailed?.let {
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
        mainViewModel.stringWithInstruments.observe(this, Observer {
            mainViewModel.getRelevantRates(it)
        })

        tradesAdapter.tradesDownloaded.observe(this, Observer {
            if (transAdapter.transDownloaded.value == true) {
                mainViewModel.countingBalance()
                transAdapter.transDownloaded.postValue(false)
                tradesAdapter.tradesDownloaded.postValue(false)
            }
        })
        transAdapter.transDownloaded.observe(this, Observer {
            if (tradesAdapter.tradesDownloaded.value == true) {
                mainViewModel.countingBalance()
                transAdapter.transDownloaded.postValue(false)
                tradesAdapter.tradesDownloaded.postValue(false)
            }
        })
        mainViewModel.balancesAtTheEnd.observe(this, Observer {
            mainViewModel.getStringWithInstruments(baseCur)
        }
        )

        mainViewModel.relevantRatesSuccessLiveData.observe(this, Observer {

            rvRatesAdapter.setRates(it)
            mainViewModel.multiplyRelevant(baseCur)
        }
        )

        mainViewModel.balancesMultRates.observe(this, Observer {

            chartAdapter.setChart(it, chart!!, baseCur)
            setRates()

        }
        )
        columnViewModel.columnGraphData.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
            aaChartView = findViewById(R.id.AAChartView)
            columnChartAdapter.setColumnChart(aaChartView, it, k.text.toString().toInt(), baseCur)
        })
        columnViewModel.yearBalanceLiveData.observe(this, Observer {
            columnViewModel.getStringWithInstrumentsForColumn(it[12].keys.toMutableList(), baseCur)
        })
        columnViewModel.stringWithInstruments2.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
            columnViewModel.getRatesForTime(it, k.text.toString().toInt())
        })
        columnViewModel.rateSuccessLiveData.observe(this, Observer {
            columnViewModel.multiplyRes(baseCur)
        })

        mainViewModel.authSuccessLiveData.observe(this, Observer {

            val editor = pref.edit()
            editor.clear()
            editor.putString(APP_PREFERENCES_TOKEN, it)
            editor.apply()
            dialog.dismiss()
            completedAuth()

        }
        )

        incomeViewModel.ratesIncomeSuccessLiveData.observe(this, Observer {
            incomeViewModel.modelingSeriesForIncome(
                it.keys.first(),
                mainViewModel.tradesSuccessLiveData.value,
                mainViewModel.transSuccessLiveData.value
            )
        }
        )
        incomeViewModel.resultIncomeLiveData.observe(this, Observer {
            incomeChartAdapter.setIncomeChart(aaChartView, it.first, it.second, baseCur)
        }
        )
        curBalanceViewModel.ratesCurSuccessLiveData.observe(this, Observer {
            curBalanceViewModel.modelingSeriesForRateInPortfolio(
                mainViewModel.tradesSuccessLiveData.value,
                mainViewModel.transSuccessLiveData.value,
                it.keys.first()
            )
        }
        )
        curBalanceViewModel.resultCurLiveData.observe(this, Observer {
            curBalanceChartAdapter.setCurBalanceChart(aaChartView, it.first, it.second, baseCur)
        }
        )
        portfolioViewModel.portfolioSuccessLiveData.observe(this, Observer {
            it.add(Portfolio(-1, "Add"))
            rvPortfolioAdapter.setPortfolios(it)
            setPortfolios()

        }
        )
        rvPortfolioAdapter.selectedPortfolioLiveData.observe(this, Observer {
            if (it.second != -1) {
                val editor = pref.edit()
                editor.putString(
                    APP_PREFERENCES_ID,
                    rvPortfolioAdapter.selectedPortfolioLiveData.value!!.second.toString()
                )
                editor.apply()
                showGraphs()
            } else {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.add_portfolio)
                val name: EditText = dialog.findViewById(R.id.portfolioName)
                val button: Button = dialog.findViewById(R.id.addPortfolio)
                dialog.show()
                button.setOnClickListener {
                    val token = getToken()
                    portfolioViewModel.addPortfolio(Portfolio(0, name.text.toString()), token)
                    dialog.dismiss()
                }

            }
        }
        )

        mainViewModel.authFailureLiveData.observe(this, Observer { isFailed ->
            isFailed?.let {
                dialog.dismiss()
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }

        }
        )
        mainViewModel.rateCurSuccessLiveData.observe(this, Observer {
            relativeRatesAdapter.setRatesChart(it, aaChartView, baseCur)
        }
        )
        inputViewModel.valuesForInput.observe(this, Observer {
            inputViewModel.getRatesForTimeInput(it.first, it.second.first, it.second.second)
        }
        )
        inputViewModel.inOutSuccessLiveData.observe(this, Observer {
            inputViewModel.calculationInput(baseCur)
        }
        )
        rvPortfolioAdapter.deletePortfolioLiveData.observe(this, Observer {
            var token: String? = null
            if (pref.contains(APP_PREFERENCES_TOKEN)) {
                token = pref.getString(APP_PREFERENCES_TOKEN, "-")
            }
            portfolioViewModel.deletePortfolio(it.first, token!!)
        }
        )
        inputViewModel.resSuccessLiveData.observe(this, Observer {
            inputOutputAdapter.setInputChart(
                aaChartView,
                it,
                inputViewModel.valuesForInput.value!!.second,
                baseCur
            )
        }
        )
        incomePortViewModel.incomeFilterSuccessLiveData.observe(this, Observer {
            incomePortViewModel.getRatesForIncome(it.first, it.second.first, it.second.second)
        }
        )
        incomePortViewModel.incomePortSuccessLiveData.observe(this, Observer {
            incomePortViewModel.modelingSeriesForIncome(baseCur)
        }
        )
        incomePortViewModel.resultIncomePortLiveData.observe(this, Observer {
            incomeChartAdapter.setIncomeChart(aaChartView, it.first, it.second, baseCur)
        }
        )
        rvCorrelationAdapter.deleteCorLiveData.observe(this, Observer {
            setCorr()
        })
        correlationViewModel.rateCorSuccessLiveData.observe(this, Observer {
            correlationViewModel.calcCorr()
        }
        )
        correlationViewModel.correlationSuccessLiveData.observe(this, Observer {
            rvCorrelationAdapter.addCorrelation(Correlation(it.first, it.second))
            setCorr()
        }
        )
        uploadViewModel.uploadSuccessLiveData.observe(this, Observer {
            val token = getToken()
            var id: String? = null
            if (pref.contains(APP_PREFERENCES_ID)) {
                id = pref.getString(APP_PREFERENCES_ID, "0")
            }
            mainViewModel.getTrades(token, id!!.toInt())
            mainViewModel.getTrans(token, id.toInt())
        }
        )
        rvRatesAdapter.selectedRateLiveData.observe(this, Observer {
           ratesGraphDraw(it.first.substring(0, it.first.indexOf('-')), it.first.substring(it.first.indexOf('-') + 1, it.first.length))
        }
        )
        rvRatesAdapter.deleteRateLiveData.observe(this, Observer {
            rvRatesAdapter.deleteRate()
            setRates()

        })

    }

    private fun getToken(): String {
        var token: String? = null
        if (pref.contains(APP_PREFERENCES_TOKEN)) {
            token = pref.getString(APP_PREFERENCES_TOKEN, "-")
        }
        return token!!
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}


