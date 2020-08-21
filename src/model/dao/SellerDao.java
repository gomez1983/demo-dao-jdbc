package model.dao;

import java.util.List;

import model.entities.Seller;

public interface SellerDao {

	void insert(Seller obj); // Insere no banco de dados o "obj" que enviar como parâmetro de entrada
	void update(Seller obj); // Atualiza no banco de dados o "obj" que enviar como parâmetro de entrada
	void deleteById(Seller obj); // Deleta no banco de dados o "obj" que enviar como parâmetro de entrada
	Seller findById(Integer id); // Pega o Id e consulta no banco de dados o objeto com essa "id". Se existir, retorna. Se não, retorna nulo.
	List<Seller> findAll(); // "findAll" retorna todos os vendedores.
	
}
