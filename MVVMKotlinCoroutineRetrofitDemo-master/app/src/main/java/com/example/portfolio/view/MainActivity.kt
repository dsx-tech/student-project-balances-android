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
import com.example.portfolio.model.LoginBody
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.text.set
import com.example.portfolio.model.Correlation
import com.example.portfolio.model.Portfolio
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


class MainActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inf: MenuInflater = menuInflater
        inf.inflate(R.menu.popup_menu_with_graphs, menu)
        inf.toString()
        return true
    }

    private val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"
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
            R.id.upload_trades -> {

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
                                111
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

                true
            }
            R.id.upload_trans -> {

                Dexter.withActivity(this@MainActivity)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                            Toast.makeText(this@MainActivity, "1", Toast.LENGTH_SHORT).show()
                        }

                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            Toast.makeText(this@MainActivity, "2", Toast.LENGTH_SHORT).show()
                            val intent = Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT)
                            startActivityForResult(
                                Intent.createChooser(intent, "Select a file"),
                                222
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

                true
            }
            R.id.menu1 -> {
                setContentView(R.layout.activity_main)
                setToolbar()
                chart = findViewById(R.id.chart)
                if (!mainViewModel.balancesAtTheEnd.value.isNullOrEmpty())
                    mainViewModel.getStringWithInstruments()
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

                ratesGraphDraw("btc", "usd")
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
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    Currencies.currenciesArray
                )
                currencies1.threshold = 0
                currencies1.setAdapter(adapter)
                currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }



                button.setOnClickListener {
                    button.clearFocus()
                    incomeViewModel.getRatesForIncome(
                        currencies1.text.toString().toLowerCase(),
                        textDateFrom.text.toString(),
                        textDateTo.text.toString()
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
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    Currencies.currenciesArray
                )
                currencies1.threshold = 0
                currencies1.setAdapter(adapter)
                currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }
                button.setOnClickListener {
                    button.clearFocus()
                    curBalanceViewModel.getRatesForCurBalance(
                        currencies1.text.toString().toLowerCase(),
                        textDateFrom.text.toString(),
                        textDateTo.text.toString()
                    )
                    hideKeyboardFrom(this, it)
                }
                true
            }
            R.id.menu6 -> {
                setContentView(R.layout.activity_for_relative_correl)
                setToolbar()
                setCur(Currencies.currenciesArray)
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.graph_draw)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)

                button.setOnClickListener {
                    button.clearFocus()
                    val timeNow = Calendar.getInstance().timeInMillis / 1000
                    val time = timeNow - Days.MONTH_IN_SEC
                    mainViewModel.getRatesCor(
                        Pair(
                            currencies1.text.toString().toLowerCase(),
                            currencies2.text.toString().toLowerCase()
                        ),
                        time, timeNow
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
                        textDateTo.text.toString()
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
                        textDateTo.text.toString()
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
                c.forEach { it1 ->
                    c.forEach {
                        if (it != it1)
                            curInstr.add("$it1-$it")
                    }
                }
                curInstr.distinct()
                setCur(curInstr.toTypedArray())
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                recyclerView.adapter = rvCorrelationAdapter

                button.setOnClickListener {
                    if (currencies1.text.toString() != currencies2.text.toString().toLowerCase()) {
                        button.clearFocus()
                        val timeNow = Calendar.getInstance().timeInMillis / 1000
                        val time = timeNow - Days.MONTH_IN_SEC * 2
                        correlationViewModel.getRatesForCor(
                            "${currencies1.text.toString().toLowerCase()},${currencies2.text.toString().toLowerCase()}",
                            time,
                            timeNow
                        )


                        hideKeyboardFrom(this, it)
                    }
                }
                true
            }
            else -> false
        }

    }


    fun ratesGraphDraw(cur1: String, cur2: String){
        setContentView(R.layout.activity_rate_graph)
        setToolbar()
        setCur(Currencies.currenciesArray)
        mainViewModel.getRates(
            "$cur1-$cur2",
            Calendar.getInstance().timeInMillis / 1000 - Days.MONTH_IN_SEC * 2, Calendar.getInstance().timeInMillis / 1000
        )
        val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
        currencies1.setText(cur1.toUpperCase())
        val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
        currencies2.setText(cur2.toUpperCase())
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
            uploadViewModel.uploadFiles(selectedFile, f, id!!.toInt(), token, type)
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
                uploadViewModel.uploadTrans(selectedFile, f, id!!.toInt(), token, type)
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
        val cal = Calendar.getInstance()
        val myFormat = "dd.MM.yyyy"
        val dateSetListenerFrom =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textDateFrom.text = sdf.format(cal.time)
            }
        textDateFrom.setOnClickListener {
            DatePickerDialog(
                this, dateSetListenerFrom,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dateSetListenerTo =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textDateTo.text = sdf.format(cal.time)
            }
        textDateTo.setOnClickListener {
            DatePickerDialog(
                this, dateSetListenerTo,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setCur(array: Array<String>) {

        val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            array
        )
        currencies1.threshold = 0
        currencies1.setAdapter(adapter)
        currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }

        val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
        val adapter2 = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            array
        )
        currencies2.threshold = 0
        currencies2.setAdapter(adapter2)
        currencies2.setOnFocusChangeListener { _, b -> if (b) currencies2.showDropDown() }


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
            mainViewModel.getStringWithInstruments()
        }
        )

        mainViewModel.relevantRatesSuccessLiveData.observe(this, Observer {

            rvRatesAdapter.setRates(it)
            mainViewModel.multiplyRelevant()
        }
        )

        mainViewModel.balancesMultRates.observe(this, Observer {

            chartAdapter.setChart(it, chart!!)
            setRates()

        }
        )
        columnViewModel.columnGraphData.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
            aaChartView = findViewById(R.id.AAChartView)
            columnChartAdapter.setColumnChart(aaChartView, it, k.text.toString().toInt())
        })
        columnViewModel.yearBalanceLiveData.observe(this, Observer {
            columnViewModel.getStringWithInstrumentsForColumn(it[12].keys.toMutableList())
        })
        columnViewModel.stringWithInstruments2.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
            columnViewModel.getRatesForTime(it, k.text.toString().toInt())
        })
        columnViewModel.rateSuccessLiveData.observe(this, Observer {
            columnViewModel.multiplyRes()
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
            incomeChartAdapter.setIncomeChart(aaChartView, it.first, it.second)
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
            curBalanceChartAdapter.setCurBalanceChart(aaChartView, it.first, it.second)
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
            relativeRatesAdapter.setRatesChart(it, aaChartView)
        }
        )
        inputViewModel.valuesForInput.observe(this, Observer {
            inputViewModel.getRatesForTimeInput(it.first, it.second.first, it.second.second)
        }
        )
        inputViewModel.inOutSuccessLiveData.observe(this, Observer {
            inputViewModel.calculationInput()
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
                inputViewModel.valuesForInput.value!!.second
            )
        }
        )
        incomePortViewModel.incomeFilterSuccessLiveData.observe(this, Observer {
            incomePortViewModel.getRatesForIncome(it.first, it.second.first, it.second.second)
        }
        )
        incomePortViewModel.incomePortSuccessLiveData.observe(this, Observer {
            incomePortViewModel.modelingSeriesForIncome()
        }
        )
        incomePortViewModel.resultIncomePortLiveData.observe(this, Observer {
            incomeChartAdapter.setIncomeChart(aaChartView, it.first, it.second)
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


