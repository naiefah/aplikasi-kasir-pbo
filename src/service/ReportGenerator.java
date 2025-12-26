package service;

import config.DatabaseConnection;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    public static void generatePdf(Date startDate, Date endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Document document = new Document();
            PdfWriter.getInstance(
                    document,
                    new FileOutputStream("laporan_penjualan.pdf")
            );

            document.open();
            document.add(new Paragraph("LAPORAN PENJUALAN"));
            document.add(new Paragraph(
                    "Periode: " + sdf.format(startDate) +
                    " s/d " + sdf.format(endDate)
            ));
            document.add(new Paragraph(" "));

            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE tanggal BETWEEN ? AND ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sdf.format(startDate));
            ps.setString(2, sdf.format(endDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                document.add(new Paragraph(
                        "ID Order : " + rs.getInt("id_order") +
                        " | Tanggal : " + rs.getDate("tanggal") +
                        " | Total : " + rs.getDouble("total")
                ));
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
