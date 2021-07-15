package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private Button btnNewSeller;
	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumId;
	@FXML
	private TableColumn<Seller, String> tableComlumName;

	@FXML
	private TableColumn<Seller, String> tableComlumEmail;

	@FXML
	private TableColumn<Seller, Date> tableComlumBirthDate;

	@FXML
	private TableColumn<Seller, Double> tableComlumSalary;

	@FXML
	private TableColumn<Seller, Seller> tableClomunEdit;

	@FXML
	private TableColumn<Seller, Seller> tableClomunDelete;

	@FXML
	public void onBtNewActpio(ActionEvent event) {
		Stage parentStage = gui.util.Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	private ObservableList<Seller> obsList;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNode();
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@FXML
	private void initializeNode() {

		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableComlumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableComlumEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableComlumBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableComlumBirthDate, "dd/MM/yyyy");
		tableComlumSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableComlumSalary, 2);
		Stage stage = (Stage) Main.getMainScene().getWindow();

		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
		

	}

	public void setSeller(SellerService service) {
		this.service = service;
	}

	public void initializeSeller() {
		if (service == null) {
			throw new IllegalStateException("Error");
		}

		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		deleteButtons();

	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		initializeSeller();
	}

	@FXML
	private void initEditButtons() {
		tableClomunEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableClomunEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Edit");
		

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});

	}

	private void deleteButtons() {
		tableClomunDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableClomunDelete.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});

	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "do you want to remove?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.removeSeller(obj);
				initializeSeller();
			} catch (DbException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}

		}

	}
	

}
