package com.aldosolis.systemsales.Datos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.aldosolis.systemsales.ClientesProvider
import com.aldosolis.systemsales.ComprobantesProvider
import com.aldosolis.systemsales.Entidad.Cliente
import com.aldosolis.systemsales.Entidad.Comprobante
import com.aldosolis.systemsales.Entidad.DetalleVentaPrincipal
import com.aldosolis.systemsales.Entidad.Inventario
import com.aldosolis.systemsales.Entidad.Producto
import com.aldosolis.systemsales.Entidad.Ventas
import com.aldosolis.systemsales.InventarioProvider
import com.aldosolis.systemsales.ProductosProvider
import com.aldosolis.systemsales.VentasProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineStart
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ConexionFirebase(val context: Context) {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var procedimientos: Procedimientos
    init {
        // Inicializar la aplicación Firebase
        FirebaseApp.initializeApp(context)
        procedimientos  =Procedimientos()
        // Habilitar la persistencia de datos si es necesario
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.setPersistenceEnabled(true)

    }

    fun agregarCliente(cliente: Cliente) {
        // Obtener una referencia al nodo "clientes"
        val clientesRef = firebaseDatabase.getReference().child("clientes")

        // Generar automáticamente un ID único para el cliente
        val nuevoClienteRef = clientesRef.push()

        // Establecer los valores del cliente en la referencia generada
        nuevoClienteRef.setValue(cliente)
            .addOnSuccessListener {
                // Operación exitosa
                Toast.makeText(context, "Cliente agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error al agregar cliente
                Toast.makeText(context, "Error al agregar cliente: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun agregarVenta(venta: Ventas) {
        // Obtener una referencia al nodo "clientes"
        val ventasRef = firebaseDatabase.getReference().child("ventas")

        // Generar automáticamente un ID único para el cliente
        val nuevoVentaRef = ventasRef.push()

        // Establecer los valores del cliente en la referencia generada
        nuevoVentaRef.setValue(venta)
            .addOnSuccessListener {
                // Operación exitosa
                Toast.makeText(context, "Venta agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error al agregar cliente
                Toast.makeText(context, "Error al agregar venta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun agregarDetalleVenta(detalleVenta: DetalleVentaPrincipal) {
        // Obtener una referencia al nodo "clientes"
        val ventasRef = firebaseDatabase.getReference().child("detalleVentas")

        // Generar automáticamente un ID único para el cliente
        val nuevoVentaRef = ventasRef.push()

        // Establecer los valores del cliente en la referencia generada
        nuevoVentaRef.setValue(detalleVenta)
            .addOnSuccessListener {
                // Operación exitosa
                Toast.makeText(context, "El detalle de la venta fue agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error al agregar cliente
                Toast.makeText(context, "Error al agregar detalle venta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun actualizarInventario(inventario: Inventario) {
        // Convertir arrays a listas si es necesario

        val nuevosDatos =inventario.toMap()
        // Obtener una referencia al nodo de la venta que se desea editar
        val ventaRef = firebaseDatabase.getReference().child("inventario").child(inventario.id.toString())

        // Actualizar los valores de la venta con los nuevos datos
        ventaRef.updateChildren(nuevosDatos!!)
            .addOnSuccessListener {
                // Operación exitosa
                Toast.makeText(context, "Inventario actualizada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error al editar la venta
                Toast.makeText(context, "Error al editar Inventario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    @OptIn(ExperimentalEncodingApi::class)
    fun cargarDatosInventario() {
        val databaseReference = firebaseDatabase.getReference().child("inventario")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterar sobre los hijos de dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val inventario = Inventario()
                    // Obtener los valores de cada hijo y crear un objeto Inventario
                    val id = snapshot.child("id").getValue(String::class.java)
                    val fotoPro = snapshot.child("fotoPro").getValue(String::class.java)
                    val codigoPro = snapshot.child("codigoPro").getValue(String::class.java)
                    val nombrePro = snapshot.child("nombrePro").getValue(String::class.java)
                    val cantidad = snapshot.child("cantidad").getValue(String::class.java)
                    val costoUnitarioPro = snapshot.child("costoUnitarioPro").getValue(String::class.java)
                    val precioVenta = snapshot.child("precioVentaPro").getValue(String::class.java)
                    val montoTotal = snapshot.child("montoTotalPro").getValue(String::class.java)
                    val tipoCargo = snapshot.child("tipoCargoPro").getValue(String::class.java)

                    inventario.id = id
                    inventario.fotoPro = fotoPro
                    inventario.codigoPro = codigoPro
                    inventario.nombrePro = nombrePro
                    inventario.costoUnitarioPro = costoUnitarioPro
                    inventario.precioVentaPro = precioVenta
                    inventario.cantidad = cantidad
                    inventario.montoTotalPro = montoTotal
                    inventario.tipoCargoPro = tipoCargo

                    // Agregar el objeto Inventario a la lista
                    InventarioProvider.inventarioList.add(inventario)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error si ocurrió uno
                println("Error al obtener datos: ${databaseError.message}")
            }
        })
    }
    fun cargarDatosProductos() {
        val databaseReference = firebaseDatabase.getReference().child("productos")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterar sobre los hijos de dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val producto = Producto()
                    // Obtener los valores de cada hijo y crear un objeto Inventario
                    val id = snapshot.child("id").getValue(String::class.java)
                    val foto = snapshot.child("fotoPro").getValue(String::class.java)
                    val codigo = snapshot.child("codigoPro").getValue(String::class.java)
                    val nombre = snapshot.child("nombrePro").getValue(String::class.java)
                    val descripcion = snapshot.child("descripcionPro").getValue(String::class.java)

                    val costoUnitario = snapshot.child("costoUnitarioPro").getValue(String::class.java)
                    val precioVenta = snapshot.child("precioVentaPro").getValue(String::class.java)
                    val categoria = snapshot.child("categoriaPro").getValue(String::class.java)
                    val presentacion= snapshot.child("presentacionPro").getValue(String::class.java)
                    val tipoCargo = snapshot.child("tipoCargoPro").getValue(String::class.java)

                    producto.id = id
                    producto.fotoPro = foto?.let { decodeBase64ToByteArray(it) }
                   producto.codigoPro = codigo
                   producto.nombrePro = nombre
                    producto.descripcionPro = descripcion
                   producto.costoUnitarioPro = costoUnitario
                   producto.precioVentaPro = precioVenta
                    producto.categoriaPro = categoria
                    producto.presentacionPro = presentacion
                   producto.tipoCargoPro = tipoCargo

                    // Agregar el objeto Inventario a la lista
                    ProductosProvider.productosList.add(producto)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error si ocurrió uno
                println("Error al obtener datos: ${databaseError.message}")
            }
        })
    }
    fun cargarDatosComprobantes() {
        val databaseReference = firebaseDatabase.getReference().child("tipoComprobantes")
        // Agregar un listener para traer los datos una sola vez
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterar sobre los hijos de dataSnapshot

                for (snapshot in dataSnapshot.children) {
                    val comprobante = Comprobante()
                    // Obtener los valores de cada hijo y crear un objeto Inventario
                    var nombreCmp = snapshot.child("nombreCmp").getValue(String::class.java)
                    var tipoCmp = snapshot.child("tipoCmp").getValue(String::class.java)
                    var correlativo = snapshot.child("correlativo").getValue(String::class.java)

                    comprobante.nombreCmp = nombreCmp
                    comprobante.tipoCmp = tipoCmp
                    comprobante.correlativo = correlativo

                    // Agregar el objeto Inventario a la lista
                    ComprobantesProvider.comprobantesList.add(comprobante)

                }

                // Aquí listaInventarioFirebase contiene todos los productos de Firebase
                // Puedes hacer lo que quieras con la lista, como mostrarla en un RecyclerView
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error si ocurrió uno
                println("Error al obtener datos: ${databaseError.message}")
            }
        })

    }
     fun cargarDatosClientes() {

        val databaseReference = firebaseDatabase.getReference().child("clientes")
        // Agregar un listener para traer los datos una sola vez
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val cliente = Cliente()

                    var codigoCli: String? = snapshot.child("codigoCli").getValue(String::class.java)
                    var nombreCli: String? = snapshot.child("nombreCli").getValue(String::class.java)
                    var rucCli: String? = snapshot.child("rucCli").getValue(String::class.java)
                    var direccionCli: String? = snapshot.child("direccionCli").getValue(String::class.java)
                    var telefonoCli: String? = snapshot.child("telefonoCli").getValue(String::class.java)
                    var emailCli: String? = snapshot.child("emailCli").getValue(String::class.java)
                    var estadoCli: String? = snapshot.child("estadoCli").getValue(String::class.java)
                    cliente.id = snapshot.key ?: ""
                    cliente.codigoCli = codigoCli
                    cliente.nombreCli = nombreCli
                    cliente.rucCli = rucCli
                    cliente.direccionCli = direccionCli
                    cliente.telefonoCli = telefonoCli
                    cliente.emailCli = emailCli
                    cliente.estadoCli =estadoCli
                    ClientesProvider.clientesList.add(cliente)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si ocurren durante la recuperación de datos
                println("Error al obtener datos: ${databaseError.message}")
            }
        })
    }
    fun cargarDatosVentas() {

        val databaseReference = firebaseDatabase.getReference().child("ventas")
        // Agregar un listener para traer los datos una sola vez
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val venta = Ventas()
                    venta.id = snapshot.child("id").getValue(String::class.java)
                    venta.idCliente = snapshot.child("idCliente").getValue(String::class.java)
                    venta.idVenta = snapshot.child("idVenta").getValue(String::class.java)
                    venta.numFactura = snapshot.child("numFactura").getValue(String::class.java)
                    venta.fechaVenta = snapshot.child("fechaVenta").getValue(String::class.java)
                    venta.comprobante = snapshot.child("comprobante").getValue(String::class.java)
                    venta.subTotal = snapshot.child("subTotal").getValue(String::class.java)
                    venta.descuento = snapshot.child("descuento").getValue(String::class.java)
                    venta.IGV = snapshot.child("IGV").getValue(String::class.java)
                    venta.montoTotal = snapshot.child("montoTotal").getValue(String::class.java)
                    venta.estado = snapshot.child("estado").getValue(String::class.java)
                    venta.idUsuario = snapshot.child("idUsuario").getValue(String::class.java)
                    venta.nombreUsuario = snapshot.child("nombreUsuario").getValue(String::class.java)
                    venta.formaPago = snapshot.child("formaPago").getValue(String::class.java)
                    VentasProvider.ventasList.add(venta)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si ocurren durante la recuperación de datos
                println("Error al obtener datos: ${databaseError.message}")
            }
        })
    }
    fun calcularCantidadVentas(onCodigoOrdenadoGenerado: (String) -> Unit) {
        val databaseReference = firebaseDatabase.getReference().child("ventas")
        databaseReference.keepSynced(true)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // El nodo "ventas" existe y contiene datos
                    val totalRegistrosVentas = dataSnapshot.childrenCount.toInt() +1
                    val codigoOrdenado = generarCodigoOrdenado("C"+procedimientos.generarCodigo3Digitos(),totalRegistrosVentas)
                    // Invocar la función pasada como parámetro con el código ordenado generado
                    onCodigoOrdenadoGenerado(codigoOrdenado)

                    // Aquí puedes hacer lo que necesites con totalRegistros
                    Toast.makeText(context, "Total de registros de ventas: $totalRegistrosVentas", Toast.LENGTH_LONG).show()
                } else {
                    // El nodo "ventas" no existe o está vacío
                    Toast.makeText(context, "No hay registros de ventas", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si es necesario
                println("Error al obtener datos: ${databaseError.message}")
            }
        })
    }
    private fun generarCodigoOrdenado(tipoComprobante:String,totalRegistrosVentas: Int): String {
        return tipoComprobante +"-"+ String.format("%07d", totalRegistrosVentas)
    }
    @OptIn(ExperimentalEncodingApi::class)
    fun decodeBase64ToByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String)
    }

    fun convertirByteArrayABase64(byteArray: ByteArray): String {
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

}