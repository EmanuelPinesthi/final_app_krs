package com.dinus;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DosenController implements Initializable {
    ObservableList<Dosen> listDosen;
    boolean flagEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDel;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label lblErr;

    @FXML
    private TextField tfKodeDsn;

    @FXML
    private TextField tfNamaDsn;

    @FXML
    private TextField tfCariNama;

    @FXML
    private TableColumn<Dosen, String> kodeDsn;

    @FXML
    private TableColumn<Dosen, String> namaDsn;

    @FXML
    private TableView<Dosen> tvDosen;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listDosen = FXCollections.observableArrayList();        
        initTabel();
        loadData();
        setFilter();
        buttonAktif(false);
        teksAktif(false);
        flagEdit = false;
        
        System.out.println("DosenController initialized successfully");
    } 

    @FXML
    void add(ActionEvent event) {
        flagEdit = false;
        teksAktif(true);
        buttonAktif(true);
        clearTeks();
        tfKodeDsn.requestFocus();
        updateStatus("Mode tambah dosen aktif", false);
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
        int selectedIndex = tvDosen.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan dihapus!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data dosen?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Dosen selectedDosen = tvDosen.getItems().get(selectedIndex);
            AksesDB.delDataDosen(selectedDosen.getKodeDsn());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data dosen berhasil dihapus!");
            loadData();
            updateStatus("Data berhasil dihapus", true);
        }
    }

    @FXML
    void edit(ActionEvent event) {
        int selectedIndex = tvDosen.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan diedit!");
            return;
        }

        buttonAktif(true);
        teksAktif(true);
        flagEdit = true;
        
        Dosen selectedDosen = tvDosen.getItems().get(selectedIndex);
        tfKodeDsn.setText(selectedDosen.getKodeDsn());
        tfNamaDsn.setText(selectedDosen.getNamaDsn());
        tfKodeDsn.requestFocus();
        updateStatus("Mode edit dosen aktif", false);
    }

    @FXML
    void update(ActionEvent event) {
        String kodeDsn = tfKodeDsn.getText().trim();
        String namaDsn = tfNamaDsn.getText().trim();

        if (kodeDsn.isEmpty() || namaDsn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Semua field harus diisi!");
            return;
        }

        try {
            if (flagEdit == false) {
                AksesDB.addDataDosen(kodeDsn, namaDsn);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data dosen berhasil ditambahkan!");
                updateStatus("Data berhasil ditambahkan", true);
            } else {
                int idx = tvDosen.getSelectionModel().getSelectedIndex();
                String kodeDsnLama = tvDosen.getItems().get(idx).getKodeDsn();
                AksesDB.updateDataDosen(kodeDsn, namaDsn, kodeDsnLama);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data dosen berhasil diperbarui!");
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
                 "Total Dosen: " + (listDosen != null ? listDosen.size() : 0));
    }

    @FXML
    void refreshData(ActionEvent event) {
        loadData();
        updateStatus("Data berhasil di-refresh", true);
    }

    @FXML
    void showHelp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Bantuan", 
                 "Cara penggunaan:\n- Klik Tambah untuk menambah dosen\n- Pilih data lalu klik Edit untuk mengubah\n- Pilih data lalu klik Hapus untuk menghapus");
    }

    @FXML
    void showAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Tentang", "Modul Dosen - Sistem KRS UDINUS v2.0");
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
        btnDel.setDisable(nonAktif);
        btnUpdate.setDisable(!nonAktif);
        btnCancel.setDisable(!nonAktif);
    }

    public void teksAktif(boolean aktif) {
        tfKodeDsn.setEditable(aktif);
        tfNamaDsn.setEditable(aktif);
    }

    public void clearTeks() {
        tfKodeDsn.setText("");
        tfNamaDsn.setText("");
    }

    void initTabel() {
        kodeDsn.setCellValueFactory(new PropertyValueFactory<Dosen, String>("kodeDsn"));
        namaDsn.setCellValueFactory(new PropertyValueFactory<Dosen, String>("namaDsn"));
    }

    void loadData() {
        listDosen = AksesDB.getDataDosen();
        if (listDosen != null) {
            tvDosen.setItems(listDosen);
            setFilter();
        }
    }

    void setFilter() {
        if (listDosen == null || tfCariNama == null) return;
        
        FilteredList<Dosen> filterData = new FilteredList<>(listDosen, b -> true);
        tfCariNama.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(dosen -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                return dosen.getNamaDsn().toLowerCase().indexOf(searchKeyword) > -1 ||
                       dosen.getKodeDsn().toLowerCase().indexOf(searchKeyword) > -1;
            });           
        });
        
        SortedList<Dosen> sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tvDosen.comparatorProperty());
        tvDosen.setItems(sortedData);
    }
}