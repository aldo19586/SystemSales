package com.aldosolis.systemsales

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.Datos.ConexionFirebase
import com.aldosolis.systemsales.Datos.Procedimientos
import com.aldosolis.systemsales.Entidad.Cliente
import com.aldosolis.systemsales.Entidad.Comprobante
import com.aldosolis.systemsales.Entidad.DetalleVentaPrincipal
import com.aldosolis.systemsales.Entidad.Inventario
import com.aldosolis.systemsales.Entidad.Ventas
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class CustomDialog(var activity: Activity, private val conexionFirebase: ConexionFirebase,private var rvDetalleVenta:RecyclerView) : Dialog(activity),CustomDialogComprobante.OnItemClickedListener {
    private lateinit var spinnerComprobante: Spinner
    private lateinit var spinnerTipoPago: Spinner
    private lateinit var numComprobante:EditText
    private lateinit var nFactura:EditText
    private lateinit var tipoPago:EditText
    private lateinit var cantidadRecibida:EditText
    private  lateinit var btnBuscarComprobante:TextView
    private  lateinit var pagoRecibidio:TextView
    private  lateinit var pagoCambio:TextView
    private  lateinit var total:TextView
    private  lateinit var lblPagoRecibido:TextView
    private  lateinit var lblPagoCambio:TextView
    private lateinit var tvFecha :TextView
    private lateinit var  nombreCliente:EditText


    private lateinit var llEspecial: LinearLayout

    lateinit var procedimientos: Procedimientos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venta_layout) // Establece el diseño personalizado

        procedimientos = Procedimientos()
        numComprobante = findViewById<EditText>(R.id.etNumComprobante)
       nFactura = findViewById<EditText>(R.id.etNumFactura)
        tipoPago = findViewById<EditText>(R.id.etTipoPago)
        btnBuscarComprobante= findViewById<TextView>(R.id.btnBuscarComprobante)
        spinnerComprobante = findViewById<Spinner>(R.id.spinnerTipoComprobante)
        spinnerTipoPago = findViewById<Spinner>(R.id.spinnerTipoPago)
        cantidadRecibida = findViewById<EditText>(R.id.etCantidadRecibida)
        pagoRecibidio = findViewById<TextView>(R.id.tvPagoRecibido)
        pagoCambio = findViewById<TextView>(R.id.tvPagoCambio)
        total  = findViewById<TextView>(R.id.tvMontoTotal)
        llEspecial = findViewById(R.id.llEspecial)
        lblPagoRecibido =findViewById(R.id.lblPagoRecibido)
        lblPagoCambio=findViewById(R.id.lblPagoCambio)
        tvFecha = findViewById(R.id.tvFecha)
        nombreCliente = findViewById<EditText>(R.id.etCliente)
        var Total  = findViewById<TextView>(R.id.tvMontoTotal)
        var agregarVenta = findViewById<TextView>(R.id.btnAgregarVenta)
        iniciarFecha()

        agregarVenta.setOnClickListener {
            if(nFactura.text.isEmpty()||
                nombreCliente.text.isEmpty()||
                numComprobante.text.isEmpty()||
                tipoPago.text.isEmpty()){
                Toast.makeText(context, "Por favor complete los campos vacios", Toast.LENGTH_SHORT).show()
            }else{
                val nuevaVenta = Ventas().apply {
                    idVenta = procedimientos.generarCodigo("VENT")
                    idCliente = procedimientos.encontrarIdClientePorNombre(nombreCliente.text.toString())
                    numFactura =nFactura.text.toString()
                    fechaVenta = tvFecha.text.toString()
                    comprobante = numComprobante.text.toString()
                    subTotal = total.text.toString()
                    descuento="0.00"
                    IGV="0"
                    montoTotal = total.text.toString()
                    estado = "Emitido"
                    idUsuario = "-NxJ3L-xhzA8AJjfe0-Q"
                    nombreUsuario = "vldosolis"
                    formaPago = tipoPago.text.toString()
                }
                for(detalleVenta in DetalleVentasTemporalProvider.detalleVentasList){

                    val nuevaDetalleVenta = DetalleVentaPrincipal().apply {
                        idVenta = nuevaVenta.idVenta
                        idProducto = procedimientos.encontrarIdPorCodigo(detalleVenta.codigoPro.toString())
                        nombreProducto = detalleVenta.nombrePro
                        presentacion = detalleVenta.presentacionPro
                        val position = DetalleVentasTemporalProvider.detalleVentasList.indexOf(detalleVenta)
                        if (position != RecyclerView.NO_POSITION) {
                            // Obtiene la cantidad del TextView en esa posición del RecyclerView
                            val cantidadView = rvDetalleVenta.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvCantidad)
                            val subTotalDetallePro = rvDetalleVenta.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvPrecioXcantidad)
                            // Comprueba si la vista de cantidad no es nula y si contiene un valor numérico
                            val cantidadString = cantidadView?.text.toString()?:"0"
                            cantidad = cantidadString
                            subTotal = subTotalDetallePro?.text.toString()
                            montoTotal=subTotalDetallePro?.text.toString()
                        } else {
                            cantidad = "0" // Valor por defecto si no se encuentra la posición
                        }
                        precioVenta = detalleVenta.precioVentaPro
                        descuento = "0.00"
                        IGV = "0"
                        }

                    val inventario = Inventario().apply {
                        id = procedimientos.encontrarIdInventarioPorCodigo(detalleVenta.codigoPro.toString())
                        fotoPro = procedimientos.encontrarFotoPorCodigo(detalleVenta.codigoPro.toString())
                        codigoPro = detalleVenta.codigoPro
                        nombrePro = detalleVenta.nombrePro
                        cantidad = (procedimientos.obtenerCantidadPorCodigo(detalleVenta.codigoPro.toString()).toDouble()- nuevaDetalleVenta.cantidad.toString().toDouble()).toString()
                        costoUnitarioPro = procedimientos.encontrarCostoUnitarioPorCodigo(detalleVenta.codigoPro.toString())
                        precioVentaPro = detalleVenta.precioVentaPro
                        montoTotalPro = nuevaDetalleVenta.montoTotal
                        tipoCargoPro = procedimientos.encontrarTipoCargoPorCodigo(detalleVenta.codigoPro.toString())
                    }

                    //conexionFirebase.actualizarInventario(inventario)
                    //conexionFirebase.agregarDetalleVenta(nuevaDetalleVenta)
                }
               // conexionFirebase.agregarVenta(nuevaVenta)

                Toast.makeText(context, "Venta agregada exitosamente", Toast.LENGTH_SHORT).show()
                this.dismiss()

            }



        }


        cantidadRecibida.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Obtener el texto del EditText

                val inputText = s.toString()
// Mostrar los TextView adicionales y ajustar el tamaño del diálogo
                if (inputText.isNotEmpty()) {
                    pagoRecibidio.visibility = View.VISIBLE
                    pagoCambio.visibility = View.VISIBLE
                    lblPagoRecibido.visibility = View.VISIBLE
                    lblPagoCambio.visibility = View.VISIBLE
                    val layoutParams = llEspecial.layoutParams
                    layoutParams.height = dpToPx(120, context) // Convertir dp a píxeles utilizando el contexto de la actividad
                    llEspecial.layoutParams = layoutParams
                    if(inputText.toString().toDouble()<Total.text.toString().toDouble()){
                        Toast.makeText(context,"El monto recibido es insuficiente",Toast.LENGTH_SHORT).show()
                        pagoRecibidio.text="0.00"
                        pagoCambio.text="0.00"
                    }else{


                        pagoRecibidio.text = inputText

                        // Obtener el total como un Double
                        val totalAmount = total.text.toString().toDoubleOrNull() ?: 0.0

                        // Obtener el pago recibido como un Double
                        val paymentReceived = inputText.toDoubleOrNull() ?: 0.0


                        // Calcular y actualizar el pagoCambio
                        pagoCambio.text = ( paymentReceived-totalAmount ).toString()

                        // Ajustar el tamaño del diálogo según sea necesario
                        val params =    this@CustomDialog.window?.attributes
                        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
                        this@CustomDialog.window?.attributes = params
                    }

                } else {
                    pagoRecibidio.visibility = View.INVISIBLE
                    pagoCambio.visibility = View.INVISIBLE
                    lblPagoRecibido.visibility = View.INVISIBLE
                    lblPagoCambio.visibility = View.INVISIBLE
                    val layoutParams = llEspecial.layoutParams
                    layoutParams.height = dpToPx(50, context) // Convertir dp a píxeles utilizando el contexto de la actividad
                    llEspecial.layoutParams = layoutParams
                }
                // Actualizar los TextViews




            }
        })
        // Llama a calcularCantidadVentas() para obtener el código ordenado
        conexionFirebase.calcularCantidadVentas { codigoOrdenado ->
            // Establece el texto ennFactura con el código ordenado obtenido
           nFactura.setText(codigoOrdenado)
        }

        spinnerComprobante.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                cargarSpinnerComprobante(ComprobantesProvider.comprobantesList, spinnerComprobante)
                spinnerComprobante.performClick() // Abre manualmente la lista desplegable del Spinner
            }
            true
        }



        spinnerComprobante.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener el elemento seleccionado
                val itemSeleccionado = parent?.getItemAtPosition(position).toString()

                numComprobante.setText(procedimientos.generarCodigoFactura(VentasProvider.ventasList,itemSeleccionado))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso en el que no se haya seleccionado ningún elemento
            }
        }
        cargarSpinnerTipoPago(spinnerTipoPago)

        spinnerTipoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener el elemento seleccionado
                val itemSeleccionado = parent?.getItemAtPosition(position).toString()

                tipoPago.setText(itemSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso en el que no se haya seleccionado ningún elemento
            }
        }
        btnBuscarComprobante.setOnClickListener {
            for(cliente in ClientesProvider.clientesList){
                Toast.makeText(context,cliente.id.toString(),Toast.LENGTH_SHORT).show()
            }

            val customDialogComprobante = CustomDialogComprobante(context,this)
            customDialogComprobante.show()

            val nombreCliente = customDialogComprobante.findViewById<EditText>(R.id.etNombreCliente)
            val btnAgregarCliente = customDialogComprobante.findViewById<Button>(R.id.btnAgregarCliente)
            val cardViewLista = customDialogComprobante.findViewById<CardView>(R.id.cardViewListaClientes)
            cardViewLista.setBackgroundResource(R.drawable.white_background)
            customDialogComprobante.setClientes(ClientesProvider.clientesList)

            btnAgregarCliente.setOnClickListener {
                val nuevoCliente = Cliente().apply {
                    codigoCli = procedimientos.generarCodigo("CLI")
                    nombreCli = nombreCliente.text.toString()
                    direccionCli = "Default"
                    emailCli ="Default"
                    estadoCli="Activo"
                    rucCli ="Default"
                    telefonoCli="Default"
                }

                if (nuevoCliente.nombreCli!!.isNotEmpty()) {
                    conexionFirebase.agregarCliente(nuevoCliente)
                    ClientesProvider.clientesList.add(nuevoCliente) // Agregar el nuevo cliente a la lista local
                    customDialogComprobante.setClientes(ClientesProvider.clientesList) // Actualizar la lista en el diálogo

                    // Actualizar la lista de clientes desde Firebase nuevamente para evitar duplicaciones
                    conexionFirebase.cargarDatosClientes()

                    nombreCliente.text.clear() // Limpiar el campo de entrada
                    Toast.makeText(context, "Cliente agregado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor, ingrese el nombre del cliente", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
    fun iniciarFecha(){
        val task = object : TimerTask() {
            override fun run() {
                val currentTime = Date()
                val longTimeString = android.text.format.DateFormat.getTimeFormat(context).format(currentTime)
                val longDateString = android.text.format.DateFormat.getDateFormat(context).format(currentTime)

                // Actualizar los textos de los TextViews en el hilo principal
                activity?.runOnUiThread {
                    tvFecha.text = longDateString
                }
            }
        }

        val timer = Timer("timer", true)
        timer.schedule(task, 0, 1000)
    }
    fun cargarSpinnerComprobante(comprobantes: MutableList<Comprobante>, spinner: Spinner) {
        val nombresComprobantes = comprobantes.map { it.nombreCmp } // Obtener solo los nombres de los comprobantes
        val mAdapter = ArrayAdapter(context, R.layout.item_spinner_comprobante, nombresComprobantes)
        spinner.adapter = mAdapter
    }
    fun cargarSpinnerTipoPago(spinner: Spinner) {
        val formasPago = mutableListOf("Al contado", "Transferencias (Yape o Plin)", "Fiado")

        val mAdapter = ArrayAdapter(context, R.layout.item_spinner_comprobante, formasPago)
        spinner.adapter = mAdapter
    }
    override fun onItemClicked(item: String) {
        nombreCliente.setText(item)
    }

    class CurrencyTextWatcher(private val editText: EditText) : TextWatcher {

        private val decimalFormat: DecimalFormat

        init {
            val symbols = DecimalFormatSymbols()
            symbols.groupingSeparator = ','
            symbols.decimalSeparator = '.'
            decimalFormat = DecimalFormat("#,##0.00", symbols)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            editText.removeTextChangedListener(this)

            try {
                val originalString = editable.toString()

                // Eliminar cualquier coma previamente agregada
                val cleanString = originalString.replace("[,.]".toRegex(), "")

                val parsed = cleanString.toDouble()
                val formatted = decimalFormat.format(parsed)

                editText.setText(formatted)
                editText.setSelection(formatted.length) // Colocar el cursor al final del texto

            } catch (ex: NumberFormatException) {
                // Manejar la excepción si el número no puede ser parseado
            }

            editText.addTextChangedListener(this)
        }
    }


}
