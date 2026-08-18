package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired
    private BankAdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FundsTransferRepository transferRepository;

    @Autowired
    private ChequeRequestRepository chequeRequestRepository;

    public BankAdmin addAdmin(BankAdmin user) {
        return adminRepository.save(user);
    }

    public List<BankAdmin> getAdminByEmail(String emailId) {
        return adminRepository.findByEmailId(emailId);
    }

    public List<Customer> getCustomersByStatus(String status) {
        return customerRepository.findByStatus(status);
    }

    public List<Account> getAccountsByStatus(String status) {
        return accountRepository.findByStatus(status);
    }

    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    public List<FundsTransfer> getTransfersByStatus(String status) {
        return transferRepository.findByStatus(status);
    }

    public List<ChequeRequest> getChequeRequestByStatus(String status) {
        return chequeRequestRepository.findByStatus(status);
    }

    public Customer updateCustomerByStatusId(Customer customer, Long id) {
        customer.setCustomerId(id);
        return customerRepository.save(customer);
    }

    public Account updateAcctByStatusId(Account account, Long id) {
        account.setAccountId(id);
        return accountRepository.save(account);
    }

    public Transaction updateTxnByStatusId(Transaction txn, Long id) {
        txn.setTransactionId(id);
        return transactionRepository.save(txn);
    }

    public FundsTransfer updateTransferByStatusId(FundsTransfer transfer, Long id) {
        transfer.setTransferId(id);
        return transferRepository.save(transfer);
    }

    public ChequeRequest updateChequeByStatusId(ChequeRequest request, Long id) {
        request.setRequestId(id);
        return chequeRequestRepository.save(request);
    }
}
