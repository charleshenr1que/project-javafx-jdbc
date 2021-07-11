package model.dao;

import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC();
	}
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}
}
