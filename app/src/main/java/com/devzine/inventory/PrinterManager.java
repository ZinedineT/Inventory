package com.devzine.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class PrinterManager {
    private static final int DEFAULT_PORT = 9100;
    private static final String PREFS_NAME = "PrinterPrefs";
    private static final String PRINTER_IP_KEY = "printer_ip";
    private static final int CONNECTION_TIMEOUT = 5000; // 5 segundos
    private Context context;
    private String printerIp;
    private int printerPort;
    private boolean isConnected = false;
    public PrinterManager(Context context) {
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.printerIp = prefs.getString(PRINTER_IP_KEY, "");
        this.printerPort = DEFAULT_PORT;
    }
    public void setPrinterIp(String ip) {
        this.printerIp = ip;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PRINTER_IP_KEY, ip).apply();
    }
    public String getPrinterIp() {
        return printerIp;
    }
    public boolean isPrinterConfigured() {
        return printerIp != null && !printerIp.isEmpty();
    }
    public void testConnection(final PrinterConnectionCallback callback) {
        if (!isPrinterConfigured()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onResult(false, "Impresora no configurada"));
            return;
        }
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(printerIp, printerPort), CONNECTION_TIMEOUT);
                isConnected = socket.isConnected();
                socket.close();

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResult(true, "Conectado a la impresora"));
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResult(false, "Error: " + e.getMessage()));
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void imprimirEtiqueta(Inmueble inmueble, final PrinterConnectionCallback callback) {
        if (!isPrinterConfigured()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onResult(false, "Impresora no configurada"));
            return;
        }
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(printerIp, printerPort), CONNECTION_TIMEOUT);

                if (socket.isConnected()) {
                    OutputStream outputStream = socket.getOutputStream();

                    // Comandos ESC/POS
                    // Inicializar impresora
                    outputStream.write(new byte[]{0x1B, 0x40});

                    // Centrar texto
                    outputStream.write(new byte[]{0x1B, 0x61, 0x01});

                    // Texto en negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x01});

                    // Título
                    String titulo = "INVENTARIO DE INMUEBLES\n";
                    outputStream.write(titulo.getBytes());

                    // Quitar negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x00});

                    // Alinear a la izquierda
                    outputStream.write(new byte[]{0x1B, 0x61, 0x00});

                    // Datos del inmueble
                    String datos = "--------------------------------\n";
                    datos += "Nombre: " + inmueble.getNombre() + "\n";
                    datos += "Codigo: " + inmueble.getCodigo() + "\n";
                    datos += "Cantidad: " + inmueble.getCantidad() + "\n";
                    datos += "Precio: S/ " + inmueble.getPrecio() + "\n";
                    datos += "Area: " + inmueble.getArea() + "\n";
                    datos += "--------------------------------\n";
                    outputStream.write(datos.getBytes());

                    // Avanzar papel
                    outputStream.write(new byte[]{0x0A, 0x0A, 0x0A, 0x0A});

                    // Cortar papel (si la impresora lo soporta)
                    outputStream.write(new byte[]{0x1D, 0x56, 0x00});

                    outputStream.flush();
                    socket.close();

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onResult(true, "Impresión enviada con éxito"));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onResult(false, "No se pudo conectar a la impresora"));
                }
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResult(false, "Error de impresión: " + e.getMessage()));
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void imprimirListaInmuebles(List<Inmueble> inmuebles, String area, final PrinterConnectionCallback callback) {
        if (!isPrinterConfigured()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onResult(false, "Impresora no configurada"));
            return;
        }

        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(printerIp, printerPort), CONNECTION_TIMEOUT);

                if (socket.isConnected()) {
                    OutputStream outputStream = socket.getOutputStream();

                    // Inicializar impresora
                    outputStream.write(new byte[]{0x1B, 0x40});

                    // Centrar texto
                    outputStream.write(new byte[]{0x1B, 0x61, 0x01});

                    // Texto en negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x01});

                    // Título
                    String titulo = "REPORTE DE INMUEBLES\n";
                    titulo += "Area: " + area + "\n\n";
                    outputStream.write(titulo.getBytes());

                    // Quitar negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x00});

                    // Alinear a la izquierda
                    outputStream.write(new byte[]{0x1B, 0x61, 0x00});

                    // Cabecera de la tabla
                    String cabecera = "NOMBRE | CODIGO | CANT | PRECIO | AREA\n";
                    cabecera += "--------------------------------\n";
                    outputStream.write(cabecera.getBytes());

                    // Datos de los inmuebles
                    for (Inmueble inmueble : inmuebles) {
                        String linea = inmueble.getNombre() + " | ";
                        linea += inmueble.getCodigo() + " | ";
                        linea += inmueble.getCantidad() + " | ";
                        linea += inmueble.getPrecio() + " | ";
                        linea += inmueble.getArea() + "\n";
                        outputStream.write(linea.getBytes());
                    }

                    // Pie de página
                    String piePagina = "--------------------------------\n";
                    piePagina += "Total: " + inmuebles.size() + " inmuebles\n";
                    piePagina += "Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\n";
                    outputStream.write(piePagina.getBytes());

                    // Avanzar papel
                    outputStream.write(new byte[]{0x0A, 0x0A, 0x0A, 0x0A});

                    // Cortar papel (si la impresora lo soporta)
                    outputStream.write(new byte[]{0x1D, 0x56, 0x00});

                    outputStream.flush();
                    socket.close();

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onResult(true, "Reporte impreso con éxito"));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onResult(false, "No se pudo conectar a la impresora"));
                }
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onResult(false, "Error de impresión: " + e.getMessage()));
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public interface PrinterConnectionCallback {
        void onResult(boolean success, String message);
    }
}