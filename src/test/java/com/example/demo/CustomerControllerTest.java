package com.example.demo;

import com.example.demo.controller.CustomerController;
import com.example.demo.model.Account;
import com.example.demo.model.ChequeRequest;
import com.example.demo.model.Customer;
import com.example.demo.model.FundsTransfer;
import com.example.demo.model.Transaction;
import com.example.demo.service.CustomerService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * REST API tests for {@link CustomerController}.
 *
 * <p>Only the web layer is loaded ({@code @WebMvcTest}); {@link CustomerService} is replaced
 * with a Mockito mock. Each test checks three things:
 * <ul>
 *   <li>Response: HTTP status, content type and JSON body</li>
 *   <li>Request handling: routing, path-variable and JSON body binding, invalid input</li>
 *   <li>Service interaction: the right service method is called with the right arguments</li>
 * </ul>
 */
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String BASE = "/Customer";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Customer customer;
    private Account account;
    private Transaction transaction;
    private FundsTransfer transfer;
    private ChequeRequest chequeRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmailId("john.doe@example.com");
        customer.setStatus("APPROVED");

        account = new Account();
        account.setAccountId(1004450001L);
        account.setBalance(2500.5f);
        account.setStatus("ACTIVE");

        transaction = new Transaction();
        transaction.setTransactionId(3L);
        transaction.setAccountNo(1004450001L);
        transaction.setAmount(500.0f);

        transfer = new FundsTransfer();
        transfer.setTransferId(10L);
        transfer.setFromAcctNo(1004450001L);
        transfer.setToAcctNo(1004450002L);
        transfer.setAmount(300.0f);

        chequeRequest = new ChequeRequest();
        chequeRequest.setRequestId(5L);
        chequeRequest.setDescription("New cheque book");
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    // ------------------------------------------------------------------
    // Customer endpoints
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST addCustomer binds the body, calls the service once and returns the saved customer")
    void addCustomer() throws Exception {
        Customer request = new Customer();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmailId("john.doe@example.com");
        when(customerService.addCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post(BASE + "/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.emailId").value("john.doe@example.com"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).addCustomer(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals("John", captor.getValue().getFirstName());
        assertEquals("Doe", captor.getValue().getLastName());
        assertEquals("john.doe@example.com", captor.getValue().getEmailId());
    }

    @Test
    @DisplayName("GET getCustomerIds returns the id list from the service")
    void getCustomerIds() throws Exception {
        when(customerService.getCustomerIds()).thenReturn(Arrays.asList(1L, 2L, 3L));

        mockMvc.perform(get(BASE + "/getCustomerIds"))
                .andExpect(status().isOk())
                .andExpect(content().json("[1,2,3]"));

        verify(customerService).getCustomerIds();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getCustomers returns all customers")
    void getCustomers() throws Exception {
        Customer second = new Customer();
        second.setCustomerId(2L);
        second.setFirstName("Jane");
        when(customerService.getCustomers()).thenReturn(Arrays.asList(customer, second));

        mockMvc.perform(get(BASE + "/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(customerService).getCustomers();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getCustomers returns an empty array when there are no customers")
    void getCustomers_empty() throws Exception {
        when(customerService.getCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE + "/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customerService).getCustomers();
    }

    @Test
    @DisplayName("PUT updateCustomer passes the body and path id to the service")
    void updateCustomer() throws Exception {
        Customer request = new Customer();
        request.setFirstName("Johnny");
        Customer updated = new Customer();
        updated.setCustomerId(1L);
        updated.setFirstName("Johnny");
        when(customerService.updateCustomer(any(Customer.class), eq(1L))).thenReturn(updated);

        mockMvc.perform(put(BASE + "/updateCustomer/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.firstName").value("Johnny"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService).updateCustomer(captor.capture(), eq(1L));
        verifyNoMoreInteractions(customerService);
        assertEquals("Johnny", captor.getValue().getFirstName());
    }

    @Test
    @DisplayName("GET getUserByEmail returns matching customers")
    void getUserByEmail() throws Exception {
        when(customerService.getUserByEmail("john.doe@example.com"))
                .thenReturn(Collections.singletonList(customer));

        mockMvc.perform(get(BASE + "/getUserByEmail/{emailId}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].emailId").value("john.doe@example.com"));

        verify(customerService).getUserByEmail("john.doe@example.com");
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getUserByEmail returns an empty array when nothing matches")
    void getUserByEmail_noMatch() throws Exception {
        when(customerService.getUserByEmail("missing@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE + "/getUserByEmail/{emailId}", "missing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService).getUserByEmail("missing@example.com");
    }

    // ------------------------------------------------------------------
    // Account endpoints
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST addAccount binds the body and returns the saved account")
    void addAccount() throws Exception {
        Account request = new Account();
        request.setBalance(2500.5f);
        when(customerService.addAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post(BASE + "/addAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001L))
                .andExpect(jsonPath("$.balance").value(2500.5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(customerService).addAccount(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(2500.5f, captor.getValue().getBalance());
    }

    @Test
    @DisplayName("GET findAllAccounts returns every account")
    void findAllAccounts() throws Exception {
        when(customerService.findAllAccounts()).thenReturn(Collections.singletonList(account));

        mockMvc.perform(get(BASE + "/findAllAccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountId").value(1004450001L));

        verify(customerService).findAllAccounts();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getAccountByCustomer passes the customer id and returns that customer's accounts")
    void getAccountByCustomer() throws Exception {
        when(customerService.getAccountByCustomer(1L)).thenReturn(Collections.singletonList(account));

        mockMvc.perform(get(BASE + "/getAccountByCustomer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1004450001L));

        verify(customerService).getAccountByCustomer(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getAccountByCustomer returns an empty array for a customer with no accounts")
    void getAccountByCustomer_none() throws Exception {
        when(customerService.getAccountByCustomer(42L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE + "/getAccountByCustomer/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService).getAccountByCustomer(42L);
    }

    @Test
    @DisplayName("GET findAllAccountIds returns the account id list")
    void findAllAccountIds() throws Exception {
        when(customerService.findAllAccountIds()).thenReturn(Arrays.asList(1004450001L, 1004450002L));

        mockMvc.perform(get(BASE + "/findAllAccountIds"))
                .andExpect(status().isOk())
                .andExpect(content().json("[1004450001,1004450002]"));

        verify(customerService).findAllAccountIds();
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getAccountIdsByCustomer passes the customer id and returns the account ids")
    void getAccountIdsByCustomer() throws Exception {
        when(customerService.getAccountIdsByCustomer(1L)).thenReturn(Collections.singletonList(1004450001L));

        mockMvc.perform(get(BASE + "/getAccountIdsByCustomer/{customerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("[1004450001]"));

        verify(customerService).getAccountIdsByCustomer(1L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getAccountById returns the account when it exists")
    void getAccountById_found() throws Exception {
        when(customerService.getAccountById(1004450001L)).thenReturn(Optional.of(account));

        mockMvc.perform(get(BASE + "/getAccountById/{id}", 1004450001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1004450001L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(customerService).getAccountById(1004450001L);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    @DisplayName("GET getAccountById returns 200 with body 'null' when the account is missing (current behaviour)")
    void getAccountById_notFound() throws Exception {
        when(customerService.getAccountById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE + "/getAccountById/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));

        verify(customerService).getAccountById(999L);
    }

    // ------------------------------------------------------------------
    // Money movement endpoints
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST depositAccount passes account number and amount to the service")
    void depositAccount() throws Exception {
        Transaction request = new Transaction();
        request.setAccountNo(1004450001L);
        request.setAmount(500.0f);
        when(customerService.depositAccount(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post(BASE + "/depositAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3))
                .andExpect(jsonPath("$.accountNo").value(1004450001L))
                .andExpect(jsonPath("$.amount").value(500.0));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(customerService).depositAccount(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(1004450001L, captor.getValue().getAccountNo().longValue());
        assertEquals(500.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("POST withdrawAccount calls withdrawAccount, never depositAccount")
    void withdrawAccount() throws Exception {
        Transaction request = new Transaction();
        request.setAccountNo(1004450001L);
        request.setAmount(200.0f);
        when(customerService.withdrawAccount(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post(BASE + "/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(customerService).withdrawAccount(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(1004450001L, captor.getValue().getAccountNo().longValue());
        assertEquals(200.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("POST transferAccounts maps to transferFunds and passes both account numbers")
    void transferAccounts() throws Exception {
        FundsTransfer request = new FundsTransfer();
        request.setFromAcctNo(1004450001L);
        request.setToAcctNo(1004450002L);
        request.setAmount(300.0f);
        when(customerService.transferFunds(any(FundsTransfer.class))).thenReturn(transfer);

        mockMvc.perform(post(BASE + "/transferAccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(10))
                .andExpect(jsonPath("$.fromAcctNo").value(1004450001L))
                .andExpect(jsonPath("$.toAcctNo").value(1004450002L))
                .andExpect(jsonPath("$.amount").value(300.0));

        ArgumentCaptor<FundsTransfer> captor = ArgumentCaptor.forClass(FundsTransfer.class);
        verify(customerService).transferFunds(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals(1004450001L, captor.getValue().getFromAcctNo().longValue());
        assertEquals(1004450002L, captor.getValue().getToAcctNo().longValue());
        assertEquals(300.0f, captor.getValue().getAmount());
    }

    @Test
    @DisplayName("POST chequeRequest maps to requestChequeBook and returns the saved request")
    void chequeRequest() throws Exception {
        ChequeRequest request = new ChequeRequest();
        request.setDescription("New cheque book");
        when(customerService.requestChequeBook(any(ChequeRequest.class))).thenReturn(chequeRequest);

        mockMvc.perform(post(BASE + "/chequeRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(5))
                .andExpect(jsonPath("$.description").value("New cheque book"));

        ArgumentCaptor<ChequeRequest> captor = ArgumentCaptor.forClass(ChequeRequest.class);
        verify(customerService).requestChequeBook(captor.capture());
        verifyNoMoreInteractions(customerService);
        assertEquals("New cheque book", captor.getValue().getDescription());
    }

    // ------------------------------------------------------------------
    // Invalid requests: rejected by Spring MVC before the service is called
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Malformed JSON body returns 400 and the service is not called")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/depositAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\": 1004450001, \"amount\": "))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Wrong JSON type for a numeric field returns 400 and the service is not called")
    void wrongFieldType_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/withdrawAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNo\": \"not-a-number\", \"amount\": 10}"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Missing request body returns 400 and the service is not called")
    void missingBody_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/transferAccounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Non-numeric id path variable returns 400 and the service is not called")
    void nonNumericId_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/getAccountById/{id}", "abc"))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Non-JSON content type returns 415 and the service is not called")
    void unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post(BASE + "/chequeRequest")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("New cheque book"))
                .andExpect(status().isUnsupportedMediaType());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("GET on a POST endpoint returns 405 and the service is not called")
    void getOnPostEndpoint_returns405() throws Exception {
        mockMvc.perform(get(BASE + "/addCustomer"))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("POST on a GET endpoint returns 405 and the service is not called")
    void postOnGetEndpoint_returns405() throws Exception {
        mockMvc.perform(post(BASE + "/getCustomers"))
                .andExpect(status().isMethodNotAllowed());

        verifyZeroInteractions(customerService);
    }

    @Test
    @DisplayName("Unknown path returns 404 and the service is not called")
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get(BASE + "/deleteCustomer/{id}", 1L))
                .andExpect(status().isNotFound());

        verifyZeroInteractions(customerService);
    }
}
