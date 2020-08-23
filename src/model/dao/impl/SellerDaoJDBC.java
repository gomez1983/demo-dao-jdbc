package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn; // � a conex�o com o banco de dados;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery(); // Esse comando acima � executado e o resultado cai em result set "rs"
			if (rs.next()) { // Testa se veio algum resultado. Caso n�o retorne registro, o rs conect d� falto e pula o "if". O vendedor, no caso, � nulo (n�o tem vendedor com o id).
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep); // Aqui � um objeto inteiro, n�o apenas o ID da tabela
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department(); // Instancia um departamento
		dep.setId(rs.getInt("DepartmentId")); // Seta o id do Departamento conforme o nome dele na tabela.
		dep.setName(rs.getString("DepName")); // Seta o nome do vendedor conforme o nome dele na tabela.
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? " // *getId definira o c�digo desse departamento 
					+ "ORDER BY Name");
			
			st.setInt(1, department.getId()); // * Esse getId define o c�digo do departamento
			rs = st.executeQuery(); // Esse comando acima � executado e o resultado cai em result set "rs"
			
			List<Seller> list = new ArrayList<>();
			
//---------Um Map vazio � criado para guardar qualquer departamento que for instanciado.		
			Map<Integer, Department> map = new HashMap<>(); // Integer � o id do departamento. Map � usado aqui para n�o repetir a instancia��o do departamento. S� existe 1 classe departamento, e � dela que cada objeto vendedor � instanciada.
			
			while (rs.next()) { // Como pode vir um resultado nulo (nenhum departamento), temos que usar o while no lugar do if. Isso testa se veio algum resultado. Caso n�o retorne registro, o rs conect d� falto e pula o "if". O vendedor, no caso, � nulo (n�o tem departamento com o id).
				
				Department dep = map.get(rs.getInt("DepartmentID")); // Testa se o departamento j� existe. Caso sim, o map.get pega esse departamento
				
				if (dep == null) { // Se der nulo, significa que o departamento n�o existia. Neste caso, a� sim o departamento � instanciado.
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); // Departamento instanciado porque o if deu nulo.
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj); // Quando esgotar o result set, todos ser�o adicionados a lista. 
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

}
