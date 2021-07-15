package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService service;

	private DepartmentService serviceDep;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Button btnSaveSeller;
	@FXML
	private Button btnCancelSeller;
	@FXML
	private TextField textIdSeller;
	@FXML
	private TextField textNameSeller;
	@FXML
	private TextField textEmailSeller;
	@FXML
	private DatePicker textBirthDateSeller;
	@FXML
	private TextField textBaseSalarySeller;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;
	@FXML
	private Label labelErrorDepartment;

	@FXML
	private ComboBox<Department> comboBoxDepartment;
	@FXML
	private List<Department> departments = new ArrayList<>();
	@FXML
	private ObservableList<Department> obsList;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entities erro");
		}
		if (service == null) {
			throw new IllegalStateException("Service erro");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, "Error", AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller seller = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		seller.setId(Utils.tryParseToInt(textIdSeller.getText()));
		if (textNameSeller.getText() == null || textNameSeller.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		seller.setName(textNameSeller.getText());
		if (textEmailSeller.getText() == null || textEmailSeller.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		seller.setEmail(textEmailSeller.getText());
		Instant instant ; 
		if(textBirthDateSeller.getValue() == null || textBirthDateSeller.getValue().equals("")) {
			exception.addError("birthDate", "Field can't be empty");
			instant = null;
		}
		instant= Instant.from(textBirthDateSeller.getValue().atStartOfDay(ZoneId.systemDefault()));
		
		
		
		seller.setBirthDate(Date.from(instant));
		
		if(instant == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		
		seller.setDepartment(comboBoxDepartment.getValue());
		if (textBaseSalarySeller.getText() == null || textBaseSalarySeller.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		seller.setBaseSalary(Utils.tryParseToDouble(textBaseSalarySeller.getText()));
		
		
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return seller;
	}

	@FXML
	public void onBtCalcelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService depService) {
		this.service = service;
		this.serviceDep = depService;
	}

	private void initializeNodes() {

		Constraints.setTextFieldInteger(textIdSeller);
		Constraints.setTextFieldMaxLength(textNameSeller, 70);
		Constraints.setTextFieldDouble(textBaseSalarySeller);
		Constraints.setTextFieldMaxLength(textEmailSeller, 60);
		Utils.formatDatePicker(textBirthDateSeller, "dd/MM/yyyy");

		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		textIdSeller.setText(String.valueOf(entity.getId()));
		textNameSeller.setText(entity.getName());
		textEmailSeller.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		textBaseSalarySeller.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			textBirthDateSeller
					.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}

	}

	public void loadObjects() {
		List<Department> list = serviceDep.findAll();

		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));
		}
		if (fields.contains("baseSalary")) {
			labelErrorEmail.setText(errors.get("baseSalary"));
		}
		if (fields.contains("birthDate")) {
			labelErrorEmail.setText(errors.get("birthDate"));
		}
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
