package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.RetailstoreApplication;
import com.gjoko.retailstore.persistence.enums.ItemType;
import com.gjoko.retailstore.persistence.repositories.UsersRepository;
import com.gjoko.retailstore.rest.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.gjoko.retailstore.helper.ConvertorHelper.asItem;
import static com.gjoko.retailstore.helper.ConvertorHelper.asJsonString;
import static com.gjoko.retailstore.helper.TestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {RetailstoreApplication.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemsControllerImplIT {

  @Autowired private WebApplicationContext context;

  @Autowired private UsersRepository usersRepository;

  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    setUp();

    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  // authorization tests

  @Test
  void givenUserNotExists_whenInvokeFindById_thenReturn401() throws Exception {
    mvc.perform(get("/api/items/123").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserNotExists_whenInvokeSave_thenReturn401() throws Exception {
    mvc.perform(post("/api/items").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserCredentials_whenInvokeUserAuthorizedEndPointFindById_thenReturn404()
      throws Exception {
    mvc.perform(get("/api/items/non_existing_id").with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenUserCredentials_whenInvokeUserAuthorizedEndPointSave_thenReturn201() throws Exception {
    mvc.perform(
            post("/api/items")
                .content(asJsonString(createShirtItem()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isCreated());
  }

  @Test
  void givenAdminCredentials_whenInvokeUserAuthorizedEndPointFindById_thenReturn404()
      throws Exception {
    mvc.perform(get("/api/items/non_existing_id").with(httpBasic("ROOT", "ROOT")))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenAdminCredentials_whenInvokeUserAuthorizedEndPointSave_thenReturn201() throws Exception {
    mvc.perform(
            post("/api/items")
                .content(asJsonString(createShirtItem()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("ROOT", "ROOT")))
        .andExpect(status().isCreated());
  }

  // entire flow tests

  @Test
  void givenUserCredentials_whenItemIsCreated_thenItemShouldBeQueryable() throws Exception {
    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/items")
                    .content(asJsonString(createShirtItem()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    assertNotNull(createdItemResponse.getContentAsString());

    Item createdItem = asItem(createdItemResponse.getContentAsByteArray());
    assertEquals("$60.00", createdItem.getPrice());
    assertEquals(ItemType.CLOTHES, createdItem.getType());
    assertEquals("T-Shirt", createdItem.getName());

    MockHttpServletResponse queriedItemResponse =
        mvc.perform(
                get("/api/items/" + createdItem.getId()).with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();

    Item fetchedItem = asItem(queriedItemResponse.getContentAsByteArray());

    assertEquals(200, queriedItemResponse.getStatus());
    assertEquals("$60.00", fetchedItem.getPrice());
    assertEquals(ItemType.CLOTHES, fetchedItem.getType());
    assertEquals("T-Shirt", fetchedItem.getName());
  }

  @Test
  void givenAdminCredentials_whenItemIsCreated_thenItemShouldBeQueryable() throws Exception {
    MockHttpServletResponse createdItemResponse =
        mvc.perform(
                post("/api/items")
                    .content(asJsonString(createShirtItem()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("ROOT", "ROOT")))
            .andReturn()
            .getResponse();

    assertEquals(201, createdItemResponse.getStatus());
    assertNotNull(createdItemResponse.getContentAsString());

    Item createdItem = asItem(createdItemResponse.getContentAsByteArray());
    assertEquals("$60.00", createdItem.getPrice());
    assertEquals(ItemType.CLOTHES, createdItem.getType());
    assertEquals("T-Shirt", createdItem.getName());

    MockHttpServletResponse queriedItemResponse =
        mvc.perform(get("/api/items/" + createdItem.getId()).with(httpBasic("ROOT", "ROOT")))
            .andReturn()
            .getResponse();

    Item fetchedItem = asItem(queriedItemResponse.getContentAsByteArray());

    assertEquals(200, queriedItemResponse.getStatus());
    assertEquals("$60.00", fetchedItem.getPrice());
    assertEquals(ItemType.CLOTHES, fetchedItem.getType());
    assertEquals("T-Shirt", fetchedItem.getName());
  }

  private void setUp() {
    if (usersRepository.findByUsername("EMPLOYEE") == null) {
      usersRepository.save(createUserRoleEmployee());
    }

    if (usersRepository.findByUsername("ROOT") == null) {
      usersRepository.save(createUserRoleAdmin());
    }
  }
}
