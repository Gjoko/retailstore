package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.RetailstoreApplication;
import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.repositories.ItemsRepository;
import com.gjoko.retailstore.persistence.repositories.UsersRepository;
import com.gjoko.retailstore.rest.entity.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.gjoko.retailstore.helper.ConvertorHelper.asBill;
import static com.gjoko.retailstore.helper.ConvertorHelper.asJsonString;
import static com.gjoko.retailstore.helper.TestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {RetailstoreApplication.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BillsControllerImplIT {

  @Autowired private WebApplicationContext context;

  @Autowired private UsersRepository usersRepository;

  @Autowired private ItemsRepository itemsRepository;

  private UserDto employeeDto;
  private UserDto customerDto;
  private UserDto affiliateDto;
  private UserDto oldCustomerDto;
  private Bill biggerThan100bill;
  private Bill smallerThan100bill;
  private Bill onlyGroceriesBill;

  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    setUp();
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  // authorization tests

  @Test
  void givenUserNotExist_whenInvokeSave_thenReturn401() throws Exception {
    mvc.perform(post("/api/bills").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserNotExist_whenInvokeFindById_thenReturn401() throws Exception {
    mvc.perform(get("/api/bills/123").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserNotExist_whenInvokeCalculateDiscount_thenReturn401() throws Exception {
    mvc.perform(
            post("/api/bills/calculateDiscount/123")
                .with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserExists_whenInvokeSave_thenReturn201() throws Exception {

    biggerThan100bill.setUserId(affiliateDto.getId());

    mvc.perform(
            post("/api/bills")
                .content(asJsonString(biggerThan100bill))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isCreated());
  }

  @Test
  void givenUserExists_whenInvokeFindById_thenBillIsFound() throws Exception {

    biggerThan100bill.setUserId(employeeDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(biggerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(employeeDto.getId(), createdBill.getUserId());

    MockHttpServletResponse findByIdResponse =
        mvc.perform(
                get("/api/bills/" + createdBill.getId()).with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();
    assertEquals(200, findByIdResponse.getStatus());
    Bill fetchedBill = asBill(findByIdResponse.getContentAsByteArray());
    assertTrue(new ReflectionEquals(fetchedBill).matches(createdBill));
  }

  @Test
  void givenUserExists_whenInvokeCalculateDiscount_thenBillIsNotFound() throws Exception {
    mvc.perform(get("/api/bills/123").with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andExpect(status().isNotFound());
  }

  @Test
  void givenUserExists_whenInvokeFindById_thenBillIsNotFound() throws Exception {
    mvc.perform(get("/api/bills/calculateDiscount/123").with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andExpect(status().isNotFound());
  }

  // test discounts

  /*
   mango 100 x 6 = 600
   bbq 600 x 1 = 600
   jeans 60 x 5 = 300
   chainsaw 250 x 2 = 500
   Groceries: + 600
   Non-groceries: + 1400
   Employee-discount: - 420
   5-dollar discount - 75
   After discounts : 1505
  */
  @Test
  void givenEmployeeAndBillExist_whenInvokeCalculateBill_thenReturnDiscountedPrice()
      throws Exception {
    biggerThan100bill.setUserId(employeeDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(biggerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(employeeDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(1505d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 6 = 600
   bbq 600 x 1 = 600
   jeans 60 x 5 = 300
   chainsaw 250 x 2 = 500
   Groceries: 600
   Non-groceries: 1400
   Affiliate-discount: 140
   5-dollar discount 90
   After discounts 1770
  */
  @Test
  void givenAffiliateAndBillExist_whenInvokeCalculateBill_thenReturnDiscountedPrice()
      throws Exception {
    biggerThan100bill.setUserId(affiliateDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(biggerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(affiliateDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(1770d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 6 = 600
   bbq 600 x 1 = 600
   jeans 60 x 5 = 300
   chainsaw 250 x 2 = 500
   Groceries: + 600
   Non-groceries: + 1400
   Older-than-2-years-Employee discount: - 70
   5-dollar discount -95
   After discounts : 1835
  */
  @Test
  void givenOlderThan2YearsUserAndBillExist_whenInvokeCalculateBill_thenReturnDiscountedPrice()
      throws Exception {
    biggerThan100bill.setUserId(oldCustomerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(biggerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(oldCustomerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(1835d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 6 = 600
   bbq 600 x 1 = 600
   jeans 60 x 5 = 300
   chainsaw 250 x 2 = 500
   Groceries: + 600
   Non-groceries: + 1400
   5-dollar discount 100
   After discounts : 1900
  */

  @Test
  void givenYoungerThan2YearsUserAndBillExist_whenInvokeCalculateBill_thenReturnDiscountedPrice()
      throws Exception {
    biggerThan100bill.setUserId(customerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(biggerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(customerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(1900d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   pen 5 x 2 = 10
   notebook 20 x 2 = 40
   cucumber 2 x 5 = 10
   Groceries: + 10
   Non-groceries: + 50
   Employee-discount: - 15
   5-dollar discount 0
   After discounts : 45
  */
  @Test
  void givenEmployee_whenInvokeCalculateBillSmallerThan100Dollars_thenReturnDiscountedPrice()
      throws Exception {
    smallerThan100bill.setUserId(employeeDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(smallerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(employeeDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(45, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   pen 5 x 2 = 10
   notebook 20 x 2 = 40
   cucumber 2 x 5 = 10
   Groceries: + 10
   Non-groceries: + 50
   affiliate-discount: - 5
   5-dollar discount 0
   After discounts : 55
  */
  @Test
  void givenAffiliate_whenInvokeCalculateBillSmallerThan100Dollars_thenReturnDiscountedPrice()
      throws Exception {
    smallerThan100bill.setUserId(affiliateDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(smallerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(affiliateDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(55d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   pen 5 x 2 = 10
   notebook 20 x 2 = 40
   cucumber 2 x 5 = 10
   Groceries: + 10
   Non-groceries: + 50
   Older-than-2-years-customere discount: - 2.5
   5-dollar discount 0
   After discounts : 57.5
  */
  @Test
  void
      givenCustomerOlderThan2Years_whenInvokeCalculateBillSmallerThan100Dollars_thenReturnDiscountedPrice()
          throws Exception {
    smallerThan100bill.setUserId(oldCustomerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(smallerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(oldCustomerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(57.5d, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   pen 5 x 2 = 10
   notebook 20 x 2 = 40
   cucumber 2 x 5 = 10
   Groceries: + 10
   Non-groceries: + 50
   Older-than-2-years-customer discount: - 2.5
   5-dollar discount 0
   After discounts : 57.5
  */
  @Test
  void
      givenCustomerYoungerThan2Years_whenInvokeCalculateBillSmallerThan100Dollars_thenReturnDiscountedPrice()
          throws Exception {
    smallerThan100bill.setUserId(customerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(smallerThan100bill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(customerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(60, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 2 = 200
   cucumber 2 x 10 = 20
   Groceries: + 220
   Non-groceries: + 0
   Employee discount: 0
   5-dollar discount -10
   After discounts : 210
  */
  @Test
  void givenEmployee_whenInvokeOnlyGroceriesBill_thenReturnDiscountedPrice() throws Exception {
    onlyGroceriesBill.setUserId(employeeDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(onlyGroceriesBill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(employeeDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(210, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 2 = 200
   cucumber 2 x 10 = 20
   Groceries: + 220
   Non-groceries: + 0
   Affiliate discount: 0
   5-dollar discount -10
   After discounts : 210
  */
  @Test
  void givenAffiliate_whenInvokeOnlyGroceriesBill_thenReturnDiscountedPrice() throws Exception {
    onlyGroceriesBill.setUserId(affiliateDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(onlyGroceriesBill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(affiliateDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(210, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 2 = 200
   cucumber 2 x 10 = 20
   Groceries: + 220
   Non-groceries: + 0
   Older-than-2-years-customer discount: 0
   5-dollar discount -10
   After discounts : 210
  */
  @Test
  void givenOlderThanTwoYears_whenInvokeOnlyGroceriesBill_thenReturnDiscountedPrice()
      throws Exception {
    onlyGroceriesBill.setUserId(oldCustomerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(onlyGroceriesBill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(oldCustomerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(210, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  /*
   mango 100 x 2 = 200
   cucumber 2 x 10 = 20
   Groceries: + 220
   Non-groceries: + 0
   New-customer-discount discount: 0
   5-dollar discount -10
   After discounts : 210
  */
  @Test
  void givenYoungerThanTwoYears_whenInvokeOnlyGroceriesBill_thenReturnDiscountedPrice()
      throws Exception {
    onlyGroceriesBill.setUserId(customerDto.getId());

    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/bills")
                    .content(asJsonString(onlyGroceriesBill))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    Bill createdBill = asBill(createdItemResponse.getContentAsByteArray());
    assertEquals(customerDto.getId(), createdBill.getUserId());

    MockHttpServletResponse calculateDiscountResponse =
        mvc.perform(
                get("/api/bills/calculateDiscount/" + createdBill.getId())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(200, calculateDiscountResponse.getStatus());
    assertEquals(210, Double.parseDouble(calculateDiscountResponse.getContentAsString()));
  }

  private void setUp() {

    // create users
    if (usersRepository.findByUsername("EMPLOYEE") == null) {
      employeeDto = usersRepository.save(createUserRoleEmployee());
    } else {
      employeeDto = usersRepository.findByUsername("EMPLOYEE");
    }

    if (usersRepository.findByUsername("AFFILIATE") == null) {
      affiliateDto = usersRepository.save(createUserRoleAffiliate());
    } else {
      affiliateDto = usersRepository.findByUsername("AFFILIATE");
    }

    if (usersRepository.findByUsername("CUSTOMER") == null) {
      customerDto = usersRepository.save(createUserRoleCustomer());
    } else {
      customerDto = usersRepository.findByUsername("CUSTOMER");
    }

    if (usersRepository.findByUsername("OLD_CUSTOMER") == null) {
      oldCustomerDto = usersRepository.save(createOldUserRoleCustomer());
    } else {
      oldCustomerDto = usersRepository.findByUsername("OLD_CUSTOMER");
    }

    // create products

    ItemDto mango =
        itemsRepository.findByName("mango") == null
            ? itemsRepository.save(createMangoItem())
            : itemsRepository.findByName("mango");

    ItemDto bbq =
        itemsRepository.findByName("BBQ") == null
            ? itemsRepository.save(createBBQItem())
            : itemsRepository.findByName("BBQ");

    ItemDto chainsaw =
        itemsRepository.findByName("Chainsaw") == null
            ? itemsRepository.save(createChainsawItem())
            : itemsRepository.findByName("Chainsaw");

    ItemDto jeans =
        itemsRepository.findByName("jeans") == null
            ? itemsRepository.save(createJeansDtoItem())
            : itemsRepository.findByName("jeans");

    ItemDto pen =
        itemsRepository.findByName("pen") == null
            ? itemsRepository.save(createPenItem())
            : itemsRepository.findByName("pen");

    ItemDto notebook =
        itemsRepository.findByName("notebook") == null
            ? itemsRepository.save(createNotebookItem())
            : itemsRepository.findByName("notebook");

    ItemDto cucumber =
        itemsRepository.findByName("cucumber") == null
            ? itemsRepository.save(createCucumberItem())
            : itemsRepository.findByName("cucumber");

    // create bill
    biggerThan100bill = new Bill();
    Map<String, Integer> items = new HashMap<>();
    items.put(mango.getId(), 6);
    items.put(bbq.getId(), 1);
    items.put(chainsaw.getId(), 2);
    items.put(jeans.getId(), 5);
    biggerThan100bill.setItems(items);

    smallerThan100bill = new Bill();
    Map<String, Integer> smallItems = new HashMap<>();
    smallItems.put(pen.getId(), 2);
    smallItems.put(notebook.getId(), 2);
    smallItems.put(cucumber.getId(), 5);
    smallerThan100bill.setItems(smallItems);

    onlyGroceriesBill = new Bill();
    Map<String, Integer> onlyGroceriesItems = new HashMap<>();
    onlyGroceriesItems.put(mango.getId(), 2);
    onlyGroceriesItems.put(cucumber.getId(), 10);
    onlyGroceriesBill.setItems(onlyGroceriesItems);
  }
}
