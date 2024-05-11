package com.aldosolis.systemsales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.DetalleVentasTemporalProvider
import com.aldosolis.systemsales.Entidad.DetalleVenta
import com.aldosolis.systemsales.Entidad.Inventario

import com.aldosolis.systemsales.R


class ProductAdapter(private val productList:MutableList <DetalleVenta>,
): RecyclerView.Adapter<ProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductViewHolder(layoutInflater.inflate(R.layout.item_product,parent,false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = productList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = productList.size





}