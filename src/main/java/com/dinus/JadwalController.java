package com.dinus;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

public class JadwalController implements Initializable {
    ObservableList<Jadwal> listJadwal;
    boolean flagEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private TextField tfCari;
   
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnPilih;

    @FXML
    private Button btnPilihDosen;

    @FXML
    private Label lblErr;

    @FXML
    private TableColumn<Jadwal, String> hari;

    @FXML
    private TableColumn<Jadwal, String> jam;

    @FXML
    private TableColumn<Jadwal, String> kelas;

    @FXML
    private TableColumn<Jadwal, String> kodeMk;

    @FXML
    private TableColumn<Jadwal, String> namaMk;

    @FXML
    private TableColumn<Jadwal, String> ruang;

    @FXML
    private TableColumn<Jadwal, String> namaDsn;

    @FXML
    private TableView<Jadwal> tbJadwal;

    @FXML
    private TextField tfHari;

    @FXML
    private TextField tfJam;

    @FXML
    private TextField tfKelas;

    @FXML
    private TextField tfKodematkul;

    @FXML
    private TextField tfNmMatkul;

    @FXML
    private TextField tfRuang;

    @FXML
    private TextField tfKodeDosen;

    @FXML
    private TextField tfNmDosen;
    
    @FXML
    void pilihMatkul(ActionEvent event) {
        Stage stage = new Stage();
        Parent root;       
        try {
            root = FXMLLoader.load(MatkulSearchController.class.getResource("fmatkulSearch.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Daftar Matakuliah");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.showAndWait();         
            Matakuliah m;
            m = (Matakuliah) stage.getUserData();            
            if (m != null) {
                tfKodematkul.setText(m.getKodeMk());
                tfNmMatkul.setText(m.getNamaMk());
            }
        } catch (IOException ex) {
            Logger.getLogger(JadwalController.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }

    @FXML
    void pilihDosen(ActionEvent event) {
        Stage stage = new Stage();
        Parent root;       
        try {
            root = FXMLLoader.load(DosenSearchController.class.getResource("fdosenSearch.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Daftar Dosen");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.showAndWait();         
            Dosen d;
            d = (Dosen) stage.getUserData();            
            if (d != null) {
                tfKodeDosen.setText(d.getKodeDsn());
                tfNmDosen.setText(d.getNamaDsn());
            }
        } catch (IOException ex) {
            Logger.getLogger(JadwalController.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }
    
    @FXML
    void add(ActionEvent event) {
        flagEdit = false;
        teksAktif(true);
        buttonAktif(true);
        clearTeks();
        tfKodematkul.requestFocus();
        updateStatus("Mode tambah jadwal aktif", false);
    }

    @FXML
    void cancel(ActionEvent event) {
        teksAktif(false);
        clearTeks();
        buttonAktif(false);
        updateStatus("Operasi dibatalkan", false);
    }

    @FXML
    void delete(ActionEvent event) {
        int selectedIndex = tbJadwal.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan dihapus!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data jadwal?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Jadwal selectedJadwal = tbJadwal.getItems().get(selectedIndex);
            AksesDB.delDataJadwal(selectedJadwal.getKodeMk(), selectedJadwal.getKelas());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data jadwal berhasil dihapus!");
            loadData();
            updateStatus("Data berhasil dihapus", true);
        }
    }

    @FXML
    void edit(ActionEvent event) {
        int selectedIndex = tbJadwal.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan diedit!");
            return;
        }

        buttonAktif(true);
        teksAktif(true);
        flagEdit = true;
        
        Jadwal selectedJadwal = tbJadwal.getItems().get(selectedIndex);
        tfKodematkul.setText(selectedJadwal.getKodeMk());
        tfKelas.setText(selectedJadwal.getKelas());
        tfNmMatkul.setText(selectedJadwal.getNamaMk());        
        tfHari.setText(selectedJadwal.getHari());
        tfJam.setText(selectedJadwal.getJam());
        tfRuang.setText(selectedJadwal.getRuang());
        tfKodeDosen.setText(selectedJadwal.getKodeDsn());
        tfNmDosen.setText(selectedJadwal.getNamaDsn());
        tfKodematkul.requestFocus();
        updateStatus("Mode edit jadwal aktif", false);
    }

    @FXML
    void update(ActionEvent event) {
        String vKelas = tfKelas.getText().trim();
        String vKodeMk = tfKodematkul.getText().trim();
        String vHari = tfHari.getText().trim();
        String vJam = tfJam.getText().trim();
        String vRuang = tfRuang.getText().trim();
        String vKodeDsn = tfKodeDosen.getText().trim();

        if (vKelas.isEmpty() || vKodeMk.isEmpty() || vHari.isEmpty() || 
            vJam.isEmpty() || vRuang.isEmpty() || vKodeDsn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Semua field harus diisi!");
            return;
        }

        try {
            if (flagEdit == false) {
                AksesDB.addDataJadwal(vKodeMk, vKelas, vHari, vJam, vRuang, vKodeDsn);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data jadwal berhasil ditambahkan!");
                updateStatus("Data berhasil ditambahkan", true);
            } else {
                int idx = tbJadwal.getSelectionModel().getSelectedIndex();
                String kelasLama = tbJadwal.getItems().get(idx).getKelas();
                AksesDB.updateDataJadwal(vKodeMk, vKelas, vHari, vJam, vRuang, vKodeDsn);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data jadwal berhasil diperbarui!");
                updateStatus("Data berhasil diperbarui", true);
            }
            
            loadData();
            flagEdit = false;
            teksAktif(false);
            clearTeks();
            buttonAktif(false);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan data: " + e.getMessage());
        }
    }

    // Optional enhanced methods
    @FXML
    void exportToCSV(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Export", "Fitur export CSV dalam pengembangan...");
    }

    @FXML
    void showStatistics(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Statistik", 
                 "Total Jadwal: " + (listJadwal != null ? listJadwal.size() : 0));
    }

    @FXML
    void refreshData(ActionEvent event) {
        loadData();
        updateStatus("Data berhasil di-refresh", true);
    }

    @FXML
    void showHelp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Bantuan", 
                 "Cara penggunaan:\n- Klik Tambah untuk menambah jadwal\n- Pilih matakuliah dan dosen menggunakan tombol pencarian\n- Isi semua field yang diperlukan");
    }

    @FXML
    void showAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Tentang", "Modul Jadwal - Sistem KRS UDINUS v2.0");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateStatus(String message, boolean isSuccess) {
        if (lblErr != null) {
            lblErr.setText(message);
            lblErr.setStyle(isSuccess ? 
                "-fx-text-fill: #10b981; -fx-font-weight: bold;" : 
                "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }
    
    public void buttonAktif(boolean nonAktif) {
        btnAdd.setDisable(nonAktif);
        btnEdit.setDisable(nonAktif);
        btnDelete.setDisable(nonAktif);
        btnUpdate.setDisable(!nonAktif);
        btnCancel.setDisable(!nonAktif);
    }     
    
    public void teksAktif(boolean aktif) {
        tfKelas.setEditable(aktif);
        tfKodematkul.setEditable(false); // Read-only, diisi melalui dialog
        tfNmMatkul.setEditable(false);   // Read-only
        tfHari.setEditable(aktif);
        tfJam.setEditable(aktif);
        tfRuang.setEditable(aktif);
        tfKodeDosen.setEditable(false);  // Read-only, diisi melalui dialog
        tfNmDosen.setEditable(false);    // Read-only
        btnPilih.setDisable(!aktif);
        btnPilihDosen.setDisable(!aktif);
    }
    
    public void clearTeks() {
        tfKelas.setText("");
        tfKodematkul.setText("");
        tfNmMatkul.setText("");
        tfHari.setText("");
        tfJam.setText("");
        tfRuang.setText("");
        tfKodeDosen.setText("");
        tfNmDosen.setText("");
    }   
    
    void initTabel() {
        kelas.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("kelas"));
        kodeMk.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("kodeMk"));
        namaMk.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("namaMk"));
        hari.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("hari"));
        jam.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("jam"));
        ruang.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("ruang"));
        namaDsn.setCellValueFactory(new PropertyValueFactory<Jadwal, String>("namaDsn"));
    } 
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listJadwal = FXCollections.observableArrayList();        
        initTabel();
        loadData();
        setFilter();
        buttonAktif(false);
        teksAktif(false);
        flagEdit = false;
        
        System.out.println("JadwalController initialized successfully");
    }      
    
    void loadData() {
        listJadwal = AksesDB.getDataJadwal();
        if (listJadwal != null) {
            tbJadwal.setItems(listJadwal);
            setFilter();
        }
    }      
    
    void setFilter() {
        if (listJadwal == null || tfCari == null) return;
        
        FilteredList<Jadwal> filterData = new FilteredList<>(listJadwal, b -> true);
        tfCari.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(jadwal -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                return jadwal.getNamaMk().toLowerCase().indexOf(searchKeyword) > -1 ||
                       jadwal.getKodeMk().toLowerCase().indexOf(searchKeyword) > -1 ||
                       jadwal.getKelas().toLowerCase().indexOf(searchKeyword) > -1 ||
                       jadwal.getHari().toLowerCase().indexOf(searchKeyword) > -1 ||
                       jadwal.getRuang().toLowerCase().indexOf(searchKeyword) > -1 ||
                       jadwal.getNamaDsn().toLowerCase().indexOf(searchKeyword) > -1;
            });           
        });
        
        SortedList<Jadwal> sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tbJadwal.comparatorProperty());
        tbJadwal.setItems(sortedData);
    }
}