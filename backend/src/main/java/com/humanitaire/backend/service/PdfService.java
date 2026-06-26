package com.humanitaire.backend.service;

import com.humanitaire.backend.entity.*;
import com.humanitaire.backend.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final MissionRepository missionRepository;
    private final VolunteerRepository volunteerRepository;
    private final DonationRepository donationRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(41, 128, 185));
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(44, 62, 80));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color LIGHT_BG = new Color(245, 245, 245);

    public byte[] generateMissionReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        addTitle(document, "Rapport des Missions Humanitaires");
        addSubtitle(document, "Plateforme Humanitaire du Maroc - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        List<Mission> missions = missionRepository.findAll();

        addParagraph(document, "Total des missions: " + missions.size());
        long active = missions.stream().filter(m -> m.getStatus() == Mission.MissionStatus.ACTIVE).count();
        long completed = missions.stream().filter(m -> m.getStatus() == Mission.MissionStatus.COMPLETED).count();
        addParagraph(document, "Missions actives: " + active + " | Missions terminees: " + completed);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 2, 1.5f, 1.5f, 2});
        addHeaderCell(table, "Titre");
        addHeaderCell(table, "Ville");
        addHeaderCell(table, "Region");
        addHeaderCell(table, "Statut");
        addHeaderCell(table, "Priorite");
        addHeaderCell(table, "Budget (MAD)");

        boolean alternate = false;
        for (Mission m : missions) {
            Color bg = alternate ? LIGHT_BG : Color.WHITE;
            addBodyCell(table, m.getTitle(), bg);
            addBodyCell(table, m.getCity(), bg);
            addBodyCell(table, m.getRegion(), bg);
            addBodyCell(table, m.getStatus() != null ? m.getStatus().name() : "", bg);
            addBodyCell(table, m.getPriority() != null ? m.getPriority().name() : "", bg);
            addBodyCell(table, m.getBudget() != null ? String.format("%.0f", m.getBudget()) : "", bg);
            alternate = !alternate;
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] generateVolunteerReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        addTitle(document, "Rapport des Benevoles");
        addSubtitle(document, "Plateforme Humanitaire du Maroc - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        List<Volunteer> volunteers = volunteerRepository.findAll();
        addParagraph(document, "Total des benevoles: " + volunteers.size());
        long available = volunteers.stream().filter(Volunteer::isAvailable).count();
        addParagraph(document, "Benevoles disponibles: " + available);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 2, 2, 2.5f, 1.5f});
        addHeaderCell(table, "Nom");
        addHeaderCell(table, "Ville");
        addHeaderCell(table, "Region");
        addHeaderCell(table, "Competences");
        addHeaderCell(table, "Disponible");

        boolean alternate = false;
        for (Volunteer v : volunteers) {
            Color bg = alternate ? LIGHT_BG : Color.WHITE;
            addBodyCell(table, v.getFullName(), bg);
            addBodyCell(table, v.getCity(), bg);
            addBodyCell(table, v.getRegion(), bg);
            addBodyCell(table, v.getSkills() != null ? v.getSkills() : "", bg);
            addBodyCell(table, v.isAvailable() ? "Oui" : "Non", bg);
            alternate = !alternate;
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] generateDonationReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        addTitle(document, "Rapport des Donations");
        addSubtitle(document, "Plateforme Humanitaire du Maroc - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        List<Donation> donations = donationRepository.findAll();
        double total = donations.stream().filter(d -> d.getStatus() == Donation.DonationStatus.COMPLETED).mapToDouble(Donation::getAmount).sum();
        addParagraph(document, "Total des donations: " + donations.size());
        addParagraph(document, "Montant total: " + String.format("%.2f", total) + " MAD");
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 2, 1.5f, 1.5f, 2});
        addHeaderCell(table, "Donateur");
        addHeaderCell(table, "Montant (MAD)");
        addHeaderCell(table, "Methode");
        addHeaderCell(table, "Statut");
        addHeaderCell(table, "Transaction");

        boolean alternate = false;
        for (Donation d : donations) {
            Color bg = alternate ? LIGHT_BG : Color.WHITE;
            addBodyCell(table, d.getDonorName(), bg);
            addBodyCell(table, String.format("%.2f", d.getAmount()), bg);
            addBodyCell(table, d.getPaymentMethod() != null ? d.getPaymentMethod().name() : "", bg);
            addBodyCell(table, d.getStatus() != null ? d.getStatus().name() : "", bg);
            addBodyCell(table, d.getTransactionId() != null ? d.getTransactionId() : "", bg);
            alternate = !alternate;
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }

    public byte[] generateMonthlyReport() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        addTitle(document, "Rapport Mensuel");
        addSubtitle(document, "Plateforme Humanitaire du Maroc - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        document.add(Chunk.NEWLINE);

        addSubtitle(document, "Resume General");
        addParagraph(document, "Missions: " + missionRepository.count());
        addParagraph(document, "Benevoles: " + volunteerRepository.count());
        addParagraph(document, "Beneficiaires: " + beneficiaryRepository.count());
        Double totalDonations = donationRepository.sumCompletedAmount();
        addParagraph(document, "Donations totales: " + String.format("%.2f", totalDonations != null ? totalDonations : 0.0) + " MAD");
        document.add(Chunk.NEWLINE);

        addSubtitle(document, "Missions par Statut");
        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setWidthPercentage(50);
        addHeaderCell(statusTable, "Statut");
        addHeaderCell(statusTable, "Nombre");
        for (Mission.MissionStatus status : Mission.MissionStatus.values()) {
            long count = missionRepository.countByStatus(status);
            addBodyCell(statusTable, status.name(), Color.WHITE);
            addBodyCell(statusTable, String.valueOf(count), Color.WHITE);
        }
        document.add(statusTable);
        document.add(Chunk.NEWLINE);

        addSubtitle(document, "Missions par Region");
        PdfPTable regionTable = new PdfPTable(2);
        regionTable.setWidthPercentage(50);
        addHeaderCell(regionTable, "Region");
        addHeaderCell(regionTable, "Nombre");
        missionRepository.countByRegion().forEach(row -> {
            addBodyCell(regionTable, row[0] != null ? row[0].toString() : "N/A", Color.WHITE);
            addBodyCell(regionTable, row[1].toString(), Color.WHITE);
        });
        document.add(regionTable);

        document.close();
        return out.toByteArray();
    }

    private void addTitle(Document doc, String text) {
        try {
            Paragraph p = new Paragraph(text, TITLE_FONT);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(8);
            doc.add(p);
        } catch (DocumentException ignored) {}
    }

    private void addSubtitle(Document doc, String text) {
        try {
            Paragraph p = new Paragraph(text, SUBTITLE_FONT);
            p.setSpacingBefore(10);
            p.setSpacingAfter(6);
            doc.add(p);
        } catch (DocumentException ignored) {}
    }

    private void addParagraph(Document doc, String text) {
        try {
            doc.add(new Paragraph(text, BODY_FONT));
        } catch (DocumentException ignored) {}
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", BODY_FONT));
        cell.setBackgroundColor(bg);
        cell.setPadding(4);
        table.addCell(cell);
    }
}
