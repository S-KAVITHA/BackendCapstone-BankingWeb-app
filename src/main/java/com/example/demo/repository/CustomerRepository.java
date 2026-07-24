
  package com.example.demo.repository;
  
  import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Account;
import com.example.demo.model.Customer;
  
  //@author Kavitha S
  
  @Repository
  public interface CustomerRepository extends CrudRepository<Customer, Long>{
	  
	 // @EntityGraph(value = "customer.accounts", type = EntityGraphType.FETCH)
	//  @Query("select r.customerId from Customer r inner join fetch r.accounts where r.customerId = :id")
	  
		
	 @Query("SELECT  a FROM Customer c, Account a WHERE c.customerId = :id")
	  List<Account> findBycustomerId(@Param("id") Long id);;	
	  
	  List<Customer> findByemailId(String emailId);
	  
	  List<Customer> findBystatus(String status);
  }
 