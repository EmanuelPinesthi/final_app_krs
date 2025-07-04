package com.dinus;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class KrsController implements Initializable {
    ObservableList<Krs> listKrs;
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
    private Button btnPilihJadwal;

    @FXML
    private Button btnPilihMhs;

    @FXML
    private Button btnUpdate;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private Label lblErr;

    @FXML
    private TableColumn<Krs, String> kodeJadwal;

    @FXML
    private TableColumn<Krs, String> kodeMk;

    @FXML
    private TableColumn<Krs, String> namaMhs;

    @FXML
    private TableColumn<Krs, String> namaMk;

    @FXML
    private TableColumn<Krs, String> nim;

    @FXML
    private TableColumn<Krs, String> status;

    @FXML
    private TableView<Krs> tbKrs;

    @FXML
    private TextField tfCari;

    @FXML
    private TextField tfKodeJadwal;

    @FXML
    private TextField tfNim;

    @FXML
    private TextField tfNmMatkul;

    @FXML
    private TextField tfNmMhs;

    // Variables untuk menyimpan data yang dipilih
    private String selectedKodeMk;
    private String selectedKelas;
    private String originalKodeMk;
    private String originalKelas;
    private String originalNim;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listKrs = FXCollections.observableArrayList();
        initTabel();
        initComboBox();
        loadData();
        setFilter();
        buttonAktif(false);
        teksAktif(false);
        flagEdit = false;
        
        // Set initial state
        clearTeks();
        
        // Add visual feedback for table selection
        tbKrs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !flagEdit) {
                // Enable edit and delete buttons when an item is selected
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
        
        // Add placeholder text and tooltips
        tfCari.setPromptText("🔍 Cari data KRS...");
        
        // Add tooltips for better UX
        addButtonTooltips();
        
        System.out.println("KrsController initialized successfully");
    }
    
    private void addButtonTooltips() {
        // Add tooltips for better UX
        btnAdd.setTooltip(new javafx.scene.control.Tooltip("Tambah data KRS baru"));
        btnEdit.setTooltip(new javafx.scene.control.Tooltip("Edit data KRS yang dipilih"));
        btnDelete.setTooltip(new javafx.scene.control.Tooltip("Hapus data KRS yang dipilih"));
        btnUpdate.setTooltip(new javafx.scene.control.Tooltip("Simpan perubahan data"));
        btnCancel.setTooltip(new javafx.scene.control.Tooltip("Batalkan operasi"));
        btnPilihJadwal.setTooltip(new javafx.scene.control.Tooltip("Pilih jadwal dari daftar"));
        btnPilihMhs.setTooltip(new javafx.scene.control.Tooltip("Pilih mahasiswa dari daftar"));
    }

    private void initComboBox() {
        cbStatus.getItems().addAll("baru", "ulang");
        cbStatus.setValue("baru");
    }

    @FXML
    void pilihJadwal(ActionEvent event) {
        System.out.println("pilihJadwal method called");
        try {
            // Create FXML loader
            FXMLLoader loader = new FXMLLoader();
            
            // Try to find the FXML file
            URL fxmlUrl = getClass().getResource("fJadwalSearchKrs.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/fJadwalSearchKrs.fxml");
            }
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/com/dinus/fJadwalSearchKrs.fxml");
            }
            
            if (fxmlUrl == null) {
                // Create inline table dialog if FXML not found
                showJadwalTableDialog();
                return;
            }
            
            loader.setLocation(fxmlUrl);
            Parent root = loader.load();
            
            // Get controller
            JadwalSearchKrsController controller = loader.getController();
            
            // Create and show dialog
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Pilih Jadwal Kuliah");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.showAndWait();
            
            // Get selected data
            JadwalSearch selectedJadwal = (JadwalSearch) stage.getUserData();
            if (selectedJadwal != null) {
                tfKodeJadwal.setText(selectedJadwal.getKodeJadwal());
                tfNmMatkul.setText(selectedJadwal.getNamaMk());
                selectedKodeMk = selectedJadwal.getKodeMk();
                selectedKelas = selectedJadwal.getKelas();
                
                System.out.println("Selected: " + selectedJadwal.getKodeJadwal());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            // If error, show inline table dialog
            showJadwalTableDialog();
        }
    }
    
    private void showJadwalTableDialog() {
        try {
            // Create new stage for table dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pilih Jadwal Kuliah");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnPilihJadwal.getScene().getWindow());
            
            // Create table view
            TableView<JadwalSearch> tableView = new TableView<>();
            tableView.setPrefWidth(800);
            tableView.setPrefHeight(400);
            
            // Create columns
            TableColumn<JadwalSearch, String> colKodeJadwal = new TableColumn<>("Kode Jadwal");
            colKodeJadwal.setCellValueFactory(new PropertyValueFactory<>("kodeJadwal"));
            colKodeJadwal.setPrefWidth(120);
            
            TableColumn<JadwalSearch, String> colKodeMk = new TableColumn<>("Kode MK");
            colKodeMk.setCellValueFactory(new PropertyValueFactory<>("kodeMk"));
            colKodeMk.setPrefWidth(90);
            
            TableColumn<JadwalSearch, String> colNamaMk = new TableColumn<>("Matakuliah");
            colNamaMk.setCellValueFactory(new PropertyValueFactory<>("namaMk"));
            colNamaMk.setPrefWidth(200);
            
            TableColumn<JadwalSearch, String> colKelas = new TableColumn<>("Kelas");
            colKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));
            colKelas.setPrefWidth(70);
            
            TableColumn<JadwalSearch, String> colHari = new TableColumn<>("Hari");
            colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
            colHari.setPrefWidth(90);
            
            TableColumn<JadwalSearch, String> colJam = new TableColumn<>("Jam");
            colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
            colJam.setPrefWidth(120);
            
            TableColumn<JadwalSearch, String> colRuang = new TableColumn<>("Ruang");
            colRuang.setCellValueFactory(new PropertyValueFactory<>("ruang"));
            colRuang.setPrefWidth(110);
            
            tableView.getColumns().addAll(colKodeJadwal, colKodeMk, colNamaMk, colKelas, colHari, colJam, colRuang);
            
            // Load data
            ObservableList<JadwalSearch> jadwalData = AksesDB.getDataJadwalSearch();
            if (jadwalData != null) {
                tableView.setItems(jadwalData);
                System.out.println("Loaded " + jadwalData.size() + " jadwal records");
            } else {
                System.out.println("No jadwal data found");
            }
            
            // Create search field
            TextField searchField = new TextField();
            searchField.setPromptText("Cari matakuliah atau kode mk...");
            searchField.setPrefWidth(400);
            
            // Add search functionality
            FilteredList<JadwalSearch> filteredData = new FilteredList<>(jadwalData, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(jadwal -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return jadwal.getNamaMk().toLowerCase().contains(lowerCaseFilter) ||
                           jadwal.getKodeMk().toLowerCase().contains(lowerCaseFilter) ||
                           jadwal.getKodeJadwal().toLowerCase().contains(lowerCaseFilter);
                });
            });
            
            SortedList<JadwalSearch> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
            
            // Create buttons
            Button btnPilih = new Button("Pilih");
            btnPilih.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 8px 16px;");
            btnPilih.setOnAction(e -> {
                JadwalSearch selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    dialogStage.setUserData(selected);
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih jadwal dari tabel!");
                }
            });
            
            Button btnBatal = new Button("Batal");
            btnBatal.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8px 16px;");
            btnBatal.setOnAction(e -> dialogStage.close());
            
            // Create layout
            VBox layout = new VBox(10);
            layout.setPadding(new javafx.geometry.Insets(15));
            
            // Header
            Label header = new Label("Pilih Jadwal Kuliah");
            header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            
            // Search box
            HBox searchBox = new HBox(10);
            searchBox.getChildren().addAll(new Label("Cari:"), searchField);
            
            // Button box
            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(btnPilih, btnBatal);
            
            layout.getChildren().addAll(header, searchBox, tableView, buttonBox);
            
            // Set scene and show
            Scene scene = new Scene(layout, 850, 550);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            // Get result
            JadwalSearch selectedJadwal = (JadwalSearch) dialogStage.getUserData();
            if (selectedJadwal != null) {
                tfKodeJadwal.setText(selectedJadwal.getKodeJadwal());
                tfNmMatkul.setText(selectedJadwal.getNamaMk());
                selectedKodeMk = selectedJadwal.getKodeMk();
                selectedKelas = selectedJadwal.getKelas();
                
                System.out.println("Selected: " + selectedJadwal.getKodeJadwal());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat menampilkan data jadwal: " + e.getMessage());
        }
    }

    @FXML
    void pilihMhs(ActionEvent event) {
        System.out.println("pilihMhs method called");
        try {
            // Try to find FXML file first
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = getClass().getResource("fMhsSearchKrs.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/fMhsSearchKrs.fxml");
            }
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/com/dinus/fMhsSearchKrs.fxml");
            }
            
            if (fxmlUrl == null) {
                // Create inline table dialog if FXML not found
                showMhsTableDialog();
                return;
            }
            
            loader.setLocation(fxmlUrl);
            Parent root = loader.load();
            
            // Get controller
            MhsSearchKrsController controller = loader.getController();
            
            // Create and show dialog
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Pilih Mahasiswa");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.showAndWait();
            
            // Get selected data
            MhsSearch selectedMhs = (MhsSearch) stage.getUserData();
            if (selectedMhs != null) {
                tfNim.setText(selectedMhs.getNim());
                tfNmMhs.setText(selectedMhs.getNama());
                System.out.println("Selected: " + selectedMhs.getNim() + " - " + selectedMhs.getNama());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            // If error, show inline table dialog
            showMhsTableDialog();
        }
    }
    
    private void showMhsTableDialog() {
        try {
            // Create new stage for table dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pilih Mahasiswa");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnPilihMhs.getScene().getWindow());
            
            // Create table view
            TableView<MhsSearch> tableView = new TableView<>();
            tableView.setPrefWidth(700);
            tableView.setPrefHeight(400);
            
            // Create columns
            TableColumn<MhsSearch, String> colNim = new TableColumn<>("NIM");
            colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
            colNim.setPrefWidth(180);
            
            TableColumn<MhsSearch, String> colNama = new TableColumn<>("Nama Mahasiswa");
            colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
            colNama.setPrefWidth(250);
            
            TableColumn<MhsSearch, String> colAlamat = new TableColumn<>("Alamat");
            colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
            colAlamat.setPrefWidth(270);
            
            tableView.getColumns().addAll(colNim, colNama, colAlamat);
            
            // Load data
            ObservableList<MhsSearch> mhsData = AksesDB.getDataMhsSearch();
            if (mhsData != null) {
                tableView.setItems(mhsData);
                System.out.println("Loaded " + mhsData.size() + " mahasiswa records");
            } else {
                System.out.println("No mahasiswa data found");
            }
            
            // Create search field
            TextField searchField = new TextField();
            searchField.setPromptText("Cari nama atau NIM...");
            searchField.setPrefWidth(400);
            
            // Add search functionality
            FilteredList<MhsSearch> filteredData = new FilteredList<>(mhsData, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(mhs -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return mhs.getNama().toLowerCase().contains(lowerCaseFilter) ||
                           mhs.getNim().toLowerCase().contains(lowerCaseFilter);
                });
            });
            
            SortedList<MhsSearch> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
            
            // Create buttons
            Button btnPilih = new Button("Pilih");
            btnPilih.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 8px 16px;");
            btnPilih.setOnAction(e -> {
                MhsSearch selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    dialogStage.setUserData(selected);
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih mahasiswa dari tabel!");
                }
            });
            
            Button btnBatal = new Button("Batal");
            btnBatal.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8px 16px;");
            btnBatal.setOnAction(e -> dialogStage.close());
            
            // Create layout
            VBox layout = new VBox(10);
            layout.setPadding(new javafx.geometry.Insets(15));
            
            // Header
            Label header = new Label("Pilih Mahasiswa");
            header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            
            // Search box
            HBox searchBox = new HBox(10);
            searchBox.getChildren().addAll(new Label("Cari:"), searchField);
            
            // Button box
            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(btnPilih, btnBatal);
            
            layout.getChildren().addAll(header, searchBox, tableView, buttonBox);
            
            // Set scene and show
            Scene scene = new Scene(layout, 750, 550);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            // Get result
            MhsSearch selectedMhs = (MhsSearch) dialogStage.getUserData();
            if (selectedMhs != null) {
                tfNim.setText(selectedMhs.getNim());
                tfNmMhs.setText(selectedMhs.getNama());
                System.out.println("Selected: " + selectedMhs.getNim() + " - " + selectedMhs.getNama());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat menampilkan data mahasiswa: " + e.getMessage());
        }
    }

    @FXML
    void add(ActionEvent event) {
        flagEdit = false;
        teksAktif(true);
        buttonAktif(true);
        clearTeks();
        tfKodeJadwal.requestFocus();
        updateStatus("Mode tambah KRS aktif", false);
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
        int selectedIndex = tbKrs.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan dihapus!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data KRS?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Krs selectedKrs = tbKrs.getItems().get(selectedIndex);
            AksesDB.delDataKrs(selectedKrs.getKodeMk(), selectedKrs.getKelas(), selectedKrs.getNim());
            
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data KRS berhasil dihapus!");
            loadData();
            updateStatus("Data berhasil dihapus", true);
        }
    }

    @FXML
    void edit(ActionEvent event) {
        int selectedIndex = tbKrs.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih data yang akan diedit!");
            return;
        }
        
        buttonAktif(true);
        teksAktif(true);
        flagEdit = true;
        
        Krs selectedKrs = tbKrs.getItems().get(selectedIndex);
        
        // Set form fields
        tfKodeJadwal.setText(selectedKrs.getKodeJadwal());
        tfNmMatkul.setText(selectedKrs.getNamaMk());
        tfNim.setText(selectedKrs.getNim());
        tfNmMhs.setText(selectedKrs.getNamaMhs());
        cbStatus.setValue(selectedKrs.getStatus());
        
        // Set selected values for new data
        selectedKodeMk = selectedKrs.getKodeMk();
        selectedKelas = selectedKrs.getKelas();
        
        // Store original values for update
        originalKodeMk = selectedKrs.getKodeMk();
        originalKelas = selectedKrs.getKelas();
        originalNim = selectedKrs.getNim();
        
        tfKodeJadwal.requestFocus();
        updateStatus("Mode edit KRS aktif", false);
    }

    @FXML
    void update(ActionEvent event) {
        // Validasi input
        if (selectedKodeMk == null || selectedKelas == null || tfNim.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Silakan lengkapi semua data!");
            return;
        }
        
        String nim = tfNim.getText().trim();
        String status = cbStatus.getValue();

        try {
            if (flagEdit == false) {
                // Add new data
                AksesDB.addDataKrs(selectedKodeMk, selectedKelas, nim, status);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data KRS berhasil ditambahkan!");
                updateStatus("Data berhasil ditambahkan", true);
            } else {
                // Update existing data
                AksesDB.updateDataKrs(selectedKodeMk, selectedKelas, nim, status, 
                                    originalKodeMk, originalKelas, originalNim);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data KRS berhasil diperbarui!");
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
                 "Total Data KRS: " + (listKrs != null ? listKrs.size() : 0));
    }

    @FXML
    void refreshData(ActionEvent event) {
        loadData();
        updateStatus("Data berhasil di-refresh", true);
    }

    @FXML
    void showHelp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Bantuan", 
                 "Cara penggunaan:\n- Klik Tambah untuk menambah KRS\n- Pilih jadwal dan mahasiswa menggunakan tombol pencarian\n- Pilih status: baru atau ulang");
    }

    @FXML
    void showAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Tentang", "Modul KRS - Sistem KRS UDINUS v2.0");
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
        btnEdit.setDisable(true); // Will be enabled when selecting from table
        btnDelete.setDisable(true); // Will be enabled when selecting from table
        btnUpdate.setDisable(!nonAktif);
        btnCancel.setDisable(!nonAktif);
    }

    public void teksAktif(boolean aktif) {
        // Jadwal fields - hanya bisa diedit melalui dialog pencarian
        tfKodeJadwal.setEditable(false);
        tfNmMatkul.setEditable(false);
        btnPilihJadwal.setDisable(!aktif);
        
        // Mahasiswa fields - hanya bisa diedit melalui dialog pencarian  
        tfNim.setEditable(false);
        tfNmMhs.setEditable(false);
        btnPilihMhs.setDisable(!aktif);
        
        // Status - bisa diedit langsung
        cbStatus.setDisable(!aktif);
    }

    public void clearTeks() {
        tfKodeJadwal.setText("");
        tfNmMatkul.setText("");
        tfNim.setText("");
        tfNmMhs.setText("");
        cbStatus.setValue("baru");
        selectedKodeMk = null;
        selectedKelas = null;
        originalKodeMk = null;
        originalKelas = null;
        originalNim = null;
    }

    void initTabel() {
        kodeJadwal.setCellValueFactory(new PropertyValueFactory<Krs, String>("kodeJadwal"));
        kodeMk.setCellValueFactory(new PropertyValueFactory<Krs, String>("kodeMk"));
        namaMk.setCellValueFactory(new PropertyValueFactory<Krs, String>("namaMk"));
        nim.setCellValueFactory(new PropertyValueFactory<Krs, String>("nim"));
        namaMhs.setCellValueFactory(new PropertyValueFactory<Krs, String>("namaMhs"));
        status.setCellValueFactory(new PropertyValueFactory<Krs, String>("status"));
    }

    void loadData() {
        listKrs = AksesDB.getDataKrs();
        if (listKrs != null) {
            tbKrs.setItems(listKrs);
            setFilter(); // Refresh the filter
        }
    }

    void setFilter() {
        if (listKrs == null || tfCari == null) return;
        
        FilteredList<Krs> filterData = new FilteredList<>(listKrs, b -> true);
        tfCari.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(krs -> {
                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                return krs.getNamaMk().toLowerCase().indexOf(searchKeyword) > -1 ||
                       krs.getKodeMk().toLowerCase().indexOf(searchKeyword) > -1 ||
                       krs.getNamaMhs().toLowerCase().indexOf(searchKeyword) > -1 ||
                       krs.getNim().toLowerCase().indexOf(searchKeyword) > -1 ||
                       krs.getKodeJadwal().toLowerCase().indexOf(searchKeyword) > -1 ||
                       krs.getStatus().toLowerCase().indexOf(searchKeyword) > -1;
            });
        });
        
        SortedList<Krs> sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tbKrs.comparatorProperty());
        tbKrs.setItems(sortedData);
    }
}