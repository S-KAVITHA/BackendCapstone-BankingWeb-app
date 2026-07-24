
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.BankAdmin;




//@author Kavitha S

@Repository
public interface BankAdminRepository extends CrudRepository<BankAdmin, Long> {

	List<BankAdmin> findByemailId(String emailId);
}
