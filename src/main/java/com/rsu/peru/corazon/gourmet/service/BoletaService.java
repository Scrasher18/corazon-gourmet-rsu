package com.rsu.peru.corazon.gourmet.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rsu.peru.corazon.gourmet.model.Pedido;
import com.rsu.peru.corazon.gourmet.model.DetallePedido;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class BoletaService {

    public void export(HttpServletResponse response, Pedido pedido) throws IOException {
        Document document = new Document(PageSize.A6, 15, 15, 15, 15);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fuenteBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 8);

        Paragraph empresa = new Paragraph("CORAZÓN GOURMET", fuenteTitulo);
        empresa.setAlignment(Element.ALIGN_CENTER);
        document.add(empresa);

        Paragraph ruc = new Paragraph("RUC: 20123456789\nCalle Los Ruiseñores 123 - Santa Anita", fuenteSubtitulo);
        ruc.setAlignment(Element.ALIGN_CENTER);
        document.add(ruc);
        
        document.add(new Paragraph("--------------------------------------------------", fuenteSubtitulo));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        document.add(new Paragraph("BOLETA ELECTRÓNICA: T001-" + pedido.getId(), fuenteBold));
        document.add(new Paragraph("Fecha: " + pedido.getFecha().format(formatter), fuenteNormal));
        
        // ¡Añadido! Número de mesa para control de sala
        document.add(new Paragraph("Mesa N°: " + pedido.getMesa(), fuenteBold));
    
        if (pedido.getUsuario() != null) {
            document.add(new Paragraph("Atendido por: " + pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(), fuenteNormal));
        }
        
        if (pedido.getNombreCliente() != null && !pedido.getNombreCliente().isEmpty()) {
            document.add(new Paragraph("Cliente: " + pedido.getNombreCliente(), fuenteNormal));
            document.add(new Paragraph("DNI: " + pedido.getDniCliente(), fuenteNormal));
        } else {
            document.add(new Paragraph("Cliente: PÚBLICO GENERAL", fuenteNormal));
        }
        
        if (pedido.getEsConadis() != null && pedido.getEsConadis()) {
            document.add(new Paragraph("Tipo de Tarifa: CONADIS (Descuento Aplicado)", fuenteBold));
        }

        document.add(new Paragraph("--------------------------------------------------", fuenteSubtitulo));

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{45f, 15f, 20f, 20f}); 

        tabla.addCell(crearCelda("Producto", fuenteBold, Element.ALIGN_LEFT, false));
        tabla.addCell(crearCelda("Cant.", fuenteBold, Element.ALIGN_CENTER, false));
        tabla.addCell(crearCelda("P. Unit", fuenteBold, Element.ALIGN_RIGHT, false));
        tabla.addCell(crearCelda("Subt.", fuenteBold, Element.ALIGN_RIGHT, false));

        for (DetallePedido detalle : pedido.getDetalles()) {
            String nombreMostrar = detalle.getMenu().getNombreItem();
            if (detalle.getEntradaSeleccionada() != null && !detalle.getEntradaSeleccionada().isEmpty() 
                && detalle.getBebidaSeleccionada() != null && !detalle.getBebidaSeleccionada().isEmpty()) {
                nombreMostrar += " (" + detalle.getEntradaSeleccionada() + " / " + detalle.getBebidaSeleccionada() + ")";
            }

            tabla.addCell(crearCelda(nombreMostrar, fuenteNormal, Element.ALIGN_LEFT, true));
            tabla.addCell(crearCelda(String.valueOf(detalle.getCantidad()), fuenteNormal, Element.ALIGN_CENTER, true));
            tabla.addCell(crearCelda("S/ " + String.format("%.2f", detalle.getPrecioUnitario()), fuenteNormal, Element.ALIGN_RIGHT, true));
            tabla.addCell(crearCelda("S/ " + String.format("%.2f", detalle.getSubtotal()), fuenteNormal, Element.ALIGN_RIGHT, true));
        }

        document.add(tabla);
        document.add(new Paragraph("--------------------------------------------------", fuenteSubtitulo));

        if (pedido.getMetodoPago() != null) {
            Paragraph pago = new Paragraph("Forma de Pago: " + pedido.getMetodoPago(), fuenteNormal);
            pago.setAlignment(Element.ALIGN_LEFT);
            document.add(pago);
        }

        Paragraph total = new Paragraph("TOTAL A PAGAR: S/ " + String.format("%.2f", pedido.getMontoTotal()), fuenteTitulo);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.add(new Chunk("\n"));
        Paragraph agradecimiento = new Paragraph("¡Gracias por su preferencia!\nCorazón Gourmet - RSU UTP", fuenteSubtitulo);
        agradecimiento.setAlignment(Element.ALIGN_CENTER);
        document.add(agradecimiento);

        document.close();
    }

    private PdfPCell crearCelda(String texto, Font fuente, int alineacion, boolean quitarBordes) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(3);
        if (quitarBordes) {
            celda.setBorder(PdfPCell.NO_BORDER);
        }
        return celda;
    }
}