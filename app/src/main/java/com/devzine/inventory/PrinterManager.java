package com.devzine.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                    imprimirLogo(outputStream);

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
                    datos += String.format("Nombre: %s\n", inmueble.getNombre());
                    datos += String.format("Código: %s\n", inmueble.getCodigo());
                    datos += String.format("Cantidad: %s\n", inmueble.getCantidad());
                    datos += String.format("Precio: S/ %s\n", inmueble.getPrecio());
                    datos += String.format("Área: %s\n", inmueble.getArea());
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
                    outputStream.write(new byte[]{0x1B, 0x61, 0x01});
                    imprimirLogo(outputStream);

                    // Texto en negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x01});

                    // Título
                    String titulo = "REPORTE DE INMUEBLES\n";
                    titulo += "Area: " + area + "\n\n";
                    outputStream.write(titulo.getBytes());

                    // Quitar negrita
                    outputStream.write(new byte[]{0x1B, 0x45, 0x00});

                    // Cabecera de la tabla
                    String cabecera = String.format("%-13s %-8s %-5s %-8s %-10s%n",
                            "NOMBRE", "CODIGO", "CANT", "PRECIO", "AREA");
                    cabecera += "--------------------------------\n";
                    outputStream.write(cabecera.getBytes());

                    // Datos de los inmuebles
                    for (Inmueble inmueble : inmuebles) {
                        String linea = String.format("%-13s %-8s %-5s %-8s %-10s%n",
                                inmueble.getNombre(),
                                inmueble.getCodigo(),
                                inmueble.getCantidad(),
                                inmueble.getPrecio(),
                                inmueble.getArea());
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
    private void imprimirLogo(OutputStream outputStream) throws IOException {
        int logoResourceId = context.getResources().getIdentifier("logoetiqueta", "drawable", context.getPackageName());
        if (logoResourceId == 0) return;

        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), logoResourceId);
        logoBitmap = convertirImagenBlancoNegro(logoBitmap);

        int width = logoBitmap.getWidth();
        int height = logoBitmap.getHeight();
        int maxWidth = 384; //

        // Escalar la imagen si es más grande que el ancho máximo
        if (width > maxWidth) {
            double scaleFactor = (double) maxWidth / width;
            width = maxWidth;
            height = (int) (height * scaleFactor);
            logoBitmap = Bitmap.createScaledBitmap(logoBitmap, width, height, false);
        }

        int widthBytes = (width + 7) / 8;

        // Centrar imagen en la impresora
        outputStream.write(new byte[]{0x1B, 0x61, 0x01});

        // Enviar comando para impresión de gráficos
        outputStream.write(new byte[]{0x1D, 0x76, 0x30, 0x00});
        outputStream.write(new byte[]{(byte) (widthBytes & 0xff), (byte) ((widthBytes >> 8) & 0xff)});
        outputStream.write(new byte[]{(byte) (height & 0xff), (byte) ((height >> 8) & 0xff)});

        byte[] imageData = new byte[widthBytes * height];
        int pos = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x += 8) {
                byte b = 0;
                for (int i = 0; i < 8; i++) {
                    int realX = x + i;
                    if (realX < width) {
                        int pixel = logoBitmap.getPixel(realX, y);
                        int gray = (((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff)) / 3;

                        if (gray > 128) { // Si el pixel es claro, lo hacemos negro
                            b |= (1 << (7 - i));
                        }
                    }
                }
                imageData[pos++] = b;
            }
        }
        // Enviar datos de la imagen a la impresora
        outputStream.write(imageData);
        outputStream.write(new byte[]{0x0A, 0x0A});
    }
    private Bitmap convertirImagenBlancoNegro(Bitmap original) {
        Bitmap newBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int pixel = original.getPixel(x, y);
                int gray = (((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff)) / 3;

                if (gray > 128) {
                    newBitmap.setPixel(x, y, 0xFF000000); // Negro
                } else {
                    newBitmap.setPixel(x, y, 0xFFFFFFFF); // Blanco
                }
            }
        }
        return newBitmap;
    }
    public interface PrinterConnectionCallback {
        void onResult(boolean success, String message);
    }
}