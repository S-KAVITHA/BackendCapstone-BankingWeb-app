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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST API tests for {@link AdminController}.
 *
 * <p>{@code @WebMvcTest} starts only the Spring MVC layer (routing, JSON conversion,
 * argument binding). {@link AdminService} is replaced by a Mockito mock, so no database
 * or business logic runs. The tests cover three areas:
 * <ol>
 *   <li><b>Response</b> - status code, content type and JSON body produced from the
 *       service's return value</li>
 *   <li><b>Request handling</b> - URL mapping, path variables, JSON body binding and
 *       rejection of invalid requests</li>
 *   <li><b>Service interaction</b> - the correct service method is called exactly once
 *       with the bound arguments, and nothing else is called</li>
 * </ol>
 */
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    private static final String BASE_URL = "/Admin";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    // ---------------------------------------------------------------------
    // Test data builders
    // ---------------------------------------------------------------------

    private static BankAdmin admin(Long id, String email, String role) {
        BankAdmin a = new BankAdmin();
        a.setId(id);
        a.setFirstName("Priya");
        a.setLastName("Raman");
        a.setEmailId(email);
        a.setRole(role);
        return a;
    }

    private static Customer customer(Long id, String status) {
        Customer c = new Customer();
        c.setCustomerId(id);
        c.setFirstName("Arun");
        c.setLastName("Kumar");
        c.setEmailId("arun.kumar@example.com");
        c.setStatus(status);
        return c;
    }

    private static Account account(Long id, String status) {
        Account a = new Account();
        a.setAccountId(id);
        a.setCustName("Arun Kumar");
        a.setAccttype("SAVINGS");
        a.setCurrency("INR");
        a.setBalance(10000.0f);
        a.setCreatedDate(LocalDate.of(2026, 1, 15));
        a.setStatus(status);
        return a;
    }

    private static Transaction transaction(Long id, String status) {
        Transaction t = new Transaction();
        t.setTransactionId(id);
        t.setAccountNo(5000100001L);
        t.setAmount(750.0f);
        t.setTxntype("DEPOSIT");
        t.setStatus(status);
        return t;
    }

    private static FundsTransfer transfer(Long id, String status) {
        FundsTransfer f = new FundsTransfer();
        f.setTransferId(id);
        f.setFromAcctNo(5000100001L);
        f.setToAcctNo(5000100002L);
        f.setAmount(1200.0f);
        f.setStatus(status);
        return f;
    }

    private static ChequeRequest chequeRequest(Long id, String status) {
        ChequeRequest r = new ChequeRequest();
        r.setRequestId(id);
        r.setAccountNo(5000100001L);
        r.setDescription("50-leaf cheque book");
        r.setStatus(status);
        return r;
    }

    private String toJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    // =====================================================================
    // POST /Admin/addAdmin
    // =====================================================================

    @Test
    @DisplayName("addAdmin: 200 with the saved admin serialised as JSON")
    void addAdmin_returnsSavedAdmin() throws Exception {
        BankAdmin saved = admin(1L, "priya@bank.com", "MANAGER");
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(saved);

        mockMvc.perform(post(BASE_URL + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(admin(null, "priya@bank.com", "MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Priya"))
                .andExpect(jsonPath("$.lastName").value("Raman"))
                .andExpect(jsonPath("$.emailId").value("priya@bank.com"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @DisplayName("addAdmin: JSON body is bound field-by-field and passed to the service once")
    void addAdmin_bindsBodyAndCallsServiceOnce() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(admin(2L, "x@bank.com", "CLERK"));

        String body = "{\"firstName\":\"Meena\",\"lastName\":\"Iyer\","
                + "\"emailId\":\"meena@bank.com\",\"password\":\"s3cret\",\"role\":\"CLERK\"}";

        mockMvc.perform(post(BASE_URL + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<BankAdmin> captor = ArgumentCaptor.forClass(BankAdmin.class);
        verify(adminService, times(1)).addAdmin(captor.capture());
        verifyNoMoreInteractions(adminService);

        BankAdmin bound = captor.getValue();
        assertNull(bound.getId());
        assertEquals("Meena", bound.getFirstName());
        assertEquals("Iyer", bound.getLastName());
        assertEquals("meena@bank.com", bound.getEmailId());
        assertEquals("s3cret", bound.getPassword());
        assertEquals("CLERK", bound.getRole());
    }

    @Test
    @DisplayName("addAdmin: 200 with empty body when the service returns null")
    void addAdmin_serviceReturnsNull() throws Exception {
        when(adminService.addAdmin(any(BankAdmin.class))).thenReturn(null);

        mockMvc.perform(post(BASE_URL + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(admin(null, "a@bank.com", "CLERK"))))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(adminService).addAdmin(any(BankAdmin.class));
    }

    // =====================================================================
    // GET /Admin/getAdminByEmail/{emailId}
    // =====================================================================

    @Test
    @DisplayName("getAdminByEmail: path variable is passed to the service and matches are returned")
    void getAdminByEmail_returnsMatches() throws Exception {
        when(adminService.getAdminByEmail("priya@bank"))
                .thenReturn(Collections.singletonList(admin(1L, "priya@bank", "MANAGER")));

        // No dot in the email: Spring MVC would otherwise treat ".com" as a file extension.
        mockMvc.perform(get(BASE_URL + "/getAdminByEmail/{emailId}", "priya@bank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].emailId").value("priya@bank"));

        verify(adminService).getAdminByEmail("priya@bank");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getAdminByEmail: 200 with [] when no admin matches")
    void getAdminByEmail_noMatch() throws Exception {
        when(adminService.getAdminByEmail("unknown")).thenReturn(Collections.<BankAdmin>emptyList());

        mockMvc.perform(get(BASE_URL + "/getAdminByEmail/{emailId}", "unknown"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(adminService).getAdminByEmail("unknown");
        verifyNoMoreInteractions(adminService);
    }

    // =====================================================================
    // GET /Admin/get*ByStatus/{status}
    // =====================================================================

    @Test
    @DisplayName("getCustomersByStatus: returns every customer the service finds for the status")
    void getCustomersByStatus() throws Exception {
        when(adminService.getCustomersByStatus("PENDING"))
                .thenReturn(Arrays.asList(customer(101L, "PENDING"), customer(102L, "PENDING")));

        mockMvc.perform(get(BASE_URL + "/getCustomersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerId").value(101))
                .andExpect(jsonPath("$[1].customerId").value(102))
                .andExpect(jsonPath("$[*].status").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.is("PENDING"))));

        verify(adminService).getCustomersByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getCustomersByStatus: @JsonIgnore collections are not exposed in the response")
    void getCustomersByStatus_hidesIgnoredFields() throws Exception {
        when(adminService.getCustomersByStatus("APPROVED"))
                .thenReturn(Collections.singletonList(customer(103L, "APPROVED")));

        mockMvc.perform(get(BASE_URL + "/getCustomersByStatus/{status}", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accounts").doesNotExist())
                .andExpect(jsonPath("$[0].transactions").doesNotExist())
                .andExpect(jsonPath("$[0].requests").doesNotExist());
    }

    @Test
    @DisplayName("getAccountsByStatus: returns accounts, with createdDate as an ISO date")
    void getAccountsByStatus() throws Exception {
        when(adminService.getAccountsByStatus("PENDING"))
                .thenReturn(Collections.singletonList(account(5000100001L, "PENDING")));

        mockMvc.perform(get(BASE_URL + "/getAccountsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountId").value(5000100001L))
                .andExpect(jsonPath("$[0].accttype").value("SAVINGS"))
                .andExpect(jsonPath("$[0].balance").value(10000.0))
                .andExpect(jsonPath("$[0].createdDate").value("2026-01-15"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(adminService).getAccountsByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getTransactionsByStatus: returns transactions for the status")
    void getTransactionsByStatus() throws Exception {
        when(adminService.getTransactionsByStatus("PENDING"))
                .thenReturn(Collections.singletonList(transaction(301L, "PENDING")));

        mockMvc.perform(get(BASE_URL + "/getTransactionsByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transactionId").value(301))
                .andExpect(jsonPath("$[0].accountNo").value(5000100001L))
                .andExpect(jsonPath("$[0].amount").value(750.0))
                .andExpect(jsonPath("$[0].txntype").value("DEPOSIT"));

        verify(adminService).getTransactionsByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getTransfersByStatus: returns funds transfers for the status")
    void getTransfersByStatus() throws Exception {
        when(adminService.getTransfersByStatus("PENDING"))
                .thenReturn(Collections.singletonList(transfer(401L, "PENDING")));

        mockMvc.perform(get(BASE_URL + "/getTransfersByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transferId").value(401))
                .andExpect(jsonPath("$[0].fromAcctNo").value(5000100001L))
                .andExpect(jsonPath("$[0].toAcctNo").value(5000100002L))
                .andExpect(jsonPath("$[0].amount").value(1200.0));

        verify(adminService).getTransfersByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("getChequeRequestByStatus: returns cheque requests for the status")
    void getChequeRequestByStatus() throws Exception {
        when(adminService.getChequeRequestByStatus("PENDING"))
                .thenReturn(Collections.singletonList(chequeRequest(501L, "PENDING")));

        mockMvc.perform(get(BASE_URL + "/getChequeRequestByStatus/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requestId").value(501))
                .andExpect(jsonPath("$[0].description").value("50-leaf cheque book"));

        verify(adminService).getChequeRequestByStatus("PENDING");
        verifyNoMoreInteractions(adminService);
    }

    @Test
    @DisplayName("get*ByStatus: status path variable is passed through exactly as sent (no case change)")
    void getByStatus_passesStatusVerbatim() throws Exception {
        when(adminService.getTransfersByStatus("approved")).thenReturn(Collections.<FundsTransfer>emptyList());

        mockMvc.perform(get(BASE_URL + "/getTransfersByStatus/{status}", "approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(adminService).getTransfersByStatus("approved");
        verifyNoMoreInteractions(adminService);
    }

    // =====================================================================
    // PUT /Admin/update*/{id}
    // =====================================================================

    @Test
    @DisplayName("updateCustomer: body and path id go to updateCustomerByStatusId; result is returned")
    void updateCustomer() throws Exception {
        Customer request = new Customer();
        request.setStatus("APPROVED");
        when(adminService.updateCustomerByStatusId(any(Customer.class), eq(101L)))
                .thenReturn(customer(101L, "APPROVED"));

        mockMvc.perform(put(BASE_URL + "/updateCustomer/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Customer> body = ArgumentCaptor.forClass(Customer.class);
        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        verify(adminService).updateCustomerByStatusId(body.capture(), id.capture());
        verifyNoMoreInteractions(adminService);
        assertEquals(Long.valueOf(101L), id.getValue());
        assertEquals("APPROVED", body.getValue().getStatus());
        // The controller does not copy the path id into the body; the service owns that.
        assertNull(body.getValue().getCustomerId());
    }

    @Test
    @DisplayName("updateAccount: body and path id go to updateAcctByStatusId; result is returned")
    void updateAccount() throws Exception {
        Account request = new Account();
        request.setStatus("APPROVED");
        when(adminService.updateAcctByStatusId(any(Account.class), eq(5000100001L)))
                .thenReturn(account(5000100001L, "APPROVED"));

        mockMvc.perform(put(BASE_URL + "/updateAccount/{id}", 5000100001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(5000100001L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Account> body = ArgumentCaptor.forClass(Account.class);
        verify(adminService).updateAcctByStatusId(body.capture(), eq(5000100001L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", body.getValue().getStatus());
    }

    @Test
    @DisplayName("updateTransaction: body and path id go to updateTxnByStatusId; result is returned")
    void updateTransaction() throws Exception {
        Transaction request = new Transaction();
        request.setStatus("APPROVED");
        when(adminService.updateTxnByStatusId(any(Transaction.class), eq(301L)))
                .thenReturn(transaction(301L, "APPROVED"));

        mockMvc.perform(put(BASE_URL + "/updateTransaction/{id}", 301L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(301))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Transaction> body = ArgumentCaptor.forClass(Transaction.class);
        verify(adminService).updateTxnByStatusId(body.capture(), eq(301L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", body.getValue().getStatus());
    }

    @Test
    @DisplayName("updateFundsTransfer: body and path id go to updateTransferByStatusId; result is returned")
    void updateFundsTransfer() throws Exception {
        FundsTransfer request = new FundsTransfer();
        request.setStatus("REJECTED");
        when(adminService.updateTransferByStatusId(any(FundsTransfer.class), eq(401L)))
                .thenReturn(transfer(401L, "REJECTED"));

        mockMvc.perform(put(BASE_URL + "/updateFundsTransfer/{id}", 401L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(401))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        ArgumentCaptor<FundsTransfer> body = ArgumentCaptor.forClass(FundsTransfer.class);
        verify(adminService).updateTransferByStatusId(body.capture(), eq(401L));
        verifyNoMoreInteractions(adminService);
        assertEquals("REJECTED", body.getValue().getStatus());
    }

    @Test
    @DisplayName("updateChequeRequest: body and path id go to updateChequeByStatusId; result is returned")
    void updateChequeRequest() throws Exception {
        ChequeRequest request = new ChequeRequest();
        request.setStatus("APPROVED");
        when(adminService.updateChequeByStatusId(any(ChequeRequest.class), eq(501L)))
                .thenReturn(chequeRequest(501L, "APPROVED"));

        mockMvc.perform(put(BASE_URL + "/updateChequeRequest/{id}", 501L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(501))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<ChequeRequest> body = ArgumentCaptor.forClass(ChequeRequest.class);
        verify(adminService).updateChequeByStatusId(body.capture(), eq(501L));
        verifyNoMoreInteractions(adminService);
        assertEquals("APPROVED", body.getValue().getStatus());
    }

    // =====================================================================
    // Service failures
    // =====================================================================

    @Test
    @DisplayName("Service exception is not swallowed by the controller (no @ControllerAdvice yet)")
    void serviceException_propagates() {
        when(adminService.updateAcctByStatusId(any(Account.class), eq(999L)))
                .thenThrow(new IllegalStateException("Account not found"));

        Exception thrown = assertThrows(Exception.class, () ->
                mockMvc.perform(put(BASE_URL + "/updateAccount/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}")));

        assertEquals(IllegalStateException.class, thrown.getCause().getClass());
        assertEquals("Account not found", thrown.getCause().getMessage());
        verify(adminService).updateAcctByStatusId(any(Account.class), eq(999L));
    }

    // =====================================================================
    // Invalid requests - rejected by Spring MVC before reaching the service
    // =====================================================================

    @Test
    @DisplayName("Malformed JSON -> 400, service not called")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/addAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailId\": \"priya@bank.com\", "))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Missing request body -> 400, service not called")
    void missingBody_returns400() throws Exception {
        mockMvc.perform(put(BASE_URL + "/updateChequeRequest/{id}", 501L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Non-numeric {id} -> 400, service not called")
    void nonNumericId_returns400() throws Exception {
        mockMvc.perform(put(BASE_URL + "/updateTransaction/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Wrong JSON type for a numeric field -> 400, service not called")
    void wrongFieldType_returns400() throws Exception {
        mockMvc.perform(put(BASE_URL + "/updateFundsTransfer/{id}", 401L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"lots\"}"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Non-JSON content type -> 415, service not called")
    void unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post(BASE_URL + "/addAdmin")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<admin/>"))
                .andExpect(status().isUnsupportedMediaType());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Wrong HTTP method -> 405 with Allow header, service not called")
    void wrongMethod_returns405() throws Exception {
        mockMvc.perform(get(BASE_URL + "/updateCustomer/{id}", 101L))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "PUT"));

        mockMvc.perform(delete(BASE_URL + "/getAccountsByStatus/{status}", "PENDING"))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(adminService);
    }

    @Test
    @DisplayName("Unknown path -> 404, service not called")
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/getAllAdmins"))
                .andExpect(status().isNotFound());

        verifyZeroInteractions(adminService);
    }

    // =====================================================================
    // Cross-origin
    // =====================================================================

    @Test
    @DisplayName("@CrossOrigin(*): any cross-origin caller is allowed")
    void crossOrigin_allowsAnyOrigin() throws Exception {
        when(adminService.getCustomersByStatus("PENDING")).thenReturn(Collections.<Customer>emptyList());

        mockMvc.perform(get(BASE_URL + "/getCustomersByStatus/{status}", "PENDING")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                // Spring 5.1 echoes the caller's origin rather than a literal "*".
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
