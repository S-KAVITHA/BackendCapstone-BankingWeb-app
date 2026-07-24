
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.model.Account;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;

//@author Kavitha S

@CrossOrigin("*")
@RepositoryRestResource

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findBystatus(String status); 
}
