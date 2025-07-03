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

public class MatkulController implements Initializable {
    ObservableList<Matakuliah> listMatakuliah;
    boolean flagEdit;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label lblErr;

    @FXML
    private TextField tfKodeMk;

    @FXML
    private TextField tfNamaMk;

    @FXML
    private TextField tfSks;

    @FXML
    private TextField tfCariMatkul;

    @FXML
    private TableColumn<Matakuliah, String> kodeMk;

    @FXML
    private TableColumn<Matakuliah, String> namaMk;

    @FXML
    private TableColumn<Matakuliah, Integer> sks;

    @FXML
    private TableView<Matakuliah> tvMatakuliah;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listMatakuliah = FXCollections.observableArrayList();
        initTabel();
        loadData();
        setFilter();
        buttonAktif(false);
        teksAktif(false);
        flagEdit = false;
        
        System.out.println("MatkulController initialized successfully");
    }

    @FXML
    void add(ActionEvent event) {
        flagEdit = false;
        teksAktif(true);
        buttonAktif(true);
        clearTeks();
        tfKodeMk.requestFocus();
        updateStatus("Mode tambah matakuliah aktif", false);
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
        int selectedIndex = tvMatakuliah.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan dihapus!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data matakuliah?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        
        if (alert.getResult() == ButtonType.YES) {
            Matakuliah selectedMatkul = tvMatakuliah.getItems().get(selectedIndex);
            AksesDB.delDataMatakuliah(selectedMatkul.getKodeMk());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data matakuliah berhasil dihapus!");
            loadData();
            updateStatus("Data berhasil dihapus", true);
        }
    }

    @FXML
    void edit(ActionEvent event) {
        int selectedIndex = tvMatakuliah.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan diedit!");
            return;
        }

        buttonAktif(true);
        teksAktif(true);
        flagEdit = true;
        
        Matakuliah selectedMatkul = tvMatakuliah.getItems().get(selectedIndex);
        tfKodeMk.setText(selectedMatkul.getKodeMk());
        tfNamaMk.setText(selectedMatkul.getNamaMk());
        tfSks.setText(String.valueOf(selectedMatkul.getSks()));
        tfKodeMk.requestFocus();
        updateStatus("Mode edit matakuliah aktif", false);
    }

    @FXML
    void update(ActionEvent event) {
        String kodeMk = tfKodeMk.getText().trim();
        String namaMk = tfNamaMk.getText().trim();
        String sksText = tfSks.getText().trim();

        if (kodeMk.isEmpty() || namaMk.isEmpty() || sksText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Semua field harus diisi!");
            return;
        }

        try {
            int sksValue = Integer.parseInt(sksText);
            if (sksValue < 1 || sksValue > 6) {
                showAlert(Alert.AlertType.ERROR, "Error", "SKS harus antara 1-6!");
                return;
            }

            if (flagEdit == false) {
                AksesDB.addDataMatakuliah(kodeMk, namaMk, sksValue);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data matakuliah berhasil ditambahkan!");
                updateStatus("Data berhasil ditambahkan", true);
            } else {
                int idx = tvMatakuliah.getSelectionModel().getSelectedIndex();
                String kodeMkLama = tvMatakuliah.getItems().get(idx).getKodeMk();
                AksesDB.updateDataMatakuliah(kodeMk, namaMk, sksValue, kodeMkLama);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data matakuliah berhasil diperbarui!");
                updateStatus("Data berhasil diperbarui", true);
            }
            
            loadData();
            flagEdit = false;
            teksAktif(false);
            clearTeks();
            buttonAktif(false);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "SKS harus berupa angka!");
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
        int totalSks = listMatakuliah != null ? 
                      listMatakuliah.stream().mapToInt(Matakuliah::getSks).sum() : 0;
        showAlert(Alert.AlertType.INFORMATION, "Statistik", 
                 "Total Matakuliah: " + (listMatakuliah != null ? listMatakuliah.size() : 0) +
                 "\nTotal SKS: " + totalSks);
    }

    @FXML
    void refreshData(ActionEvent event) {
        loadData();
        updateStatus("Data berhasil di-refresh", true);
    }

    @FXML
    void showHelp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Bantuan", 
                 "Cara penggunaan:\n- Klik Tambah untuk menambah matakuliah\n- SKS harus angka 1-6\n- Kode matakuliah harus unik");
    }

    @FXML
    void showAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Tentang", "Modul Matakuliah - Sistem KRS UDINUS v2.0");
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
        tfKodeMk.setEditable(aktif);
        tfNamaMk.setEditable(aktif);
        tfSks.setEditable(aktif);
    }

    public void clearTeks() {
        tfKodeMk.setText("");
        tfNamaMk.setText("");
        tfSks.setText("3"); // Default 3 SKS
    }

    void initTabel() {
        kodeMk.setCellValueFactory(new PropertyValueFactory<Matakuliah, String>("kodeMk"));
        namaMk.setCellValueFactory(new PropertyValueFactory<Matakuliah, String>("namaMk"));
        sks.setCellValueFactory(new PropertyValueFactory<Matakuliah, Integer>("sks"));
    }

    void loadData() {
        listMatakuliah = AksesDB.getDataMatakuliah();
        if (listMatakuliah != null) {
            tvMatakuliah.setItems(listMatakuliah);
            setFilter();
        }
    }

    void setFilter() {
        if (listMatakuliah == null || tfCariMatkul == null) return;
        
        FilteredList<Matakuliah> filterData = new FilteredList<>(listMatakuliah, b -> true);
        tfCariMatkul.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(matkul -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                return matkul.getNamaMk().toLowerCase().indexOf(searchKeyword) > -1 ||
                       matkul.getKodeMk().toLowerCase().indexOf(searchKeyword) > -1;
            });
        });
        
        SortedList<Matakuliah> sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tvMatakuliah.comparatorProperty());
        tvMatakuliah.setItems(sortedData);
    }
}