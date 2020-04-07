package com.example.portfolio.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolio.R
import com.example.portfolio.model.Correlation

class RVCorrelationAdapter: RecyclerView.Adapter<RVCorrelationAdapter.ViewHolder>() {
    val selectedCorLiveData = MutableLiveData<Pair<String, Float>>()
    val deleteCorLiveData = MutableLiveData<Boolean>()
    private val correlationList : MutableList<Correlation> = mutableListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.correlation_item, p0, false)
        return ViewHolder(v).listen { pos, _ ->
            selectedCorLiveData.postValue(Pair(correlationList[pos].instrument, correlationList[pos].value))

        }
    }
    fun setCorrelation(correlations: MutableList<Correlation>) {

        correlationList.addAll(correlations)

    }
    fun addCorrelation(correlation: Correlation) {

        correlationList.add(0, correlation)

    }
    fun deleteCorrelation(instrument : String, value : Float ) {

        correlationList.remove(Correlation(instrument, value))
        deleteCorLiveData.postValue(true)

    }
    override fun getItemCount(): Int {
        return correlationList.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.name?.text = correlationList[p1].instrument
        p0.count?.text = correlationList[p1].value.toString()
        p0.button.setOnClickListener {
            deleteCorrelation(correlationList[p1].instrument,  correlationList[p1].value)
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.findViewById<Button>(R.id.btn_clear)
        val name = itemView.findViewById<TextView>(R.id.instrument)
        val count = itemView.findViewById<TextView>(R.id.correlation)
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this

    }


}