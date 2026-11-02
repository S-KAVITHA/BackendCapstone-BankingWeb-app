package com.example.demo;

import com.example.demo.controller.AdminController;
import com.example.demo.model.Account;
import com.example.demo.model.BankAdmin;
import com.example.demo.model.ChequeRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;
import com.example.demo.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST API tests for {@link AdminController}.
 *
 * <p>Only the web layer is loaded ({@code @WebMvcTest}); {@link AdminService} is replaced
 * with a Mockito mock. Each test checks three things:
 * <ul>
 *   <li>Response: HTTP status, content type and JSON body</li>
 *   <li>Request handling: routing, path-variable and JSON body binding, invalid input</li>
 *   <li>Service interaction: the right service method is called with the right arguments</li>
 * </ul>
 */
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    private static final String BASE = "/Admin";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    private BankAdmin admin;
    private Customer customer;
    private Account account;
    private Transaction transaction;
    private FundsTransfer transfer;
    private ChequeRequest chequeRequest;

    @BeforeEach
    void setUp() {
        admin = new BankAdmin();
        admin.setId(1L);
        admin.setFirstName("Alice");
        admin.setLastName("Admin");
        admin.setEmailId("admin@bank.com");
        admin.setRole("SUPER_ADMIN");

        customer = new Customer();
        customer.setCustomerId(11L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setStatus("PENDING");

        account = new Account();
        account.setAccountId(1004450001L);
        account.setBalance(1500.0f);
        account.setStatus("PENDING");

        transaction = new Transaction();
        transaction.setTransactionId(21L);
        transaction.setAccountNo(1004450001L);
        transaction.setAmount(250.0f);
        transaction.setStatus("PENDING");

        transfer = new FundsTransfer();
        transfer.setTransferId(31L);
        transfer.setFromAcctNo(1004450001L);
        transfer.setToAcctNo(1004450002L);
        transfer.setAmount(100.0f);
        transfer.setStatus("PENDING");

        chequeRequest = new ChequeRequest();
        chequeRequest.setRequestId(41L);
        chequeRequest.setDescription("25-leaf cheque book");
        chequeRequest.setStatus("PENDING");
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    // ------------------------------------------------------------------
    // POST /Admin/addAdmin
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST addAdmin returns the saved admin as JSON")
    void addAdmin_returnsSavedAdmin() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(admin);

        mockMvc.perform(post(BASE + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(admin)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.emailId").value("admin@bank.com"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));
    }

    @Test
    @DisplayName("POST addAdmin binds the JSON body and passes it to the service once")
    void addAdmin_bindsRequestBody() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(admin);

        BankAdmin request = new BankAdmin();
        request.setFirstName("Bob");
        request.setEmailId("bob@bank.com");
        request.setRole("CLERK");

        mockMvc.perform(post(BASE + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<BankAdmin> captor = ArgumentCaptor.forClass(BankAdmin.class);
        verify(adminService, times(1)).addAdmin(captor.capture());
        verifyNoMoreInteractions(adminService);
        assertEquals("Bob", captor.getValue().getFirstName());
        assertEquals("bob@bank.com", captor.getValue().getEmailId());
        assertEquals("CLERK", captor.getValue().getRole());
    }

    // ------------------------------------------------------------------
    // GET /Admin/getAdminByEmail/{emailId}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET getAdminByEmail returns matching admins")
    void getAdminByEmail_returnsMatches() throws Exception {
        when(adminService.getAdminByEmail("admin@bank.com"))
                .thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get(BASE + "/getAdminByEmail/{emailId}", "admin@bank.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].emailId").value("admin@bank.com"));

        verify(adminService).getAdminByEmail("admin@bank.com");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET getAdminByEmail returns an empty array when nothing matches")
    void getAdminByEmail_noMatch() throws Exception {
        when(adminService.getAdminByEmail("nobody@bank.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE + "/getAdminByEmail/{emailId}", "nobody@bank.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(adminService).getAdminByEmail("nobody@bank.com");
    }

    // ------------------------------------------------------------------
    // GET /Admin/get*ByStatus/{status}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET getCustomersByStatus passes the status through and returns customers")
    void getCustomersByStatus() throws Exception {
        Customer second = new Customer();
        second.setCustomerId(12L);
        second.setStatus("PENDING");
        when(adminService.getCustomersByStatus("PENDING")).thenReturn(Arrays.asList(customer, second));

        mockMvc.perform(get(BASE + "/getCustomersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerId").value(11))
                .andExpect(jsonPath("$[1].customerId").value(12))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService).getCustomersByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET getAccountsByStatus returns accounts for the status")
    void getAccountsByStatus() throws Exception {
        when(adminService.getAccountsByStatus("PENDING")).thenReturn(Collections.singletonList(account));

        mockMvc.perform(get(BASE + "/getAccountsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1004450001L))
                .andExpect(jsonPath("$[0].balance").value(1500.0))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService).getAccountsByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET getTransactionsByStatus returns transactions for the status")
    void getTransactionsByStatus() throws Exception {
        when(adminService.getTransactionsByStatus("PENDING"))
                .thenReturn(Collections.singletonList(transaction));

        mockMvc.perform(get(BASE + "/getTransactionsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(21))
                .andExpect(jsonPath("$[0].amount").value(250.0));

        verify(adminService).getTransactionsByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET getTransfersByStatus returns transfers for the status")
    void getTransfersByStatus() throws Exception {
        when(adminService.getTransfersByStatus("PENDING")).thenReturn(Collections.singletonList(transfer));

        mockMvc.perform(get(BASE + "/getTransfersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferId").value(31))
                .andExpect(jsonPath("$[0].fromAcctNo").value(1004450001L))
                .andExpect(jsonPath("$[0].toAcctNo").value(1004450002L));

        verify(adminService).getTransfersByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET getChequeRequestByStatus returns cheque requests for the status")
    void getChequeRequestByStatus() throws Exception {
        when(adminService.getChequeRequestByStatus("PENDING"))
                .thenReturn(Collections.singletonList(chequeRequest));

        mockMvc.perform(get(BASE + "/getChequeRequestByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(41))
                .andExpect(jsonPath("$[0].description").value("25-leaf cheque book"));

        verify(adminService).getChequeRequestByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("GET by status returns an empty array when the service finds nothing")
    void getByStatus_emptyResult() throws Exception {
        when(adminService.getAccountsByStatus("CLOSED")).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE + "/getAccountsByStatus/{status}", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(adminService).getAccountsByStatus("CLOSED");
    }

    // ------------------------------------------------------------------
    // PUT /Admin/update*/{id}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT updateCustomer passes the body and path id to the service")
    void updateCustomer() throws Exception {
        Customer request = new Customer();
        request.setStatus("APPROVED");
        Customer updated = new Customer();
        updated.setCustomerId(11L);
        updated.setStatus("APPROVED");
        when(adminService.updateCustomerByStatusId(any(Customer.class), eq(11L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateCustomer/{id}", 11L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(11))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(adminService).updateCustomerByStatusId(captor.capture(), eq(11L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", captor.getValue().getStatus());
        // The controller must not set the id itself; that is the service's job.
        assertNull(captor.getValue().getCustomerId());
    }

    @Test
    @DisplayName("PUT updateAccount passes the body and path id to the service")
    void updateAccount() throws Exception {
        Account request = new Account();
        request.setStatus("APPROVED");
        Account updated = new Account();
        updated.setAccountId(1004450001L);
        updated.setStatus("APPROVED");
        when(adminService.updateAcctByStatusId(any(Account.class), eq(1004450001L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateAccount/{id}", 1004450001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(adminService).updateAcctByStatusId(captor.capture(), eq(1004450001L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("PUT updateTransaction passes the body and path id to the service")
    void updateTransaction() throws Exception {
        Transaction request = new Transaction();
        request.setStatus("APPROVED");
        Transaction updated = new Transaction();
        updated.setTransactionId(21L);
        updated.setStatus("APPROVED");
        when(adminService.updateTxnByStatusId(any(Transaction.class), eq(21L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateTransaction/{id}", 21L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(21))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(adminService).updateTxnByStatusId(captor.capture(), eq(21L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("PUT updateFundsTransfer passes the body and path id to the service")
    void updateFundsTransfer() throws Exception {
        FundsTransfer request = new FundsTransfer();
        request.setStatus("REJECTED");
        FundsTransfer updated = new FundsTransfer();
        updated.setTransferId(31L);
        updated.setStatus("REJECTED");
        when(adminService.updateTransferByStatusId(any(FundsTransfer.class), eq(31L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateFundsTransfer/{id}", 31L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(31))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        ArgumentCaptor<FundsTransfer> captor = ArgumentCaptor.forClass(FundsTransfer.class);
        verify(adminService).updateTransferByStatusId(captor.capture(), eq(31L));
        verifyNoMoreInteractions(adminService);
        assertEquals("REJECTED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("PUT updateChequeRequest passes the body and path id to the service")
    void updateChequeRequest() throws Exception {
        ChequeRequest request = new ChequeRequest();
        request.setStatus("APPROVED");
        ChequeRequest updated = new ChequeRequest();
        updated.setRequestId(41L);
        updated.setStatus("APPROVED");
        when(adminService.updateChequeByStatusId(any(ChequeRequest.class), eq(41L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateChequeRequest/{id}", 41L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(41))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<ChequeRequest> captor = ArgumentCaptor.forClass(ChequeRequest.class);
        verify(adminService).updateChequeByStatusId(captor.capture(), eq(41L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", captor.getValue().getStatus());
    }

    // ------------------------------------------------------------------
    // Invalid requests: rejected by Spring MVC before the service is called
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Malformed JSON body returns 400 and the service is not called")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailId\": \"admin@bank.com\""))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Missing request body returns 400 and the service is not called")
    void missingBody_returns400() throws Exception {
        mockMvc.perform(put(BASE + "/updateAccount/{id}", 1004450001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Non-numeric id path variable returns 400 and the service is not called")
    void nonNumericId_returns400() throws Exception {
        mockMvc.perform(put(BASE + "/updateCustomer/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Customer())))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Non-JSON content type returns 415 and the service is not called")
    void unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post(BASE + "/addAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("admin@bank.com"))
                .andExpect(status().isUnsupportedMediaType());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("GET on a POST endpoint returns 405 and the service is not called")
    void getOnPostEndpoint_returns405() throws Exception {
        mockMvc.perform(get(BASE + "/addAdmin"))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("POST on a PUT endpoint returns 405 and the service is not called")
    void postOnPutEndpoint_returns405() throws Exception {
        mockMvc.perform(post(BASE + "/updateTransaction/{id}", 21L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Transaction())))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Unknown path returns 404 and the service is not called")
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get(BASE + "/getEverything"))
                .andExpect(status().isNotFound());

        verifyZeroInteractions(adminService);
    }
}
