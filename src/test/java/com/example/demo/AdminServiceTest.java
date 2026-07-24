package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private FundsTransferRepository transferRepository;
    @Mock private ChequeRequestRepository chequeRequestRepository;

    @InjectMocks
    private AdminService adminService;

    private Customer sampleCustomer;
    private Account sampleAccount;
    private Transaction sampleTransaction;
    private FundsTransfer sampleTransfer;
    private ChequeRequest sampleChequeRequest;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setStatus("PENDING");

        sampleAccount = new Account();
        sampleAccount.setAccountId(1004450001L);
        sampleAccount.setStatus("PENDING");

        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId(3L);
        sampleTransaction.setStatus("PENDING");

        sampleTransfer = new FundsTransfer();
        sampleTransfer.setTransferId(10L);
        sampleTransfer.setStatus("PENDING");

        sampleChequeRequest = new ChequeRequest();
        sampleChequeRequest.setRequestId(5L);
        sampleChequeRequest.setStatus("PENDING");
    }

    // --- Status Query Lookup Tests ---

    @Test
    void testGetCustomersByStatus() {
        when(customerRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(sampleCustomer));

        List<Customer> result = adminService.getCustomersByStatus("PENDING");

        assertFalse(result.isEmpty());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(customerRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    void testGetAccountsByStatus() {
        when(accountRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(sampleAccount));

        List<Account> result = adminService.getAccountsByStatus("PENDING");

        assertFalse(result.isEmpty());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(accountRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    void testGetTransactionsByStatus() {
        when(transactionRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(sampleTransaction));

        List<Transaction> result = adminService.getTransactionsByStatus("PENDING");

        assertFalse(result.isEmpty());
        verify(transactionRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    void testGetTransfersByStatus() {
        when(transferRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(sampleTransfer));

        List<FundsTransfer> result = adminService.getTransfersByStatus("PENDING");

        assertFalse(result.isEmpty());
        verify(transferRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    void testGetChequeRequestByStatus() {
        when(chequeRequestRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(sampleChequeRequest));

        List<ChequeRequest> result = adminService.getChequeRequestByStatus("PENDING");

        assertFalse(result.isEmpty());
        verify(chequeRequestRepository, times(1)).findByStatus("PENDING");
    }

    // --- Record Modification Update Tests ---

    @Test
    void testUpdateCustomerByStatusId() {
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        Customer updated = adminService.updateCustomerByStatusId(new Customer(), 1L);

        assertNotNull(updated);
        assertEquals(1L, updated.getCustomerId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateAcctByStatusId() {
        when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);

        Account updated = adminService.updateAcctByStatusId(new Account(), 1004450001L);

        assertNotNull(updated);
        assertEquals(1004450001L, updated.getAccountId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdateTxnByStatusId() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        Transaction updated = adminService.updateTxnByStatusId(new Transaction(), 3L);

        assertNotNull(updated);
        assertEquals(3L, updated.getTransactionId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransferByStatusId() {
        when(transferRepository.save(any(FundsTransfer.class))).thenReturn(sampleTransfer);

        FundsTransfer updated = adminService.updateTransferByStatusId(new FundsTransfer(), 10L);

        assertNotNull(updated);
        assertEquals(10L, updated.getTransferId());
        verify(transferRepository, times(1)).save(any(FundsTransfer.class));
    }

    @Test
    void testUpdateChequeByStatusId() {
        when(chequeRequestRepository.save(any(ChequeRequest.class))).thenReturn(sampleChequeRequest);

        ChequeRequest updated = adminService.updateChequeByStatusId(new ChequeRequest(), 5L);

        assertNotNull(updated);
        assertEquals(5L, updated.getRequestId());
        verify(chequeRequestRepository, times(1)).save(any(ChequeRequest.class));
    }
}
