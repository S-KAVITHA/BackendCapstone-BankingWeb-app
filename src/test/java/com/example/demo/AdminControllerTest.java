package com.example.demo;

import com.example.demo.controller.AdminController;
import com.example.demo.model.*;
import com.example.demo.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc-based controller tests for {@link AdminController}. The {@link AdminService}
 * dependency is mocked so only controller-level concerns (routing, request binding,
 * HTTP status/response body, and service interaction) are exercised.
 */
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private BankAdmin sampleAdmin;
    private Customer sampleCustomer;
    private Account sampleAccount;
    private Transaction sampleTransaction;
    private FundsTransfer sampleTransfer;
    private ChequeRequest sampleChequeRequest;

    @BeforeEach
    void setUp() {
        sampleAdmin = new BankAdmin();
        sampleAdmin.setId(1L);
        sampleAdmin.setEmailId("admin@bank.com");
        sampleAdmin.setFirstName("Alice");
        sampleAdmin.setLastName("Admin");
        sampleAdmin.setRole("SUPER_ADMIN");

        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setFirstName("John");
        sampleCustomer.setLastName("Doe");
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
    // addAdmin
    // =========================================================

    @Test
    void testAddAdmin() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(sampleAdmin);

        mockMvc.perform(post("/Admin/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAdmin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.emailId").value("admin@bank.com"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));

        verify(adminService, times(1)).addAdmin(any(BankAdmin.class));
    }

    // =========================================================
    // getAdminByEmail
    // =========================================================

    @Test
    void testGetAdminByEmail() throws Exception {
        when(adminService.getAdminByEmail("admin@bank.com"))
                .thenReturn(Collections.singletonList(sampleAdmin));

        mockMvc.perform(get("/Admin/getAdminByEmail/{emailId}", "admin@bank.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].emailId").value("admin@bank.com"));

        verify(adminService, times(1)).getAdminByEmail("admin@bank.com");
    }

    @Test
    void testGetAdminByEmail_NoMatch_ReturnsEmptyList() throws Exception {
        when(adminService.getAdminByEmail("missing@bank.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/Admin/getAdminByEmail/{emailId}", "missing@bank.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(adminService, times(1)).getAdminByEmail("missing@bank.com");
    }

    // =========================================================
    // getCustomersByStatus
    // =========================================================

    @Test
    void testGetCustomersByStatus() throws Exception {
        when(adminService.getCustomersByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleCustomer));

        mockMvc.perform(get("/Admin/getCustomersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService, times(1)).getCustomersByStatus("PENDING");
    }

    // =========================================================
    // getAccountsByStatus
    // =========================================================

    @Test
    void testGetAccountsByStatus() throws Exception {
        when(adminService.getAccountsByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleAccount));

        mockMvc.perform(get("/Admin/getAccountsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1004450001))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService, times(1)).getAccountsByStatus("PENDING");
    }

    // =========================================================
    // getTransactionsByStatus
    // =========================================================

    @Test
    void testGetTransactionsByStatus() throws Exception {
        when(adminService.getTransactionsByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleTransaction));

        mockMvc.perform(get("/Admin/getTransactionsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(3))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService, times(1)).getTransactionsByStatus("PENDING");
    }

    // =========================================================
    // getTransfersByStatus
    // =========================================================

    @Test
    void testGetTransfersByStatus() throws Exception {
        when(adminService.getTransfersByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleTransfer));

        mockMvc.perform(get("/Admin/getTransfersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferId").value(10))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService, times(1)).getTransfersByStatus("PENDING");
    }

    // =========================================================
    // getChequeRequestByStatus
    // =========================================================

    @Test
    void testGetChequeRequestByStatus() throws Exception {
        when(adminService.getChequeRequestByStatus("PENDING"))
                .thenReturn(Collections.singletonList(sampleChequeRequest));

        mockMvc.perform(get("/Admin/getChequeRequestByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(5))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService, times(1)).getChequeRequestByStatus("PENDING");
    }

    // =========================================================
    // updateCustomer
    // =========================================================

    @Test
    void testUpdateCustomer() throws Exception {
        Customer requestBody = new Customer();
        requestBody.setStatus("APPROVED");

        Customer serviceResponse = new Customer();
        serviceResponse.setCustomerId(1L);
        serviceResponse.setStatus("APPROVED");

        when(adminService.updateCustomerByStatusId(any(Customer.class), eq(1L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/Admin/updateCustomer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(adminService, times(1)).updateCustomerByStatusId(captor.capture(), eq(1L));
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    // =========================================================
    // updateAccount
    // =========================================================

    @Test
    void testUpdateAccount() throws Exception {
        Account requestBody = new Account();
        requestBody.setStatus("APPROVED");

        Account serviceResponse = new Account();
        serviceResponse.setAccountId(1004450001L);
        serviceResponse.setStatus("APPROVED");

        when(adminService.updateAcctByStatusId(any(Account.class), eq(1004450001L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/Admin/updateAccount/{id}", 1004450001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(adminService, times(1)).updateAcctByStatusId(captor.capture(), eq(1004450001L));
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    // =========================================================
    // updateTransaction
    // =========================================================

    @Test
    void testUpdateTransaction() throws Exception {
        Transaction requestBody = new Transaction();
        requestBody.setStatus("APPROVED");

        Transaction serviceResponse = new Transaction();
        serviceResponse.setTransactionId(3L);
        serviceResponse.setStatus("APPROVED");

        when(adminService.updateTxnByStatusId(any(Transaction.class), eq(3L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/Admin/updateTransaction/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adminService, times(1)).updateTxnByStatusId(any(Transaction.class), eq(3L));
    }

    // =========================================================
    // updateFundsTransfer
    // =========================================================

    @Test
    void testUpdateFundsTransfer() throws Exception {
        FundsTransfer requestBody = new FundsTransfer();
        requestBody.setStatus("APPROVED");

        FundsTransfer serviceResponse = new FundsTransfer();
        serviceResponse.setTransferId(10L);
        serviceResponse.setStatus("APPROVED");

        when(adminService.updateTransferByStatusId(any(FundsTransfer.class), eq(10L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/Admin/updateFundsTransfer/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(10))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adminService, times(1)).updateTransferByStatusId(any(FundsTransfer.class), eq(10L));
    }

    // =========================================================
    // updateChequeRequest
    // =========================================================

    @Test
    void testUpdateChequeRequest() throws Exception {
        ChequeRequest requestBody = new ChequeRequest();
        requestBody.setStatus("APPROVED");

        ChequeRequest serviceResponse = new ChequeRequest();
        serviceResponse.setRequestId(5L);
        serviceResponse.setStatus("APPROVED");

        when(adminService.updateChequeByStatusId(any(ChequeRequest.class), eq(5L)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/Admin/updateChequeRequest/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(5))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adminService, times(1)).updateChequeByStatusId(any(ChequeRequest.class), eq(5L));
    }

    // =========================================================
    // No unexpected service interactions sanity check
    // =========================================================

    @Test
    void testGetAccountsByStatus_DoesNotTouchOtherServiceMethods() throws Exception {
        when(adminService.getAccountsByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleAccount));

        mockMvc.perform(get("/Admin/getAccountsByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk());

        verify(adminService, times(1)).getAccountsByStatus("APPROVED");
        verifyNoMoreInteractions(adminService);
    }
}
