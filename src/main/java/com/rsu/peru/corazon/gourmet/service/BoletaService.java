package com.rsu.peru.corazon.gourmet.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.rsu.peru.corazon.gourmet.model.PedidoItem;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BoletaService {

    private List<PedidoItem> carrito;
    private double total;
    private String clienteNombre;
    private String clienteDni;

    public BoletaService(List<PedidoItem> carrito, double total, String clienteNombre, String clienteDni) {
        this.carrito = carrito;
        this.total = total;
        this.clienteNombre = clienteNombre;
        this.clienteDni = clienteDni;
    }

    public void export(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A6, 20, 20, 20, 20);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

        Paragraph title = new Paragraph("CORAZÓN GOURMET RSU", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph sub = new Paragraph("Sede Santa Anita - UTP\nTicket de Venta", fontBody);
        sub.setAlignment(Element.ALIGN_CENTER);
        document.add(sub);

        document.add(new Paragraph("\n---------------------------------------------------------"));

        document.add(new Paragraph("CLIENTE: " + clienteNombre.toUpperCase(), fontBold));
        document.add(new Paragraph("DNI: " + clienteDni, fontBody));
        document.add(new Paragraph("FECHA: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontBody));

        document.add(new Paragraph("---------------------------------------------------------\n"));

        PdfPTable table = new PdfPTable(new float[]{3f, 1f});
        table.setWidthPercentage(100);

        PdfPCell h1 = new PdfPCell(new Phrase("DESCRIPCIÓN", fontSubtitle));
        h1.setBorder(Rectangle.BOTTOM);
        h1.setPaddingBottom(5);
        table.addCell(h1);

        PdfPCell h2 = new PdfPCell(new Phrase("PRECIO", fontSubtitle));
        h2.setBorder(Rectangle.BOTTOM);
        h2.setPaddingBottom(5);
        h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(h2);

        for (PedidoItem item : carrito) {
            String desc = item.getNombre() + (item.isEsConadis() ? " (CONADIS)" : "");
            PdfPCell cellDetalle = new PdfPCell(new Phrase(desc, fontBody));
            cellDetalle.setBorder(Rectangle.NO_BORDER);
            cellDetalle.setPaddingTop(5);
            table.addCell(cellDetalle);

            double precioItem = item.isEsConadis() ? item.getPrecioConadis() : item.getPrecioNormal();
            PdfPCell cellMonto = new PdfPCell(new Phrase("S/ " + String.format("%.2f", precioItem), fontBody));
            cellMonto.setBorder(Rectangle.NO_BORDER);
            cellMonto.setPaddingTop(5);
            cellMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cellMonto);
        }

        document.add(table);

        document.add(new Paragraph("\n---------------------------------------------------------"));

        Paragraph pTotal = new Paragraph("TOTAL PAGADO: S/ " + String.format("%.2f", total), fontTotal);
        pTotal.setAlignment(Element.ALIGN_RIGHT);
        document.add(pTotal);

        Paragraph footer = new Paragraph("\n¡Gracias por su consumo!", fontBody);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }
}