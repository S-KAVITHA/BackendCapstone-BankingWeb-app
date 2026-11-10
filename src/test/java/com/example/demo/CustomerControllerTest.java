package com.example.demo;

import com.example.demo.controller.CustomerController;
import com.example.demo.model.Account;
import com.example.demo.model.ChequeRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST API tests for {@link CustomerController}.
 *
 * <p>{@code @WebMvcTest} starts only the Spring MVC layer (routing, JSON conversion,
 * argument binding). {@link CustomerService} is replaced by a Mockito mock, so no database
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
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String BASE_URL = "/Customer";
    private static final long ACCT_1 = 5000100001L;
    private static final long ACCT_2 = 5000100002L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    // ---------------------------------------------------------------------
    // Test data builders
    // ---------------------------------------------------------------------

    private static Customer customer(Long id, String firstName, String email) {
        Customer c = new Customer();
        c.setCustomerId(id);
        c.setFirstName(firstName);
        c.setLastName("Kumar");
        c.setEmailId(email);
        c.setCity("Chennai");
        c.setZipcode(600001L);
        c.setStatus("APPROVED");
        return c;
    }

    private static Account account(Long id, float balance) {
        Account a = new Account();
        a.setAccountId(id);
        a.setCustName("Arun Kumar");
        a.setAccttype("SAVINGS");
        a.setCurrency("INR");
        a.setBalance(balance);
        a.setStatus("ACTIVE");
        return a;
    }

    private static Transaction transaction(Long id, String type, float amount) {
        Transaction t = new Transaction();
        t.setTransactionId(id);
        t.setAccountNo(ACCT_1);
        t.setTxntype(type);
        t.setAmount(amount);
        t.setStatus("PENDING");
        return t;
    }

    private String toJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    // =====================================================================
    // Customer endpoints
    // =====================================================================

    @Test
    @DisplayName("addCustomer: body bound and passed to the service once; saved customer returned")
    void addCustomer() throws Exception {
        when(customerService.addCustomer(any(Customer.class)))
                .thenReturn(customer(1L, "Arun", "arun@example.com"));

        String body = "{\"firstName\":\"Arun\",\"lastName\":\"Kumar\",\"emailId\":\"arun@example.com\","
                + "\"city\":\"Chennai\",\"zipcode\":600001,\"agree\":true}";

        mockMvc.perform(post(BASE_URL + "/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.firstName").value("Arun"))
                .andExpect(jsonPath("$.emailId").value("arun@example.com"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).addCustomer(captor.capture());
        verifyNoMoreInteractions(customerService);

        Customer bound = captor.getValue();
        assertNull(bound.getCustomerId());
        assertEquals("Arun", bound.getFirstName());
        assertEquals("Kumar", bound.getLastName());
        assertEquals("arun@example.com", bound.getEmailId());
        assertEquals("Chennai", bound.getCity());
        assertEquals(600001L, bound.getZipcode());
    }

    @Test
    @DisplayName("addCustomer: @JsonIgnore collections are not exposed in the response")
    void addCustomer_hidesIgnoredFields() throws Exception {
        when(customerService.addCustomer(any(Customer.class)))
                .thenReturn(customer(1L, "Arun", "arun@example.com"));

        mockMvc.perform(post(BASE_URL + "/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Arun\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").doesNotExist())
                .andExpect(jsonPath("$.transactions").doesNotExist())
                .andExpect(jsonPath("$.requests").doesNotExist());
    }

    @Test
    @DisplayName("getCustomerIds: returns the id list from the service")
    void getCustomerIds() throws Exception {
        when(customerService.getCustomerIds()).thenReturn(Arrays.asList(1L, 2L, 3L));

        mockMvc.perform(get(BASE_URL + "/getCustomerIds"))
                .andExpect(status().isOk())
                .andExpect(content().json("[1,2,3]"));

        verify(customerService).getCustomerIds();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getCustomers: returns all customers in service order")
    void getCustomers() throws Exception {
        when(customerService.getCustomers()).thenReturn(Arrays.asList(
                customer(1L, "Arun", "arun@example.com"),
                customer(2L, "Divya", "divya@example.com")));

        mockMvc.perform(get(BASE_URL + "/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Arun"))
                .andExpect(jsonPath("$[1].firstName").value("Divya"));

        verify(customerService).getCustomers();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getCustomers: 200 with [] when there are none")
    void getCustomers_empty() throws Exception {
        when(customerService.getCustomers()).thenReturn(Collections.<Customer>emptyList());

        mockMvc.perform(get(BASE_URL + "/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customerService).getCustomers();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("updateCustomer: body and path id passed to the service; updated customer returned")
    void updateCustomer() throws Exception {
        when(customerService.updateCustomer(any(Customer.class), eq(1L)))
                .thenReturn(customer(1L, "Arun", "arun.new@example.com"));

        mockMvc.perform(put(BASE_URL + "/updateCustomer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailId\":\"arun.new@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.emailId").value("arun.new@example.com"));

        ArgumentCaptor<Customer> body = ArgumentCaptor.forClass(Customer.class);
        verify(customerService).updateCustomer(body.capture(), eq(1L));
        verifyNoMoreInteractions(customerService);
        assertEquals("arun.new@example.com", body.getValue().getEmailId());
        // Fields not sent stay null - the controller does not fill them in.
        assertNull(body.getValue().getFirstName());
        assertNull(body.getValue().getCustomerId());
    }

    @Test
    @DisplayName("getUserByEmail: path variable passed to the service; matches returned")
    void getUserByEmail() throws Exception {
        when(customerService.getUserByEmail("arun@example"))
                .thenReturn(Collections.singletonList(customer(1L, "Arun", "arun@example")));

        // No dot in the email: Spring MVC would otherwise treat ".com" as a file extension.
        mockMvc.perform(get(BASE_URL + "/getUserByEmail/{emailId}", "arun@example"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].emailId").value("arun@example"));

        verify(customerService).getUserByEmail("arun@example");
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getUserByEmail: 200 with [] when no customer matches")
    void getUserByEmail_noMatch() throws Exception {
        when(customerService.getUserByEmail("nobody")).thenReturn(Collections.<Customer>emptyList());

        mockMvc.perform(get(BASE_URL + "/getUserByEmail/{emailId}", "nobody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService).getUserByEmail("nobody");
        verifyNoMoreInteractions(customerService);
    }

    // =====================================================================
    // Account endpoints
    // =====================================================================

    @Test
    @DisplayName("addAccount: body bound (incl. nested customer) and saved account returned")
    void addAccount() throws Exception {
        when(customerService.addAccount(any(Account.class))).thenReturn(account(ACCT_1, 5000.0f));

        String body = "{\"customerId\":{\"customerId\":1},\"accttype\":\"SAVINGS\","
                + "\"currency\":\"INR\",\"balance\":5000.0}";

        mockMvc.perform(post(BASE_URL + "/addAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(ACCT_1))
                .andExpect(jsonPath("$.balance").value(5000.0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(customerService).addAccount(captor.capture());
        verifyNoMoreInteractions(customerService);
        Account bound = captor.getValue();
        assertEquals(Long.valueOf(1L), bound.getCustomerId().getCustomerId());
        assertEquals("SAVINGS", bound.getAccttype());
        assertEquals("INR", bound.getCurrency());
        assertEquals(5000.0f, bound.getBalance());
    }

    @Test
    @DisplayName("findAllAccounts: returns every account")
    void findAllAccounts() throws Exception {
        when(customerService.findAllAccounts())
                .thenReturn(Arrays.asList(account(ACCT_1, 100f), account(ACCT_2, 200f)));

        mockMvc.perform(get(BASE_URL + "/findAllAccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountId").value(ACCT_1))
                .andExpect(jsonPath("$[1].accountId").value(ACCT_2));

        verify(customerService).findAllAccounts();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getAccountByCustomer: customer id path variable passed as Long; accounts returned")
    void getAccountByCustomer() throws Exception {
        when(customerService.getAccountByCustomer(1L))
                .thenReturn(Collections.singletonList(account(ACCT_1, 100f)));

        mockMvc.perform(get(BASE_URL + "/getAccountByCustomer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountId").value(ACCT_1));

        verify(customerService).getAccountByCustomer(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getAccountByCustomer: 200 with [] for a customer with no accounts")
    void getAccountByCustomer_none() throws Exception {
        when(customerService.getAccountByCustomer(77L)).thenReturn(Collections.<Account>emptyList());

        mockMvc.perform(get(BASE_URL + "/getAccountByCustomer/{id}", 77L))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customerService).getAccountByCustomer(77L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("findAllAccountIds: returns the account id list")
    void findAllAccountIds() throws Exception {
        when(customerService.findAllAccountIds()).thenReturn(Arrays.asList(ACCT_1, ACCT_2));

        mockMvc.perform(get(BASE_URL + "/findAllAccountIds"))
                .andExpect(status().isOk())
                .andExpect(content().json("[5000100001,5000100002]"));

        verify(customerService).findAllAccountIds();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getAccountIdsByCustomer: customer id passed; account ids returned")
    void getAccountIdsByCustomer() throws Exception {
        when(customerService.getAccountIdsByCustomer(1L)).thenReturn(Collections.singletonList(ACCT_1));

        mockMvc.perform(get(BASE_URL + "/getAccountIdsByCustomer/{customerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[5000100001]"));

        verify(customerService).getAccountIdsByCustomer(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getAccountById: Optional is unwrapped and the account returned as a JSON object")
    void getAccountById_found() throws Exception {
        when(customerService.getAccountById(ACCT_1)).thenReturn(Optional.of(account(ACCT_1, 900f)));

        mockMvc.perform(get(BASE_URL + "/getAccountById/{id}", ACCT_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(ACCT_1))
                .andExpect(jsonPath("$.balance").value(900.0));

        verify(customerService).getAccountById(ACCT_1);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("getAccountById: missing account -> 200 with body 'null' (documents current behaviour, not 404)")
    void getAccountById_notFound() throws Exception {
        when(customerService.getAccountById(anyLong())).thenReturn(Optional.<Account>empty());

        mockMvc.perform(get(BASE_URL + "/getAccountById/{id}", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));

        verify(customerService).getAccountById(123L);
        verifyNoMoreInteractions(customerService);
    }

    // =====================================================================
    // Money movement endpoints
    // =====================================================================

    @Test
    @DisplayName("depositAccount: calls depositAccount (never withdraw) with account number and amount")
    void depositAccount() throws Exception {
        when(customerService.depositAccount(any(Transaction.class)))
                .thenReturn(transaction(10L, "DEPOSIT", 500f));

        mockMvc.perform(post(BASE_URL + "/depositAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\":5000100001,\"amount\":500.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(10))
                .andExpect(jsonPath("$.txntype").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(500.0));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(customerService).depositAccount(captor.capture());
        verify(customerService, never()).withdrawAccount(any(Transaction.class));
        verifyNoMoreInteractions(customerService);
        assertEquals(Long.valueOf(ACCT_1), captor.getValue().getAccountNo());
        assertEquals(500.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("withdrawAccount: calls withdrawAccount (never deposit) with account number and amount")
    void withdrawAccount() throws Exception {
        when(customerService.withdrawAccount(any(Transaction.class)))
                .thenReturn(transaction(11L, "WITHDRAW", 200f));

        mockMvc.perform(post(BASE_URL + "/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\":5000100001,\"amount\":200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(11))
                .andExpect(jsonPath("$.txntype").value("WITHDRAW"));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(customerService).withdrawAccount(captor.capture());
        verify(customerService, never()).depositAccount(any(Transaction.class));
        verifyNoMoreInteractions(customerService);
        assertEquals(Long.valueOf(ACCT_1), captor.getValue().getAccountNo());
        assertEquals(200.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("withdrawAccount: service failure (e.g. insufficient funds) is not swallowed")
    void withdrawAccount_serviceThrows() {
        when(customerService.withdrawAccount(any(Transaction.class)))
                .thenThrow(new IllegalArgumentException("Insufficient balance"));

        Exception thrown = assertThrows(Exception.class, () ->
                mockMvc.perform(post(BASE_URL + "/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\":5000100001,\"amount\":999999.0}")));

        assertEquals(IllegalArgumentException.class, thrown.getCause().getClass());
        assertEquals("Insufficient balance", thrown.getCause().getMessage());
        verify(customerService).withdrawAccount(any(Transaction.class));
    }

    @Test
    @DisplayName("transferAccounts: maps to transferFunds and passes from/to accounts and amount")
    void transferAccounts() throws Exception {
        FundsTransfer saved = new FundsTransfer();
        saved.setTransferId(20L);
        saved.setFromAcctNo(ACCT_1);
        saved.setToAcctNo(ACCT_2);
        saved.setAmount(1500f);
        saved.setStatus("PENDING");
        when(customerService.transferFunds(any(FundsTransfer.class))).thenReturn(saved);

        mockMvc.perform(post(BASE_URL + "/transferAccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAcctNo\":5000100001,\"toAcctNo\":5000100002,\"amount\":1500.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(20))
                .andExpect(jsonPath("$.fromAcctNo").value(ACCT_1))
                .andExpect(jsonPath("$.toAcctNo").value(ACCT_2))
                .andExpect(jsonPath("$.amount").value(1500.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        ArgumentCaptor<FundsTransfer> captor = ArgumentCaptor.forClass(FundsTransfer.class);
        verify(customerService).transferFunds(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(Long.valueOf(ACCT_1), captor.getValue().getFromAcctNo());
        assertEquals(Long.valueOf(ACCT_2), captor.getValue().getToAcctNo());
        assertEquals(1500.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("chequeRequest: maps to requestChequeBook; saved request returned")
    void chequeRequest() throws Exception {
        ChequeRequest saved = new ChequeRequest();
        saved.setRequestId(30L);
        saved.setAccountNo(ACCT_1);
        saved.setDescription("25-leaf cheque book");
        saved.setStatus("PENDING");
        when(customerService.requestChequeBook(any(ChequeRequest.class))).thenReturn(saved);

        mockMvc.perform(post(BASE_URL + "/chequeRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\":5000100001,\"description\":\"25-leaf cheque book\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(30))
                .andExpect(jsonPath("$.description").value("25-leaf cheque book"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        ArgumentCaptor<ChequeRequest> captor = ArgumentCaptor.forClass(ChequeRequest.class);
        verify(customerService).requestChequeBook(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(Long.valueOf(ACCT_1), captor.getValue().getAccountNo());
        assertEquals("25-leaf cheque book", captor.getValue().getDescription());
    }

    // =====================================================================
    // Invalid requests - rejected by Spring MVC before reaching the service
    // =====================================================================

    @Test
    @DisplayName("Malformed JSON -> 400, service not called")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/depositAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\":5000100001,\"amount\":"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Wrong JSON type for a numeric field -> 400, service not called")
    void wrongFieldType_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/transferAccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAcctNo\":\"abc\",\"toAcctNo\":5000100002,\"amount\":10}"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Missing request body -> 400, service not called")
    void missingBody_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Non-numeric {id} -> 400, service not called")
    void nonNumericId_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/getAccountByCustomer/{id}", "one"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(BASE_URL + "/updateCustomer/{id}", "x1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Non-JSON content type -> 415, service not called")
    void unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post(BASE_URL + "/addCustomer")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Arun Kumar"))
                .andExpect(status().isUnsupportedMediaType());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Wrong HTTP method -> 405, service not called")
    void wrongMethod_returns405() throws Exception {
        mockMvc.perform(get(BASE_URL + "/depositAccount"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "POST"));

        mockMvc.perform(post(BASE_URL + "/findAllAccounts"))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Unknown path -> 404, service not called")
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/closeAccount/{id}", ACCT_1))
                .andExpect(status().isNotFound());

        verifyZeroInteractions(customerService);
    }

    // =====================================================================
    // Cross-origin
    // =====================================================================

    @Test
    @DisplayName("@CrossOrigin(*): any cross-origin caller is allowed")
    void crossOrigin_allowsAnyOrigin() throws Exception {
        when(customerService.findAllAccountIds()).thenReturn(Collections.<Long>emptyList());

        mockMvc.perform(get(BASE_URL + "/findAllAccountIds")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                // Spring 5.1 echoes the caller's origin rather than a literal "*".
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
