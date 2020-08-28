  
package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {

	void insert(Department obj); // Insere no banco de dados o "obj" que enviar como parâmetro de entrada
	void update(Department obj); // Atualiza no banco de dados o "obj" que enviar como parâmetro de entrada
	void deleteById(Integer id); // Deleta no banco de dados o "obj" que enviar como parâmetro de entrada
	Department findById(Integer id); // Pega o Id e consulta no banco de dados o objeto com essa "id". Se existir, retorna. Se não, retorna nulo.
	List<Department> findAll(); // "findAll" retorna todos os departamentos.
}
