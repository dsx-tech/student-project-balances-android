package com.example.portfolio.view

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolio.R
import com.example.portfolio.model.Portfolio

class RVPortfolioAdapter  : RecyclerView.Adapter<RVPortfolioAdapter.ViewHolder>() {
    val selectedPortfolioLiveData = MutableLiveData<Pair<String, Int>>()
    val deletePortfolioLiveData = MutableLiveData<Pair<Int, String>>()
    private val portfolioList : MutableList<Portfolio> = mutableListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.activity_with_potfolios, p0, false)
        return ViewHolder(v).listen { pos, _ ->
            selectedPortfolioLiveData.postValue(Pair(portfolioList[pos].name, portfolioList[pos].id))

        }
    }
    fun setPortfolios(portfolios: MutableList<Portfolio>) {

        portfolioList.addAll(portfolios)

    }
    fun addPortfolio(portfolio:Portfolio) {

        portfolioList.add(0, portfolio)

    }
    fun deletePortfolio() {

        portfolioList.remove(Portfolio(deletePortfolioLiveData.value!!.first, deletePortfolioLiveData.value!!.second))

    }
    override fun getItemCount(): Int {
        return portfolioList.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.name?.text = portfolioList[p1].name
        p0.count?.text = portfolioList[p1].id.toString()
        if (p1 == portfolioList.size - 1){
            p0.button.visibility = View.INVISIBLE
            p0.count.visibility = View.INVISIBLE
        }
        p0.button.setOnClickListener {
          deletePortfolioLiveData.postValue( Pair(portfolioList[p1].id,  portfolioList[p1].name))
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.findViewById<Button>(R.id.btn_clear)
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val count = itemView.findViewById<TextView>(R.id.tvCount)
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this

    }


}