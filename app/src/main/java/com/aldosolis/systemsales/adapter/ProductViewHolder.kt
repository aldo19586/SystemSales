package com.aldosolis.systemsales.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.Product
import com.aldosolis.systemsales.ProductProvider
import com.aldosolis.systemsales.R

class ProductViewHolder(view:View):RecyclerView.ViewHolder(view) {
    val productName = view.findViewById<TextView>(R.id.tvProductName)
    val productPrice = view.findViewById<TextView>(R.id.tvProductPrice)
    val productPricexAmount = view.findViewById<TextView>(R.id.tvProductPricexAmount)
    val productAmount = view.findViewById<TextView>(R.id.tvProductAmount)
    val productImage = view.findViewById<TextView>(R.id.tvProductName)


    var btnAdd = view.findViewById<ImageView>(R.id.btnAdd).setOnClickListener {
        var addProduct = productAmount.text.toString().toInt() + 1
        productAmount.text = addProduct.toString()
        productPricexAmount.text = (productPrice.text.toString().toDouble() * productAmount.text.toString().toInt()).toString()
    }
    var btnDelete = view.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
        var reduceProduct = productAmount.text.toString().toInt() - 1
        if (reduceProduct <= 0) {
            productAmount.text = "1"
        } else {
            productAmount.text = reduceProduct.toString()
            productPricexAmount.text = (productPricexAmount.text.toString().toDouble()-productPrice.text.toString().toDouble()).toString()
        }
    }

    fun render(productModel:Product){
        productName.text = productModel.getName()
        productPrice.text = productModel.getPrice()
        productAmount.text = productModel.getAmount()
        productPricexAmount.text = (productModel.getPrice().toDouble()* productModel.getAmount().toInt()).toString()
    }

}