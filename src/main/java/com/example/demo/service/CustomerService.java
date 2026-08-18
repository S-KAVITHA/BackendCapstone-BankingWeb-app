package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FundsTransferRepository transferRepository;

	@Autowired
	private ChequeRequestRepository chequeRequestRepository;

	public Customer addCustomer(Customer customer) {
		customer.setCreatedDate(LocalDate.now());
		return customerRepository.save(customer);
	}

	public List<Long> getCustomerIds() {
		return ((Collection<Customer>) customerRepository.findAll()).stream()
				.map(Customer::getCustomerId)
				.collect(Collectors.toList());
	}

	public List<Customer> getCustomers() {
		return (List<Customer>) customerRepository.findAll();
	}

	public Customer updateCustomer(Customer customer, Long id) {
		customer.setCustomerId(id);
		return customerRepository.save(customer);
	}

	public List<Customer> getUserByEmail(String emailId) {
		return customerRepository.findByEmailId(emailId);
	}

	public Account addAccount(Account account) {
		account.setCreatedDate(LocalDate.now());
		return accountRepository.saveAndFlush(account);
	}

	public List<Account> findAllAccounts() {
		return accountRepository.findAll();
	}

	public List<Account> getAccountByCustomer(Long id) {
		return customerRepository.findByCustomerId(id);
	}

	public List<Long> findAllAccountIds() {
		return accountRepository.findAll().stream()
				.map(Account::getAccountId)
				.collect(Collectors.toList());
	}

	public List<Long> getAccountIdsByCustomer(Long customerId) {
		return customerRepository.findByCustomerId(customerId).stream()
				.map(Account::getAccountId)
				.collect(Collectors.toList());
	}

	public Optional<Account> getAccountById(Long id) {
		return accountRepository.findById(id);
	}

	public Transaction depositAccount(Transaction transaction) {
		transaction.setCreatedDate(LocalDate.now());
		Optional<Account> fetchAcct = accountRepository.findById(transaction.getAccountNo());

		if (fetchAcct.isPresent()) {
			Account acct = fetchAcct.get();
			acct.setBalance(acct.getBalance() + transaction.getAmount());
			accountRepository.save(acct);
		}
		return transactionRepository.save(transaction);
	}

	public Transaction withdrawAccount(Transaction transaction) {
		transaction.setCreatedDate(LocalDate.now());
		Optional<Account> fetchAcct = accountRepository.findById(transaction.getAccountNo());

		if (fetchAcct.isPresent()) {
			Account acct = fetchAcct.get();
			// Optional: Enterprise logic adds check here for insufficient funds
			acct.setBalance(acct.getBalance() - transaction.getAmount());
			accountRepository.save(acct);
		}
		return transactionRepository.save(transaction);
	}

	public FundsTransfer transferFunds(FundsTransfer fundstransfer) {
		fundstransfer.setCreatedDate(LocalDate.now());

		// Process Debit from source
		Optional<Account> fetchFAcct = accountRepository.findById(fundstransfer.getFromAcctNo());
		if (fetchFAcct.isPresent()) {
			Account fromAcct = fetchFAcct.get();
			fromAcct.setBalance(fromAcct.getBalance() - fundstransfer.getAmount());
			accountRepository.save(fromAcct);
		}

		// Process Credit to target
		Optional<Account> fetchTAcct = accountRepository.findById(fundstransfer.getToAcctNo());
		if (fetchTAcct.isPresent()) {
			Account toAcct = fetchTAcct.get();
			toAcct.setBalance(toAcct.getBalance() + fundstransfer.getAmount());
			accountRepository.save(toAcct);
		}

		return transferRepository.save(fundstransfer);
	}

	public ChequeRequest requestChequeBook(ChequeRequest chequerequest) {
		chequerequest.setCreatedDate(LocalDate.now());
		return chequeRequestRepository.save(chequerequest);
	}
}
