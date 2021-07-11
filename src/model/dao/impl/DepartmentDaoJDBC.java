package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	private Connection conn = null;

	public DepartmentDaoJDBC() {
		this.conn = DB.getConnection();
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO department " + "(Name) " + "VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());

			st.executeUpdate();

			System.out.println("Insert sucessfull!");

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("UPDATE department SET Name = ?" + " WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

			if (st.executeUpdate() == 1) {
				st.executeUpdate();
				System.out.println("Update sucessfull");
			} else {
				System.out.println("Update not sucessfull!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}

	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		Department dp = new Department();

		try {
			st = conn.prepareStatement("SELECT department.* FROM department WHERE Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				dp.setId(rs.getInt("Id"));
				dp.setName(rs.getString("Name"));
			} else {
				System.out.println("Ero");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		return dp;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Department> dp = new ArrayList<Department>();

		try {
			st = conn.prepareStatement("SELECT department.* FROM department");

			rs = st.executeQuery();
			while (rs.next()) {
				dp.add(new Department(rs.getInt("Id"), rs.getString("Name")));
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		return dp;
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			st.setInt(1, id);

			if (st.executeUpdate() == 1) {
				st.executeUpdate();
				System.out.println("Delete is sucessfull!" + id);
			} else {
				System.out.println("Delete not sucefull");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

}
