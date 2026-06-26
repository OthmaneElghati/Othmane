package com.humanitaire.backend.controller;

import com.humanitaire.backend.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfService pdfService;

    @GetMapping("/generate/{type}")
    public ResponseEntity<byte[]> generateReport(@PathVariable String type) {
        byte[] pdf;
        String filename;
        switch (type) {
            case "missions":
                pdf = pdfService.generateMissionReport();
                filename = "rapport-missions.pdf";
                break;
            case "volunteers":
                pdf = pdfService.generateVolunteerReport();
                filename = "rapport-benevoles.pdf";
                break;
            case "donations":
                pdf = pdfService.generateDonationReport();
                filename = "rapport-donations.pdf";
                break;
            case "monthly":
                pdf = pdfService.generateMonthlyReport();
                filename = "rapport-mensuel.pdf";
                break;
            case "beneficiaries":
                pdf = pdfService.generateMonthlyReport();
                filename = "rapport-beneficiaires.pdf";
                break;
            case "convoys":
                pdf = pdfService.generateMonthlyReport();
                filename = "rapport-convois.pdf";
                break;
            default:
                pdf = pdfService.generateMonthlyReport();
                filename = "rapport-general.pdf";
                break;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
