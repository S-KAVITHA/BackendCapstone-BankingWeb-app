package com.example.demo;

import com.example.demo.controller.AdminController;
import com.example.demo.model.*;
import com.example.demo.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

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
        sampleAdmin.setRole("ADMIN");

        sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setStatus("APPROVED");

        sampleAccount = new Account();
        sampleAccount.setAccountId(1004450001L);
        sampleAccount.setStatus("APPROVED");

        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId(3L);
        sampleTransaction.setStatus("APPROVED");

        sampleTransfer = new FundsTransfer();
        sampleTransfer.setTransferId(10L);
        sampleTransfer.setStatus("APPROVED");

        sampleChequeRequest = new ChequeRequest();
        sampleChequeRequest.setRequestId(5L);
        sampleChequeRequest.setStatus("APPROVED");
    }

    // --- addAdmin ---

    @Test
    void testAddAdmin() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(sampleAdmin);

        mockMvc.perform(post("/Admin/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailId").value("admin@bank.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(adminService, times(1)).addAdmin(any(BankAdmin.class));
    }

    // --- getAdminByEmail ---

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
    void testGetAdminByEmail_NotFound() throws Exception {
        when(adminService.getAdminByEmail("missing@bank.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/Admin/getAdminByEmail/{emailId}", "missing@bank.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(adminService, times(1)).getAdminByEmail("missing@bank.com");
    }

    // --- getCustomersByStatus ---

    @Test
    void testGetCustomersByStatus() throws Exception {
        when(adminService.getCustomersByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleCustomer));

        mockMvc.perform(get("/Admin/getCustomersByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(adminService, times(1)).getCustomersByStatus("APPROVED");
    }

    // --- getAccountsByStatus ---

    @Test
    void testGetAccountsByStatus() throws Exception {
        when(adminService.getAccountsByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleAccount));

        mockMvc.perform(get("/Admin/getAccountsByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1004450001))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(adminService, times(1)).getAccountsByStatus("APPROVED");
    }

    // --- getTransactionsByStatus ---

    @Test
    void testGetTransactionsByStatus() throws Exception {
        when(adminService.getTransactionsByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleTransaction));

        mockMvc.perform(get("/Admin/getTransactionsByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(3));

        verify(adminService, times(1)).getTransactionsByStatus("APPROVED");
    }

    // --- getTransfersByStatus ---

    @Test
    void testGetTransfersByStatus() throws Exception {
        when(adminService.getTransfersByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleTransfer));

        mockMvc.perform(get("/Admin/getTransfersByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferId").value(10));

        verify(adminService, times(1)).getTransfersByStatus("APPROVED");
    }

    // --- getChequeRequestByStatus ---

    @Test
    void testGetChequeRequestByStatus() throws Exception {
        when(adminService.getChequeRequestByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(sampleChequeRequest));

        mockMvc.perform(get("/Admin/getChequeRequestByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(5));

        verify(adminService, times(1)).getChequeRequestByStatus("APPROVED");
    }

    // --- updateCustomer ---

    @Test
    void testUpdateCustomer() throws Exception {
        when(adminService.updateCustomerByStatusId(any(Customer.class), eq(1L))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/Admin/updateCustomer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Customer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(adminService, times(1)).updateCustomerByStatusId(any(Customer.class), eq(1L));
    }

    // --- updateAccount ---

    @Test
    void testUpdateAccount() throws Exception {
        when(adminService.updateAcctByStatusId(any(Account.class), eq(1004450001L))).thenReturn(sampleAccount);

        mockMvc.perform(put("/Admin/updateAccount/{id}", 1004450001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Account())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001));

        verify(adminService, times(1)).updateAcctByStatusId(any(Account.class), eq(1004450001L));
    }

    // --- updateTransaction ---

    @Test
    void testUpdateTransaction() throws Exception {
        when(adminService.updateTxnByStatusId(any(Transaction.class), eq(3L))).thenReturn(sampleTransaction);

        mockMvc.perform(put("/Admin/updateTransaction/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Transaction())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3));

        verify(adminService, times(1)).updateTxnByStatusId(any(Transaction.class), eq(3L));
    }

    // --- updateFundsTransfer ---

    @Test
    void testUpdateFundsTransfer() throws Exception {
        when(adminService.updateTransferByStatusId(any(FundsTransfer.class), eq(10L))).thenReturn(sampleTransfer);

        mockMvc.perform(put("/Admin/updateFundsTransfer/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FundsTransfer())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(10));

        verify(adminService, times(1)).updateTransferByStatusId(any(FundsTransfer.class), eq(10L));
    }

    // --- updateChequeRequest ---

    @Test
    void testUpdateChequeRequest() throws Exception {
        when(adminService.updateChequeByStatusId(any(ChequeRequest.class), eq(5L))).thenReturn(sampleChequeRequest);

        mockMvc.perform(put("/Admin/updateChequeRequest/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChequeRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(5));

        verify(adminService, times(1)).updateChequeByStatusId(any(ChequeRequest.class), eq(5L));
    }
}
