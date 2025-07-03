package com.dinus;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MhsController implements Initializable {
    ObservableList<Mhs> listMhs;
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
    private TextField tfAlamat;

    @FXML
    private TextField tfNama;

    @FXML
    private TextField tfNim;

    @FXML
    private TextField tfCariNama;

    @FXML
    private TableColumn<Mhs, String> alamat;

    @FXML
    private TableColumn<Mhs, String> nama;

    @FXML
    private TableColumn<Mhs, String> nim;

    @FXML
    private TableView<Mhs> tvMhs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listMhs = FXCollections.observableArrayList();
        initTabel();
        loadData();
        setFilter();
        buttonAktif(false);
        teksAktif(false);
        flagEdit = false;
        
        System.out.println("MhsController initialized successfully");
    }

    @FXML
    void add(ActionEvent event) {
        flagEdit = false;
        teksAktif(true);
        buttonAktif(true);
        clearTeks();
        tfNim.requestFocus();
        updateStatus("Mode tambah data aktif", false);
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
        int selectedIndex = tvMhs.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan dihapus!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data mahasiswa?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.YES) {
            Mhs selectedMhs = tvMhs.getItems().get(selectedIndex);
            AksesDB.delDataMhs(selectedMhs.getNim());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data mahasiswa berhasil dihapus!");
            loadData();
            updateStatus("Data berhasil dihapus", true);
        }
    }

    @FXML
    void edit(ActionEvent event) {
        int selectedIndex = tvMhs.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan diedit!");
            return;
        }

        buttonAktif(true);
        teksAktif(true);
        flagEdit = true;
        
        Mhs selectedMhs = tvMhs.getItems().get(selectedIndex);
        tfNim.setText(selectedMhs.getNim());
        tfNama.setText(selectedMhs.getNama());
        tfAlamat.setText(selectedMhs.getAlamat());
        tfNim.requestFocus();
        updateStatus("Mode edit data aktif", false);
    }

    @FXML
    void update(ActionEvent event) {
        String nim = tfNim.getText().trim();
        String nama = tfNama.getText().trim();
        String alamat = tfAlamat.getText().trim();

        if (nim.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Semua field harus diisi!");
            return;
        }

        try {
            if (flagEdit == false) {
                AksesDB.addDataMhs(nim, nama, alamat);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data mahasiswa berhasil ditambahkan!");
                updateStatus("Data berhasil ditambahkan", true);
            } else {
                int idx = tvMhs.getSelectionModel().getSelectedIndex();
                String nimLama = tvMhs.getItems().get(idx).getNim();
                AksesDB.updateDataMhs(nim, nama, nimLama, alamat);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data mahasiswa berhasil diperbarui!");
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

    // Optional enhanced methods - akan berfungsi jika ada di FXML
    @FXML
    void exportToCSV(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Export", "Fitur export CSV dalam pengembangan...");
    }

    @FXML
    void showStatistics(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Statistik", 
                 "Total Mahasiswa: " + (listMhs != null ? listMhs.size() : 0));
    }

    @FXML
    void refreshData(ActionEvent event) {
        loadData();
        updateStatus("Data berhasil di-refresh", true);
    }

    @FXML
    void showHelp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Bantuan", 
                 "Cara penggunaan:\n- Klik Tambah untuk menambah data\n- Pilih data lalu klik Edit untuk mengubah\n- Pilih data lalu klik Hapus untuk menghapus");
    }

    @FXML
    void showAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Tentang", "Sistem KRS UDINUS v2.0");
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
        tfNim.setEditable(aktif);
        tfNama.setEditable(aktif);
        tfAlamat.setEditable(aktif);
    }

    public void clearTeks() {
        tfNim.setText("");
        tfNama.setText("");
        tfAlamat.setText("");
    }

    void initTabel() {
        nim.setCellValueFactory(new PropertyValueFactory<Mhs, String>("nim"));
        nama.setCellValueFactory(new PropertyValueFactory<Mhs, String>("nama"));
        alamat.setCellValueFactory(new PropertyValueFactory<Mhs, String>("alamat"));
    }

    void loadData() {
        listMhs = AksesDB.getDataMhs();
        if (listMhs != null) {
            tvMhs.setItems(listMhs);
            setFilter();
        }
    }

    void setFilter() {
        if (listMhs == null || tfCariNama == null) return;
        
        FilteredList<Mhs> filterData = new FilteredList<>(listMhs, b -> true);
        tfCariNama.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(mhs -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                return mhs.getNama().toLowerCase().indexOf(searchKeyword) > -1 ||
                       mhs.getNim().toLowerCase().indexOf(searchKeyword) > -1 ||
                       mhs.getAlamat().toLowerCase().indexOf(searchKeyword) > -1;
            });
        });
        
        SortedList<Mhs> sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tvMhs.comparatorProperty());
        tvMhs.setItems(sortedData);
    }
}