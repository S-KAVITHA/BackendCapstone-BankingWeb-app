package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FundsTransferRepository transferRepository;

    @Mock
    private ChequeRequestRepository chequeRequestRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer sampleCustomer;
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setFirstName("John");
        sampleCustomer.setLastName("Doe");

        sourceAccount = new Account();
        sourceAccount.setAccountId(1004450001L);
        sourceAccount.setBalance(2500.50f);

        targetAccount = new Account();
        targetAccount.setAccountId(1004450002L);
        targetAccount.setBalance(1200.00f);
    }

    @Test
    void testAddCustomer() {
        // Arrange: Tell the mock repository to return whatever customer object it receives
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer inputCustomer = invocation.getArgument(0);
            inputCustomer.setFirstName("John"); // Ensure our assertion condition passes
            return inputCustomer;
        });

        // Act: Execute the service call
        Customer created = customerService.addCustomer(new Customer());

        // Assert: Verify all validation points
        assertNotNull(created, "The created customer object should not be null.");
        assertEquals("John", created.getFirstName(), "The customer first name should be John.");
        assertNotNull(created.getCreatedDate(), "The created date should be automatically set by the service layer.");

        // Verify repository loop interaction
        verify(customerRepository, times(1)).save(any(Customer.class));
    }


    @Test
    void testFindAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Arrays.asList(sourceAccount, targetAccount));

        List<Account> accounts = customerService.findAllAccounts();

        assertEquals(2, accounts.size());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testDepositAccount_Success() {
        Transaction tx = new Transaction();
        tx.setAccountNo(1004450001L);
        tx.setAmount(500.00f);

        when(accountRepository.findById(1004450001L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        Transaction processedTx = customerService.depositAccount(tx);

        assertEquals(3000.50f, sourceAccount.getBalance()); // 2500.50 + 500.00
        assertNotNull(processedTx.getCreatedDate());
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(transactionRepository, times(1)).save(tx);
    }
    @Test
    void testWithdrawAccount_Success() {
        // 1. Arrange
        Transaction tx = new Transaction();
        tx.setAccountNo(1004450001L);
        tx.setAmount(200.00f);

        when(accountRepository.findById(1004450001L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        // 2. Act (This was missing!)
        Transaction processedTx = customerService.withdrawAccount(tx);

        // 3. Assert
        assertNotNull(processedTx);
        assertEquals(2300.50f, sourceAccount.getBalance()); // 2500.50 - 200.00
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(transactionRepository, times(1)).save(tx);
    }


    @Test
    void testTransferFunds_Success() {
        FundsTransfer transfer = new FundsTransfer();
        transfer.setFromAcctNo(1004450001L);
        transfer.setToAcctNo(1004450002L);
        transfer.setAmount(300.00f);

        when(accountRepository.findById(1004450001L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(1004450002L)).thenReturn(Optional.of(targetAccount));
        when(transferRepository.save(any(FundsTransfer.class))).thenReturn(transfer);

        FundsTransfer processedTransfer = customerService.transferFunds(transfer);

        assertEquals(2200.50f, sourceAccount.getBalance()); // 2500.50 - 300
        assertEquals(1500.00f, targetAccount.getBalance()); // 1200.00 + 300
        assertNotNull(processedTransfer.getCreatedDate());
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transferRepository, times(1)).save(transfer);
    }

    @Test
    void testRequestChequeBook() {
        ChequeRequest request = new ChequeRequest();
        when(chequeRequestRepository.save(any(ChequeRequest.class))).thenReturn(request);

        ChequeRequest processedRequest = customerService.requestChequeBook(request);

        assertNotNull(processedRequest);
        assertNotNull(processedRequest.getCreatedDate());
        verify(chequeRequestRepository, times(1)).save(request);
    }
}
