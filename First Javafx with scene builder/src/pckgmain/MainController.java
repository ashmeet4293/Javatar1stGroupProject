package pckgmain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import pckg.database.DatabaseConnector;
import pckg.database.StudentDbUtils;
import pckg.model.Student;
import pckgCommon.AlertBox;
import pckgCommon.UsedFunctions;

public class MainController implements Initializable {
    
    private Connection conn = null;
    private OraclePreparedStatement pst = null;
    private OracleResultSet rs = null;
    Student student;
    StudentDbUtils std;
//    private ObservableList<Student> data;
    private File file;
    private FileChooser fileChooser;
    private InputStream is;
    FileInputStream fis;
//    private Stage window;
    private Image image;
    
    ObservableList<Student> data = FXCollections.observableArrayList();
    FilteredList<Student> filteredData = new FilteredList<>(data, e -> true);
    
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtContact;
    @FXML
    private TextField txtGender;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField txtRoll;
    @FXML
    private Button btnUpdate;
    @FXML
    private TextField txtId;
    @FXML
    private TableView<Student> tableView;
    @FXML
    private TableColumn<Student, Integer> colId;
    @FXML
    private TableColumn<Student, String> colName;
    @FXML
    private TableColumn<Student, String> colAddress;
    @FXML
    private TableColumn<Student, String> colRoll;
    @FXML
    private TableColumn<Student, String> colContact;
    @FXML
    private TableColumn<Student, String> colGender;
    @FXML
    private TableColumn<Student, String> colUsername;
    @FXML
    private TableColumn<Student, String> colPassword;
    @FXML
    private Button updateBtn;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnUpdate1;
    @FXML
    private ImageView imgView;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnExport;
    @FXML
    private TextField txtSearchField;
    @FXML
    private AnchorPane txtSearch;
    @FXML
    private DatePicker typeDatePicker;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//            txtId.setEditable (false);

    }
    
    private void handlecreateAction(ActionEvent event) {
        
    }
    
    @FXML
    private void handleCreateAction(ActionEvent event) throws FileNotFoundException, IOException {
        System.out.println("Date is : " + typeDatePicker.getEditor().getText());
        if (file != null) {
            fis = new FileInputStream(file);
            std = new StudentDbUtils();
            int id = std.getMaxId();
            id = id + 1;
            student = new Student();
            student.setId(id);
            student.setName(txtName.getText());
            student.setAddress(txtAddress.getText());
            student.setRoll_no(txtRoll.getText());
            student.setPhone_no(txtContact.getText());
            student.setGender(txtGender.getText());
            student.setUsername(txtUsername.getText());
            student.setPassword(txtPassword.getText());
            student.setDob(typeDatePicker.getEditor().getText());
            
            student.setFis(fis);
            
            if (std.createUser(student)) {
                clearFields();
                studentData();
                AlertBox.alert("Success", "Created Successfully ", "Record Saved ", "INFORMATION");
            }
        } else {
            AlertBox.alert("ERROR", "Upload Image ", "Please Select the image from the Device ", "ERROR");
        }
    }
    
    public void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtRoll.clear();
        txtContact.clear();
        txtGender.clear();
        txtUsername.clear();
        txtPassword.clear();
        imgView.setImage(null);
    }
    
    @FXML
    private void handleUpdateAction(ActionEvent event) throws SQLException {
        if (!txtId.getText().isEmpty()) {
            std = new StudentDbUtils();
            student = new Student();
            std.updateData(student);
            
            int id = Integer.parseInt(txtId.getText());
            student.setId(id);
            student.setName(txtName.getText());
            student.setAddress(txtAddress.getText());
            student.setRoll_no(txtRoll.getText());
            student.setPhone_no(txtContact.getText());
            student.setGender(txtGender.getText());
            student.setUsername(txtUsername.getText());
            student.setPassword(txtPassword.getText());
            
            std.updateData(student);
            studentData();
            clearFields();
        } else {
            AlertBox.alert("Error", "Cannot Update  ", "Cannot make update with empty ID ! ", "ERROR");
            
        }
        
    }
//    public Student getFromForm(){
//        
//    }

    private void studentData() {
        try {
            if ((conn = DatabaseConnector.databaseConnection()) != null) {
                String query = "SELECT * FROM  Student";
                pst = (OraclePreparedStatement) conn.prepareStatement(query);
                rs = (OracleResultSet) pst.executeQuery();
                while (rs.next()) {
                    int stdId = rs.getInt(1);
                    String stdName = rs.getString(2);
                    String stdAddress = rs.getString(3).toUpperCase();
                    String stdRoll = rs.getString(4).toUpperCase();
                    String stdContact = rs.getString(5).toUpperCase();
                    String stdGender = rs.getString(6).toUpperCase();
                    String stdUsername = rs.getString(7).toUpperCase();
                    String stdPassword = rs.getString(8).toUpperCase();
                    String stdDob = rs.getString(10);

//                    System.out.println("ID " + stdId + " STd Name : " + stdName + " Address : " + stdAddress + " Roll : " + stdRoll);
                    data.add(new Student(stdId, stdName, stdAddress, stdRoll, stdContact, stdGender, stdUsername, stdPassword, stdDob));
//                Double wage = rs.getDouble(10);
//                String stats = rs.getString(11).toUpperCase();
                } //end of while 
            }//end of if

        } catch (Exception e) {
        }
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        //name, address, phone_no, roll_no, gender, username, password
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colRoll.setCellValueFactory(new PropertyValueFactory<>("phone_no"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("roll_no"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
//        tableView.setItems(null);
        tableView.setItems(data);
        
    } //end of method 

    @FXML
    private void handleTableMouseClicked(MouseEvent event) throws IOException {
        Student stdnt = (Student) tableView.getSelectionModel().getSelectedItem();
        try {
            conn = DatabaseConnector.databaseConnection();
            String query = "select * from Student where ID = ? ";
            pst = (OraclePreparedStatement) conn.prepareStatement(query);
            pst.setInt(1, stdnt.getId());
            rs = (OracleResultSet) pst.executeQuery();
            while (rs.next()) {
                txtId.setText(rs.getString("id"));
                txtName.setText(rs.getString("name"));
                txtAddress.setText(rs.getString("address"));
                txtRoll.setText(rs.getString("roll"));
                txtContact.setText(rs.getString("contact"));
                txtGender.setText(rs.getString("gender"));
                txtUsername.setText(rs.getString("username"));
                txtPassword.setText(rs.getString("password"));
                typeDatePicker.getEditor().setText((rs.getString("DOB")));
                is = rs.getBinaryStream("image");
                
                if (is != null) {
                    OutputStream os = new FileOutputStream(new File("photo.jpg"));
                    byte[] content = new byte[1024];
                    while ((is.read(content) > 0)) {
                        os.write(content);
                    }
                    is.close();
                    image = new Image("file:photo.jpg");
                    imgView.setImage(image);
                } else {
                    imgView.setImage(null);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("Null pointer Exception");
        }
    }
    
    @FXML
    private void handleLoadAction(ActionEvent event) {
        studentData();
    }
    
    @FXML
    private void handleNewAction(ActionEvent event) {
        clearFields();
    }
    
    @FXML
    private void logoutAction(ActionEvent event) throws IOException {
        Stage current = (Stage) btnNew.getScene().getWindow();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource(UsedFunctions.LOGIN));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        current.hide();
        stage.show();
    }
    
    @FXML
    private void handleBrowseButton(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.gif", "*.png", "*.JPG")
        );
        Stage current = (Stage) btnNew.getScene().getWindow();
        file = fileChooser.showOpenDialog(current);
        
        if (file != null) {
            image = new Image(file.toURI().toString());
            imgView.setImage(image);
        }
    }
    
    @FXML
    private void printAction(ActionEvent event) {
    }
    
    @FXML
    private void exportAction(ActionEvent event) {
    }
    
    @FXML
    private void handleSearchAction(ActionEvent event) {
        System.out.println("something is typing here");
    }
    
    @FXML
    private void handleSearchKeyReleasedAction(KeyEvent event) {
        txtSearchField.textProperty().addListener((observalbleValue, oldValue, newValue) -> {
            filteredData.setPredicate((Predicate<? super Student>) stdnt -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                Integer id = stdnt.getId();
                if (id.toString().contains(newValue)) {
                    return true;
                }
                if (stdnt.getName().toLowerCase().contains(lowerCaseFilter)) {
                    System.out.println("Student name : " + stdnt.getName());
                    return true;
                }
//                if (user.getLastName().toLowerCase().contains(lowerCaseFilter)) {
//                    return true;
//                }
                return false;
            });
        });
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        System.out.println("Datas are : " + filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
    
}
