package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.service.AdminService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("Admin")
public class AdminController {

	@Autowired
	private AdminService adminService; // Injecting the service layer

	// Add admin to table
	@PostMapping("addAdmin")
	public BankAdmin addAdmin(@RequestBody BankAdmin user) {
		System.out.println(user);
		return adminService.addAdmin(user);
	}

	// Get admin by emailId
	@GetMapping("getAdminByEmail/{emailId}")
	public List<BankAdmin> getAdminByEmail(@PathVariable String emailId) {
		return adminService.getAdminByEmail(emailId);
	}

	// Get Customer by status
	@GetMapping("getCustomersByStatus/{status}")
	public List<Customer> getCustomersByStatus(@PathVariable String status) {
		System.out.println("Status: " + status);
		return adminService.getCustomersByStatus(status);
	}

	// Get Account by status
	@GetMapping("getAccountsByStatus/{status}")
	public List<Account> getAccountsByStatus(@PathVariable String status) {
		return adminService.getAccountsByStatus(status);
	}

	// Get Transaction by status
	@GetMapping("getTransactionsByStatus/{status}")
	public List<Transaction> getTransactionsByStatus(@PathVariable String status) {
		return adminService.getTransactionsByStatus(status);
	}

	// Get Transfer by status
	@GetMapping("getTransfersByStatus/{status}")
	public List<FundsTransfer> getTransfersByStatus(@PathVariable String status) {
		return adminService.getTransfersByStatus(status);
	}

	// Get Cheque Request by status
	@GetMapping("getChequeRequestByStatus/{status}")
	public List<ChequeRequest> getChequeRequestByStatus(@PathVariable String status) {
		return adminService.getChequeRequestByStatus(status);
	}

	// Update customer record as approved status
	@PutMapping("updateCustomer/{id}")
	public Customer updateCustomer(@RequestBody Customer customer, @PathVariable Long id) {
		System.out.println(customer);
		return adminService.updateCustomerByStatusId(customer, id);
	}

	// Update acct record as approved status
	@PutMapping("updateAccount/{id}")
	public Account updateAccount(@RequestBody Account account, @PathVariable Long id) {
		System.out.println(account);
		return adminService.updateAcctByStatusId(account, id);
	}

	// Update txn record as approved status
	@PutMapping("updateTransaction/{id}")
	public Transaction updateTransaction(@RequestBody Transaction txn, @PathVariable Long id) {
		System.out.println(txn);
		return adminService.updateTxnByStatusId(txn, id);
	}

	// Update transfer record as approved status
	@PutMapping("updateFundsTransfer/{id}")
	public FundsTransfer updateFundsTransfer(@RequestBody FundsTransfer transfer, @PathVariable Long id) {
		System.out.println(transfer);
		System.out.println("ID: " + id);
		return adminService.updateTransferByStatusId(transfer, id);
	}

	// Update cheques request record as approved status
	@PutMapping("updateChequeRequest/{id}")
	public ChequeRequest updateChequeRequest(@RequestBody ChequeRequest request, @PathVariable Long id) {
		System.out.println(request);
		return adminService.updateChequeByStatusId(request, id);
	}
}