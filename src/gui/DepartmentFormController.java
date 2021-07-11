package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Button btnSaveDepartment;
	@FXML
	private Button btnCancelDepartment;
	@FXML
	private TextField textIdDepartment;
	@FXML
	private TextField textNameDepartment;
	@FXML
	private Label labelErrorName;

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
		}catch(ValidationException e){
			setErrorMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department dep = new Department();
		
		ValidationException exception = new ValidationException("Validation error");
		
		dep.setId(Utils.tryParseToInt(textIdDepartment.getText()));
		if(textNameDepartment.getText() == null || textNameDepartment.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		dep.setName(textNameDepartment.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return dep;
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

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textIdDepartment);
		Constraints.setTextFieldMaxLength(textNameDepartment, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		textIdDepartment.setText(String.valueOf(entity.getId()));
		textNameDepartment.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
