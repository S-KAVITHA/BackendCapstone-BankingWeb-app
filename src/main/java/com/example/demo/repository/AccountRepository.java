
package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.model.Account;
import com.example.demo.model.Customer;

//@author Kavitha S



@CrossOrigin("*")
@RepositoryRestResource

public interface AccountRepository extends JpaRepository<Account, Long> {

	List<Account> findBystatus(String status); 
	
	
  //  @Query("FROM Account a WHERE a.customer.id = :id")
   // @RestResource(path = "/byService")
   // public List<Account> findById(Long id);

	  
}
