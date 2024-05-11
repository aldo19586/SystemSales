package com.aldosolis.systemsales.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.DetalleVentasTemporalProvider
import com.aldosolis.systemsales.Entidad.DetalleVenta
import com.aldosolis.systemsales.Entidad.Inventario

import com.aldosolis.systemsales.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ProductViewHolder(view:View):RecyclerView.ViewHolder(view) {
    val productName = view.findViewById<TextView>(R.id.tvNombreProducto)
    val productPrice = view.findViewById<TextView>(R.id.tvPrecioProducto)
    val productPricexAmount = view.findViewById<TextView>(R.id.tvPrecioXcantidad)
    val productAmount = view.findViewById<TextView>(R.id.tvCantidad)
    val productImage = view.findViewById<ImageView>(R.id.imgProducto)
    val tvPresentacion = view.findViewById<TextView>(R.id.tvPresentacion)
    val tvCantidadInventario = view.findViewById<TextView>(R.id.tvCantidadInventario)

    val marqueeAnimation = AnimationUtils.loadAnimation(view.context, R.anim.desplazamiento)

    init {
        // Asigna un valor al productName (por ejemplo, "Nombre del producto")

    }
    var btnAdd = view.findViewById<TextView>(R.id.tvAumentar).setOnClickListener {
        var addProduct = productAmount.text.toString().toInt() + 1
        productAmount.text = addProduct.toString()
        productPricexAmount.text = (productPrice.text.toString().toDouble() * productAmount.text.toString().toInt()).toString()
    }
    var btnDelete = view.findViewById<TextView>(R.id.tvRestar).setOnClickListener {
        var reduceProduct = productAmount.text.toString().toInt() - 1
        if (reduceProduct <= 0) {
            productAmount.text = "1"

        } else {
            productAmount.text = reduceProduct.toString()
            productPricexAmount.text = (productPricexAmount.text.toString().toDouble()-productPrice.text.toString().toDouble()).toString()
        }
    }

    fun render(productModel: DetalleVenta){
        productName.text = productModel.nombrePro
        productPrice.text = productModel.precioVentaPro
        productAmount.text = "1"
        productImage.setImageBitmap(base64ToBitmap(productModel.fotoPro.toString()))
        tvPresentacion.text = productModel.presentacionPro
        tvCantidadInventario.text = productModel.cantidad
        productName.startAnimation(marqueeAnimation)
        val amount = productAmount.text.toString().toDoubleOrNull() ?: 0.0 // Convertir el texto a Double, o 0.0 si no se puede convertir
        val price = productModel.precioVentaPro?.toDoubleOrNull() ?: 0.0 // Co
        val total = amount * price
        val decimalFormat = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))
        // Formateamos el número usando el objeto DecimalFormat
        val totalFormateado = decimalFormat.format(total)
        productPricexAmount.text = totalFormateado
    }
    @OptIn(ExperimentalEncodingApi::class)
    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(base64String)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

}