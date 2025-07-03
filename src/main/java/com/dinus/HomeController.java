package com.dinus;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HomeController implements Initializable {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblDateTime;

    @FXML
    private Label lblTotalMhs;

    @FXML
    private Label lblTotalMatkul;

    @FXML
    private Label lblTotalJadwal;

    @FXML
    private Label lblTotalKrs;

    @FXML
    private VBox vboxRecentActivity;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateDateTime();
        loadStatistics();
        loadRecentActivity();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - HH:mm");
        lblDateTime.setText("📅 " + now.format(formatter));
    }

    private void loadStatistics() {
        try {
            // Load actual data counts from database
            int totalMhs = AksesDB.getDataMhs() != null ? AksesDB.getDataMhs().size() : 0;
            int totalMatkul = AksesDB.getDataMatakuliah() != null ? AksesDB.getDataMatakuliah().size() : 0;
            int totalJadwal = AksesDB.getDataJadwal() != null ? AksesDB.getDataJadwal().size() : 0;
            int totalKrs = AksesDB.getDataKrs() != null ? AksesDB.getDataKrs().size() : 0;

            lblTotalMhs.setText(String.valueOf(totalMhs));
            lblTotalMatkul.setText(String.valueOf(totalMatkul));
            lblTotalJadwal.setText(String.valueOf(totalJadwal));
            lblTotalKrs.setText(String.valueOf(totalKrs));
        } catch (Exception e) {
            System.out.println("Error loading statistics: " + e.getMessage());
            lblTotalMhs.setText("N/A");
            lblTotalMatkul.setText("N/A");
            lblTotalJadwal.setText("N/A");
            lblTotalKrs.setText("N/A");
        }
    }

    private void loadRecentActivity() {
        vboxRecentActivity.getChildren().clear();
        
        // Add some sample recent activities
        Label activity1 = new Label("✅ Data mahasiswa berhasil diperbarui - 10 menit yang lalu");
        activity1.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px;");
        
        Label activity2 = new Label("📚 Matakuliah baru ditambahkan: Algoritma Pemrograman - 25 menit yang lalu");
        activity2.setStyle("-fx-text-fill: #17a2b8; -fx-font-size: 12px;");
        
        Label activity3 = new Label("🗓️ Jadwal kuliah diperbarui untuk semester ini - 1 jam yang lalu");
        activity3.setStyle("-fx-text-fill: #ffc107; -fx-font-size: 12px;");
        
        vboxRecentActivity.getChildren().addAll(activity1, activity2, activity3);
    }

    @FXML
    void refreshDashboard(ActionEvent event) {
        updateDateTime();
        loadStatistics();
        loadRecentActivity();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard Diperbarui");
        alert.setHeaderText(null);
        alert.setContentText("✅ Dashboard berhasil diperbarui dengan data terbaru!");
        alert.showAndWait();
    }

    @FXML
    void goToMahasiswa(ActionEvent event) {
        // This will be handled by MenuController to switch to Mahasiswa view
        System.out.println("Navigate to Mahasiswa module");
    }

    @FXML
    void goToMatakuliah(ActionEvent event) {
        // This will be handled by MenuController to switch to Matakuliah view
        System.out.println("Navigate to Matakuliah module");
    }

    @FXML
    void goToJadwal(ActionEvent event) {
        // This will be handled by MenuController to switch to Jadwal view
        System.out.println("Navigate to Jadwal module");
    }

    @FXML
    void goToKrs(ActionEvent event) {
        // This will be handled by MenuController to switch to KRS view
        System.out.println("Navigate to KRS module");
    }

    @FXML
    void exportAllData(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Data");
        alert.setHeaderText("Export Semua Data Akademik");
        alert.setContentText(
            "📊 Fitur export akan mengekspor:\n" +
            "• Data Mahasiswa (CSV)\n" +
            "• Data Matakuliah (CSV)\n" +
            "• Data Jadwal (CSV)\n" +
            "• Data KRS (CSV)\n\n" +
            "Fitur ini sedang dalam pengembangan..."
        );
        alert.showAndWait();
    }

    @FXML
    void showReports(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Laporan Akademik");
        alert.setHeaderText("Dashboard Laporan");
        alert.setContentText(
            "📈 Laporan yang tersedia:\n" +
            "• Laporan Mahasiswa per Semester\n" +
            "• Statistik Matakuliah\n" +
            "• Analisis Jadwal Kuliah\n" +
            "• Rekap KRS per Periode\n\n" +
            "Fitur laporan lengkap sedang dalam pengembangan..."
        );
        alert.showAndWait();
    }

    @FXML
    void showHelp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bantuan Sistem");
        alert.setHeaderText("Panduan Penggunaan Sistem KRS");
        alert.setContentText(
            "🆘 Cara menggunakan sistem:\n\n" +
            "1️⃣ MAHASISWA: Kelola data mahasiswa\n" +
            "   • Tambah, edit, hapus data mahasiswa\n" +
            "   • Pencarian dan filter data\n\n" +
            "2️⃣ MATAKULIAH: Kelola kurikulum\n" +
            "   • Master data matakuliah dan SKS\n" +
            "   • Export data matakuliah\n\n" +
            "3️⃣ JADWAL: Atur jadwal kuliah\n" +
            "   • Buat jadwal per kelas\n" +
            "   • Deteksi konflik jadwal\n\n" +
            "4️⃣ KRS: Proses pengambilan matakuliah\n" +
            "   • Pendaftaran KRS mahasiswa\n" +
            "   • Validasi dan approval\n\n" +
            "💡 Tips: Gunakan double-click untuk edit cepat!"
        );
        alert.showAndWait();
    }

    @FXML
    void showSettings(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pengaturan Sistem");
        alert.setHeaderText("Konfigurasi Aplikasi");
        alert.setContentText(
            "⚙️ Pengaturan yang tersedia:\n\n" +
            "🎨 TAMPILAN:\n" +
            "   • Mode gelap/terang\n" +
            "   • Ukuran font\n" +
            "   • Tema warna\n\n" +
            "🔒 KEAMANAN:\n" +
            "   • Password kebijakan\n" +
            "   • Session timeout\n" +
            "   • Audit log\n\n" +
            "💾 DATABASE:\n" +
            "   • Backup otomatis\n" +
            "   • Koneksi database\n" +
            "   • Optimasi performa\n\n" +
            "Fitur pengaturan lengkap segera hadir..."
        );
        alert.showAndWait();
    }

    // Method untuk update welcome message berdasarkan user
    public void setWelcomeMessage(String userName) {
        if (lblWelcome != null) {
            lblWelcome.setText("👋 Selamat Datang, " + userName + "!");
        }
    }

    // Method untuk menambahkan aktivitas baru
    public void addRecentActivity(String activity) {
        if (vboxRecentActivity != null) {
            // Remove oldest if more than 3 activities
            if (vboxRecentActivity.getChildren().size() >= 3) {
                vboxRecentActivity.getChildren().remove(2);
            }
            
            // Add new activity at the top
            Label newActivity = new Label("✅ " + activity + " - Baru saja");
            newActivity.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px;");
            vboxRecentActivity.getChildren().add(0, newActivity);
        }
    }
}