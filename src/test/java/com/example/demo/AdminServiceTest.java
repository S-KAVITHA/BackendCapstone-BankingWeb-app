package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FundsTransferRepository transferRepository;

    @Mock
    private ChequeRequestRepository chequeRequestRepository;

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

    // =========================================================
    // STATUS LOOKUP TESTS
    // =========================================================

    @Test
    void testGetCustomersByStatus() {
        when(customerRepository.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleCustomer));

        List<Customer> result =
                adminService.getCustomersByStatus("PENDING");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId().longValue());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(customerRepository, times(1))
                .findByStatus("PENDING");
    }

    @Test
    void testGetAccountsByStatus() {
        when(accountRepository.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleAccount));

        List<Account> result =
                adminService.getAccountsByStatus("PENDING");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1004450001L, result.get(0).getAccountId().longValue());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(accountRepository, times(1))
                .findByStatus("PENDING");
    }

    @Test
    void testGetTransactionsByStatus() {
        when(transactionRepository.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleTransaction));

        List<Transaction> result =
                adminService.getTransactionsByStatus("PENDING");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getTransactionId().longValue());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(transactionRepository, times(1))
                .findByStatus("PENDING");
    }

    @Test
    void testGetTransfersByStatus() {
        when(transferRepository.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleTransfer));

        List<FundsTransfer> result =
                adminService.getTransfersByStatus("PENDING");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getTransferId().longValue());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(transferRepository, times(1))
                .findByStatus("PENDING");
    }

    @Test
    void testGetChequeRequestByStatus() {
        when(chequeRequestRepository.findByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleChequeRequest));

        List<ChequeRequest> result =
                adminService.getChequeRequestByStatus("PENDING");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getRequestId().longValue());
        assertEquals("PENDING", result.get(0).getStatus());

        verify(chequeRequestRepository, times(1))
                .findByStatus("PENDING");
    }

    // =========================================================
    // UPDATE TESTS
    // =========================================================

    @Test
    void testUpdateCustomerByStatusId() {
        Customer customer = new Customer();
        customer.setStatus("APPROVED");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated =
                adminService.updateCustomerByStatusId(customer, 1L);

        assertNotNull(updated);
        assertEquals(1L, updated.getCustomerId().longValue());
        assertEquals("APPROVED", updated.getStatus());

        ArgumentCaptor<Customer> captor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(captor.capture());

        assertEquals(1L, captor.getValue().getCustomerId().longValue());
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    void testUpdateAcctByStatusId() {
        Account account = new Account();
        account.setStatus("APPROVED");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account updated =
                adminService.updateAcctByStatusId(account, 1004450001L);

        assertNotNull(updated);
        assertEquals(1004450001L, updated.getAccountId().longValue());
        assertEquals("APPROVED", updated.getStatus());

        ArgumentCaptor<Account> captor =
                ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save(captor.capture());

        assertEquals(1004450001L, captor.getValue().getAccountId().longValue());
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    void testUpdateTxnByStatusId() {
        Transaction transaction = new Transaction();
        transaction.setStatus("APPROVED");

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction updated =
                adminService.updateTxnByStatusId(transaction, 3L);

        assertNotNull(updated);
        assertEquals(3L, updated.getTransactionId().longValue());
        assertEquals("APPROVED", updated.getStatus());

        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        assertEquals(3L, captor.getValue().getTransactionId().longValue());
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    void testUpdateTransferByStatusId() {
        FundsTransfer transfer = new FundsTransfer();
        transfer.setStatus("APPROVED");

        when(transferRepository.save(any(FundsTransfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FundsTransfer updated =
                adminService.updateTransferByStatusId(transfer, 10L);

        assertNotNull(updated);
        assertEquals(10L, updated.getTransferId().longValue());
        assertEquals("APPROVED", updated.getStatus());

        ArgumentCaptor<FundsTransfer> captor =
                ArgumentCaptor.forClass(FundsTransfer.class);

        verify(transferRepository).save(captor.capture());

        assertEquals(10L, captor.getValue().getTransferId().longValue());
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    void testUpdateChequeByStatusId() {
        ChequeRequest chequeRequest = new ChequeRequest();
        chequeRequest.setStatus("APPROVED");
        chequeRequest.setStatus("APPROVED");

        when(chequeRequestRepository.save(any(ChequeRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChequeRequest updated =
                adminService.updateChequeByStatusId(chequeRequest, 5L);

        assertNotNull(updated);
        assertEquals(5L, updated.getRequestId().longValue());
        assertEquals("APPROVED", updated.getStatus());

        ArgumentCaptor<ChequeRequest> captor =
                ArgumentCaptor.forClass(ChequeRequest.class);

        verify(chequeRequestRepository).save(captor.capture());

        assertEquals(5L, captor.getValue().getRequestId().longValue());
        assertEquals("APPROVED", captor.getValue().getStatus());
    }
}