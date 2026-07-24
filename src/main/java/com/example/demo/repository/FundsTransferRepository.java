
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Account;
import com.example.demo.model.Customer;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;

//@author Kavitha S

@Repository
public interface FundsTransferRepository extends CrudRepository<FundsTransfer, Long> {
	 
	List<FundsTransfer> findBystatus(String status);
}
