package com.rentalconnects.backend.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.rentalconnects.backend.model.Payment;

/**
 * Service class for generating payment receipts as PDF files using LaTeX.
 */
@Service
public class ReceiptGenerator {

    /**
     * Generating a LaTeX document string representing a payment receipt.
     *
     * @param payment The Payment object containing receipt details.
     * @return String containing the LaTeX code for the receipt.
     */
    public String generateReceipt(Payment payment) {
        String latexContent = """
            \\documentclass[a4paper,12pt]{article}
            \\usepackage[utf8]{inputenc}
            \\usepackage[T1]{fontenc}
            \\usepackage{geometry}
            \\geometry{a4paper, margin=1in}
            \\usepackage{amsmath}
            \\usepackage{booktabs}
            \\usepackage{parskip}
            \\usepackage{fancyhdr}
            \\pagestyle{fancy}
            \\fancyhf{}
            \\lhead{RentalConnects}
            \\rhead{Receipt}
            \\cfoot{Page \\thepage}
            \\usepackage{lastpage}
            \\usepackage{hyperref}
            \\usepackage{noto}

            \\begin{document}

            \\centering
            \\textbf{\\Large Payment Receipt}
            \\vspace{1cm}

            % Generating receipt details for tenant payment.

            \\begin{tabular}{ll}
            \\toprule
            Receipt ID & %s \\\\
            Tenant Name & %s \\\\
            Apartment & %s \\\\
            Amount & $%s \\\\
            Payment Date & %s \\\\
            Status & %s \\\\
            \\bottomrule
            \\end{tabular}

            % Issuing receipt confirmation and timestamp.

            This receipt confirms the payment transaction processed on %s.

            \\end{document}
            """.formatted(
                payment.getId(),
                payment.getName(),
                payment.getApt(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus(),
                LocalDateTime.now()
            );
        return latexContent;
    }

    /**
     * Saving the generated LaTeX content to a file and compiling it into a PDF.
     *
     * @param payment The Payment object for which to generate the receipt.
     * @param outputPath The path where the PDF will be saved.
     * @throws IOException If there is an error writing the file or compiling the LaTeX.
     */
    public void generatePdfReceipt(Payment payment, String outputPath) throws IOException {
        String latexContent = generateReceipt(payment);
        File latexFile = new File("receipt.tex");
        java.nio.file.Files.writeString(latexFile.toPath(), latexContent);

        // Compile LaTeX to PDF using latexmk (assumes latexmk is installed)
        ProcessBuilder pb = new ProcessBuilder("latexmk", "-pdf", "receipt.tex");
        pb.directory(new File("."));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            process.waitFor();
            File pdfFile = new File("receipt.pdf");
            if (pdfFile.exists()) {
                java.nio.file.Files.move(pdfFile.toPath(), new File(outputPath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while compiling LaTeX", e);
        } finally {
            // Clean up temporary files
            new File("receipt.tex").delete();
            new File("receipt.aux").delete();
            new File("receipt.log").delete();
        }
    }
}