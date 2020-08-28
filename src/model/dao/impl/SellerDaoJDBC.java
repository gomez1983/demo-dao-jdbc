package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn; // É a conexão com o banco de dados;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) { // obj é o parâmetro de entrada. Os dados adicionados abaixo vão pertencer a este obj
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) " // Todos os itens da tabela do banco de dados. 
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)", // Correspondem as colunas da tabela; São chamados de PLACEHOLDERS porque seriam os textos que ficam numa tabela como marcação (como um box de login que está escrito "login", mas ele some quando você começa a digitar).
					Statement.RETURN_GENERATED_KEYS); // Retorna o ID do vendedor inserido.
			
			st.setString(1, obj.getName()); // Insere nome do "obj". No caso, String.
			st.setString(2, obj.getEmail()); // Insere email do "obj". No caso, String.
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); // Insere a data de nascimento do "obj". No caso, deste construtor, tem que colocar o "getTime".
			st.setDouble(4, obj.getBaseSalary()); // Insere salário do "obj". No caso, Double.
			st.setInt(5, obj.getDepartment().getId()); // Insere departamento do "obj". No caso, o ID é um int. Primeiro acessa o Departamento usando "getDepartment()", depois de acessar, insere o ID por meio do "getId()".
			
			int rowsAffected = st.executeUpdate(); // Executa o update do que foi inserido acima.
			
			if (rowsAffected > 0) { // Se a coluna afetada for maior que 0, significa que foi inserido valores.
				ResultSet rs = st.getGeneratedKeys(); // Então os dados são inseridos
				if (rs.next()) { 
					int id = rs.getInt(1); // Se o ID for gerado...
							obj.setId(id); // ... ele é atribuido ao objeto "obj".
				}
				DB.closeResultSet(rs); // Exceção personalizada caso nenhuma linha ter sido alterada.
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?"); // Aqui em update, esse seria o 6 interrogação 
								
			st.setString(1, obj.getName()); // Atualiza nome do "obj". No caso, String.
			st.setString(2, obj.getEmail()); // Atualiza email do "obj". No caso, String.
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); // Atualiza a data de nascimento do "obj". No caso, deste construtor, tem que colocar o "getTime".
			st.setDouble(4, obj.getBaseSalary()); // Atualiza salário do "obj". No caso, Double.
			st.setInt(5, obj.getDepartment().getId()); // Atualiza departamento do "obj". No caso, o ID é um int. Primeiro acessa o Departamento usando "getDepartment()", depois de acessar, insere o ID por meio do "getId()".
			st.setInt(6, obj.getId());
			
			st.executeUpdate(); // Executa o update do que foi inserido acima.
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
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
			rs = st.executeQuery(); // Esse comando acima é executado e o resultado cai em result set "rs"
			if (rs.next()) { // Testa se veio algum resultado. Caso não retorne registro, o rs conect dá falto e pula o "if". O vendedor, no caso, é nulo (não tem vendedor com o id).
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
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep); // Aqui é um objeto inteiro, não apenas o ID da tabela
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
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
				
			rs = st.executeQuery(); // Esse comando acima é executado e o resultado cai em result set "rs"
			
			List<Seller> list = new ArrayList<>();
			
//---------Um Map vazio é criado para guardar qualquer departamento que for instanciado.		
			Map<Integer, Department> map = new HashMap<>(); // Integer é o id do departamento. Map é usado aqui para não repetir a instanciação do departamento. Só existe 1 classe departamento, e é dela que cada objeto vendedor é instanciada.
			
			while (rs.next()) { // Como pode vir um resultado nulo (nenhum departamento), temos que usar o while no lugar do if. Isso testa se veio algum resultado. Caso não retorne registro, o rs conect dá falto e pula o "if". O vendedor, no caso, é nulo (não tem departamento com o id).
				
				Department dep = map.get(rs.getInt("DepartmentID")); // Testa se o departamento já existe. Caso sim, o map.get pega esse departamento
				
				if (dep == null) { // Se der nulo, significa que o departamento não existia. Neste caso, aí sim o departamento é instanciado.
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); // Departamento instanciado porque o if deu nulo.
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj); // Quando esgotar o result set, todos serão adicionados a lista. 
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

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? " // *getId definira o código desse departamento 
					+ "ORDER BY Name");
			
			st.setInt(1, department.getId()); // * Esse getId define o código do departamento
			rs = st.executeQuery(); // Esse comando acima é executado e o resultado cai em result set "rs"
			
			List<Seller> list = new ArrayList<>();
			
//---------Um Map vazio é criado para guardar qualquer departamento que for instanciado.		
			Map<Integer, Department> map = new HashMap<>(); // Integer é o id do departamento. Map é usado aqui para não repetir a instanciação do departamento. Só existe 1 classe departamento, e é dela que cada objeto vendedor é instanciada.
			
			while (rs.next()) { // Como pode vir um resultado nulo (nenhum departamento), temos que usar o while no lugar do if. Isso testa se veio algum resultado. Caso não retorne registro, o rs conect dá falto e pula o "if". O vendedor, no caso, é nulo (não tem departamento com o id).
				
				Department dep = map.get(rs.getInt("DepartmentID")); // Testa se o departamento já existe. Caso sim, o map.get pega esse departamento
				
				if (dep == null) { // Se der nulo, significa que o departamento não existia. Neste caso, aí sim o departamento é instanciado.
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); // Departamento instanciado porque o if deu nulo.
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj); // Quando esgotar o result set, todos serão adicionados a lista. 
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
