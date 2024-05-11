package com.aldosolis.systemsales.Entidad

class Inventario() {
    var id: String? = null
    var fotoPro: String? = null
    var codigoPro: String? = null
    var nombrePro: String? = null
    var cantidad: String? = null
    var costoUnitarioPro: String? = null
    var precioVentaPro: String? = null
    var montoTotalPro: String? = null
    var tipoCargoPro: String? = null


    // Método para convertir la instancia de la clase en un mapa
    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map["id"] = id
        map["fotoPro"] = fotoPro
        map["codigoPro"] = codigoPro
        map["nombrePro"] = nombrePro
        map["cantidad"] = cantidad
        map["costoUnitarioPro"] = costoUnitarioPro
        map["precioVentaPro"] = precioVentaPro
        map["montoTotalPro"] = montoTotalPro
        map["tipoCargoPro"] = tipoCargoPro
        return map
    }
}