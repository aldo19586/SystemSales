package com.aldosolis.systemsales.Datos

import com.aldosolis.systemsales.ClientesProvider
import com.aldosolis.systemsales.Entidad.Ventas
import com.aldosolis.systemsales.InventarioProvider
import com.aldosolis.systemsales.ProductosProvider
import kotlin.random.Random

class Procedimientos {
    fun encontrarPresentacionPorCodigo(codigoPro:String):String{
        var presentacionEncontrado ="";
        for(producto in ProductosProvider.productosList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.presentacionPro.toString()
            }
        }
        return presentacionEncontrado
    }
    fun encontrarIdPorCodigo(codigoPro:String):String{
        var presentacionEncontrado ="";
        for(producto in InventarioProvider.inventarioList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.id.toString()
            }
        }
        return presentacionEncontrado
    }
    fun encontrarFotoPorCodigo(codigoPro:String):String{
        var presentacionEncontrado=""
        for(producto in InventarioProvider.inventarioList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.fotoPro.toString()
            }
        }
        return presentacionEncontrado
    }
    fun obtenerCantidadPorCodigo(codigoPro:String):String{
        var presentacionEncontrado ="";
        for(producto in InventarioProvider.inventarioList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.cantidad.toString()
            }
        }
        return presentacionEncontrado
    }
    fun encontrarCostoUnitarioPorCodigo(codigoPro:String):String{
        var presentacionEncontrado ="";
        for(producto in InventarioProvider.inventarioList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.costoUnitarioPro.toString()
            }
        }
        return presentacionEncontrado
    }
    fun encontrarTipoCargoPorCodigo(codigoPro:String):String{
        var presentacionEncontrado ="";
        for(producto in InventarioProvider.inventarioList){
            if(producto.codigoPro == codigoPro){
                presentacionEncontrado = producto.tipoCargoPro.toString()
            }
        }
        return presentacionEncontrado
    }
    fun encontrarIdClientePorNombre(nombreCli:String):String{
        var IdEncontrado ="";
        for(cliente in ClientesProvider.clientesList){
            if(cliente.nombreCli == nombreCli){
                IdEncontrado = cliente.id.toString()
            }
        }
        return IdEncontrado
    }
    fun encontrarIdInventarioPorCodigo(codigoPro:String):String{
        var IdEncontrado ="";
        for(inventario in InventarioProvider.inventarioList){
            if(inventario.codigoPro == codigoPro){
                IdEncontrado = inventario.id.toString()
            }
        }
        return IdEncontrado
    }
    fun generarCodigoFactura(ventas: MutableList<Ventas>, nombreCmp: String): String {
        var cantidadFacturas = 0
        var cantidadBoletas = 0
        var cantidadRecibos = 0
        var codigo = ""

        when (nombreCmp) {
            "Boleta" -> {
                val ventasBoletas = ventas.filter { venta ->
                    venta.comprobante!!.length >= 4 &&
                            venta.comprobante!!.substring(0, 4).equals(nombreCmp.substring(0, 4), ignoreCase = true)
                }
                cantidadBoletas = if (ventasBoletas.isEmpty()) 1 else ventasBoletas.size + 1
                codigo ="BOLE" + "%07d".format(cantidadBoletas)
            }
            "Factura" -> {
                val ventasFacturas = ventas.filter { venta ->
                    venta.comprobante!!.length >= 4 &&
                            venta.comprobante!!.substring(0, 4).equals(nombreCmp.substring(0, 4), ignoreCase = true)
                }
                cantidadFacturas = if (ventasFacturas.isEmpty()) 1 else ventasFacturas.size + 1
                codigo = "FACT"+ "%07d".format(cantidadFacturas)
            }
            "Recibo por Honorarios" -> {
                val ventasRecibos = ventas.filter { venta ->
                    venta.comprobante!!.length >= 4 &&
                            venta.comprobante!!.substring(0, 4).equals(nombreCmp.substring(0, 4), ignoreCase = true)
                }
                cantidadRecibos = if (ventasRecibos.isEmpty()) 1 else ventasRecibos.size + 1
                codigo = "REHO"+"%07d".format(cantidadRecibos)
            }
        }

        return codigo
    }
    fun generarCodigo(tipoCodigo: String): String {
        var codigo: String? = null

        // Crear una instancia de la clase Random
        val random = Random

        // Generar un número aleatorio entre 1000000 y 9999999 (7 dígitos)
        val numeroAleatorio = random.nextInt(1000000, 10000000)

        codigo = (tipoCodigo + numeroAleatorio).toString()

        return codigo
    }
    fun generarCodigo3Digitos(): String {
        var codigo: String? = null

        // Crear una instancia de la clase Random
        val random = Random

        // Generar un número aleatorio entre 1000000 y 9999999 (7 dígitos)
        val numeroAleatorio = random.nextInt(100, 1000)

        codigo =  numeroAleatorio.toString()

        return codigo
    }
}