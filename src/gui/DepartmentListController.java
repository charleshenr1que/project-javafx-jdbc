package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service;

	@FXML
	private Button btnNewDepartment;
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumId;
	@FXML
	private TableColumn<Department, String> tableComlumName;

	@FXML
	public void onBtNewActpio() {
		System.out.println("On bT NEW!");
	}

	private ObservableList<Department> obsList;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNode();
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@FXML
	private void initializeNode() {

		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableComlumName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();

		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	public void setDepartment(DepartmentService service) {
		this.service = service;
	}

	public void initializeDepartment() {
		if (service == null) {
			throw new IllegalStateException("Error");
		}

		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);

	}

}
