package com.example.demo;

import com.example.demo.controller.CustomerController;
import com.example.demo.model.*;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Customer sampleCustomer;
    private Account sampleAccount;
    private Transaction sampleTransaction;
    private FundsTransfer sampleTransfer;
    private ChequeRequest sampleChequeRequest;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setFirstName("John");
        sampleCustomer.setLastName("Doe");
        sampleCustomer.setEmailId("john.doe@example.com");

        sampleAccount = new Account();
        sampleAccount.setAccountId(1004450001L);
        sampleAccount.setBalance(2500.50f);

        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId(3L);
        sampleTransaction.setAccountNo(1004450001L);
        sampleTransaction.setAmount(500.00f);

        sampleTransfer = new FundsTransfer();
        sampleTransfer.setTransferId(10L);
        sampleTransfer.setFromAcctNo(1004450001L);
        sampleTransfer.setToAcctNo(1004450002L);
        sampleTransfer.setAmount(300.00f);

        sampleChequeRequest = new ChequeRequest();
        sampleChequeRequest.setRequestId(5L);
    }

    // --- addCustomer ---

    @Test
    void testAddCustomer() throws Exception {
        when(customerService.addCustomer(any(Customer.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/Customer/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Customer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.emailId").value("john.doe@example.com"));

        verify(customerService, times(1)).addCustomer(any(Customer.class));
    }

    // --- getCustomerIds ---

    @Test
    void testGetCustomerIds() throws Exception {
        when(customerService.getCustomerIds()).thenReturn(Arrays.asList(1L, 2L, 3L));

        mockMvc.perform(get("/Customer/getCustomerIds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[2]").value(3));

        verify(customerService, times(1)).getCustomerIds();
    }

    // --- getCustomers ---

    @Test
    void testGetCustomers() throws Exception {
        when(customerService.getCustomers()).thenReturn(Collections.singletonList(sampleCustomer));

        mockMvc.perform(get("/Customer/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(customerService, times(1)).getCustomers();
    }

    // --- updateCustomer ---

    @Test
    void testUpdateCustomer() throws Exception {
        when(customerService.updateCustomer(any(Customer.class), eq(1L))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/Customer/updateCustomer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Customer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));

        verify(customerService, times(1)).updateCustomer(any(Customer.class), eq(1L));
    }

    // --- getUserByEmail ---

    @Test
    void testGetUserByEmail() throws Exception {
        when(customerService.getUserByEmail("john.doe@example.com"))
                .thenReturn(Collections.singletonList(sampleCustomer));

        mockMvc.perform(get("/Customer/getUserByEmail/{emailId}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailId").value("john.doe@example.com"));

        verify(customerService, times(1)).getUserByEmail("john.doe@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        when(customerService.getUserByEmail("missing@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/Customer/getUserByEmail/{emailId}", "missing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService, times(1)).getUserByEmail("missing@example.com");
    }

    // --- addAccount ---

    @Test
    void testAddAccount() throws Exception {
        when(customerService.addAccount(any(Account.class))).thenReturn(sampleAccount);

        mockMvc.perform(post("/Customer/addAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Account())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001))
                .andExpect(jsonPath("$.balance").value(2500.50));

        verify(customerService, times(1)).addAccount(any(Account.class));
    }

    // --- findAllAccounts ---

    @Test
    void testFindAllAccounts() throws Exception {
        Account secondAccount = new Account();
        secondAccount.setAccountId(1004450002L);
        when(customerService.findAllAccounts()).thenReturn(Arrays.asList(sampleAccount, secondAccount));

        mockMvc.perform(get("/Customer/findAllAccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(customerService, times(1)).findAllAccounts();
    }

    // --- getAccountByCustomer ---

    @Test
    void testGetAccountByCustomer() throws Exception {
        when(customerService.getAccountByCustomer(1L)).thenReturn(Collections.singletonList(sampleAccount));

        mockMvc.perform(get("/Customer/getAccountByCustomer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1004450001));

        verify(customerService, times(1)).getAccountByCustomer(1L);
    }

    // --- findAllAccountIds ---

    @Test
    void testFindAllAccountIds() throws Exception {
        when(customerService.findAllAccountIds()).thenReturn(Arrays.asList(1004450001L, 1004450002L));

        mockMvc.perform(get("/Customer/findAllAccountIds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(customerService, times(1)).findAllAccountIds();
    }

    // --- getAccountIdsByCustomer ---

    @Test
    void testGetAccountIdsByCustomer() throws Exception {
        when(customerService.getAccountIdsByCustomer(1L)).thenReturn(Collections.singletonList(1004450001L));

        mockMvc.perform(get("/Customer/getAccountIdsByCustomer/{customerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1004450001));

        verify(customerService, times(1)).getAccountIdsByCustomer(1L);
    }

    // --- getAccountById ---

    @Test
    void testGetAccountById_Found() throws Exception {
        when(customerService.getAccountById(1004450001L)).thenReturn(Optional.of(sampleAccount));

        mockMvc.perform(get("/Customer/getAccountById/{id}", 1004450001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001));

        verify(customerService, times(1)).getAccountById(1004450001L);
    }

    @Test
    void testGetAccountById_NotFound() throws Exception {
        when(customerService.getAccountById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/Customer/getAccountById/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(customerService, times(1)).getAccountById(999L);
    }

    // --- depositAccount ---

    @Test
    void testDepositAccount() throws Exception {
        when(customerService.depositAccount(any(Transaction.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/Customer/depositAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3))
                .andExpect(jsonPath("$.amount").value(500.00));

        verify(customerService, times(1)).depositAccount(any(Transaction.class));
    }

    // --- withdrawAccount ---

    @Test
    void testWithdrawAccount() throws Exception {
        when(customerService.withdrawAccount(any(Transaction.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/Customer/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3));

        verify(customerService, times(1)).withdrawAccount(any(Transaction.class));
    }

    // --- transferAccounts ---

    @Test
    void testTransferAccounts() throws Exception {
        when(customerService.transferFunds(any(FundsTransfer.class))).thenReturn(sampleTransfer);

        mockMvc.perform(post("/Customer/transferAccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTransfer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(10))
                .andExpect(jsonPath("$.amount").value(300.00));

        verify(customerService, times(1)).transferFunds(any(FundsTransfer.class));
    }

    // --- chequeRequest ---

    @Test
    void testChequeRequest() throws Exception {
        when(customerService.requestChequeBook(any(ChequeRequest.class))).thenReturn(sampleChequeRequest);

        mockMvc.perform(post("/Customer/chequeRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleChequeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(5));

        verify(customerService, times(1)).requestChequeBook(any(ChequeRequest.class));
    }
}
