
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ChequeRequest;
import com.example.demo.model.FundsTransfer;


//@author Kavitha S

@Repository
public interface ChequeRequestRepository extends CrudRepository<ChequeRequest, Long> {
	List<ChequeRequest> findBystatus(String status);

}
