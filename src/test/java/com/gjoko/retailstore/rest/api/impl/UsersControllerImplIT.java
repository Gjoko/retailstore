package com.gjoko.retailstore.rest.api.impl;

import com.gjoko.retailstore.RetailstoreApplication;
import com.gjoko.retailstore.persistence.repositories.UsersRepository;
import com.gjoko.retailstore.rest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.gjoko.retailstore.helper.ConvertorHelper.asJsonString;
import static com.gjoko.retailstore.helper.ConvertorHelper.asUser;
import static com.gjoko.retailstore.helper.TestHelper.*;
import static com.gjoko.retailstore.persistence.enums.Role.ROLE_AFFILIATE;
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
class UsersControllerImplIT {
  @Autowired private WebApplicationContext context;

  @Autowired private UsersRepository usersRepository;

  private MockMvc mvc;

  private User user;

  @BeforeEach
  public void setup() {
    setUp();

    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void givenUserNotExists_whenInvokeFindById_thenReturn401() throws Exception {
    mvc.perform(get("/api/users/123").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserNotExists_whenInvokeFindByUsername_thenReturn401() throws Exception {
    mvc.perform(get("/api/users?username=123").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenUserNotExists_whenInvokeSave_thenReturn401() throws Exception {
    mvc.perform(post("/api/users").with(httpBasic("non_existing_user", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenNonAdminUserExists_whenInvokeSave_thenReturn403() throws Exception {
    mvc.perform(
            post("/api/users")
                .content(asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenUserNotExists_whenInvokeFindByUsernameOnNonExstingUsername_thenReturn404()
      throws Exception {
    mvc.perform(get("/api/users?username=123").with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenUserNotExists_whenInvokeFindByIdOnNonExstingId_thenReturn404() throws Exception {
    mvc.perform(get("/api/users/123").with(httpBasic("EMPLOYEE", "EMPLOYEE")))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenAdminUserExists_whenInvokeSave_thenReturn201() throws Exception {
    MockHttpServletResponse createdUserResponse =
        mvc.perform(
                post("/api/users")
                    .content(asJsonString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("ROOT", "ROOT")))
            .andReturn()
            .getResponse();
    assertEquals(201, createdUserResponse.getStatus());
    assertNotNull(createdUserResponse.getContentAsString());

    User createdUser = asUser(createdUserResponse.getContentAsByteArray());
    assertEquals("TEST_CUSTOMER", createdUser.getUsername());
    assertEquals(ROLE_AFFILIATE, createdUser.getRole());
    assertEquals("affiliate", createdUser.getFirstName());
    assertEquals("affiliate", createdUser.getLastName());
  }

  @Test
  void givenUserExists_whenQueryUserById_thenReturn200() throws Exception {
    MockHttpServletResponse createdUserResponse =
        mvc.perform(
                post("/api/users")
                    .content(asJsonString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("ROOT", "ROOT")))
            .andReturn()
            .getResponse();
    assertEquals(201, createdUserResponse.getStatus());
    assertNotNull(createdUserResponse.getContentAsString());
    User createdUser = asUser(createdUserResponse.getContentAsByteArray());

    MockHttpServletResponse queriedUserResponse =
        mvc.perform(
                get("/api/users/" + createdUser.getId()).with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();
    User fetchedUser = asUser(queriedUserResponse.getContentAsByteArray());
    assertEquals(200, queriedUserResponse.getStatus());
    assertEquals("TEST_CUSTOMER", fetchedUser.getUsername());
    assertEquals(ROLE_AFFILIATE, fetchedUser.getRole());
    assertEquals("affiliate", fetchedUser.getFirstName());
    assertEquals("affiliate", fetchedUser.getLastName());
  }

  @Test
  void givenUserExists_whenQueryUserByUsername_thenReturn200() throws Exception {
    String username = UUID.randomUUID().toString();
    user.setUsername(username);
    MockHttpServletResponse createdUserResponse =
        mvc.perform(
                post("/api/users")
                    .content(asJsonString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic("ROOT", "ROOT")))
            .andReturn()
            .getResponse();
    assertEquals(201, createdUserResponse.getStatus());
    assertNotNull(createdUserResponse.getContentAsString());
    User createdUser = asUser(createdUserResponse.getContentAsByteArray());

    MockHttpServletResponse queriedUserResponse =
        mvc.perform(
                get("/api/users?username=" + createdUser.getUsername())
                    .with(httpBasic("EMPLOYEE", "EMPLOYEE")))
            .andReturn()
            .getResponse();
    User fetchedUser = asUser(queriedUserResponse.getContentAsByteArray());
    assertEquals(200, queriedUserResponse.getStatus());
    assertEquals(username, fetchedUser.getUsername());
    assertEquals(ROLE_AFFILIATE, fetchedUser.getRole());
    assertEquals("affiliate", fetchedUser.getFirstName());
    assertEquals("affiliate", fetchedUser.getLastName());
  }

  private void setUp() {
    if (usersRepository.findByUsername("EMPLOYEE") == null) {
      usersRepository.save(createUserRoleEmployee());
    }

    if (usersRepository.findByUsername("ROOT") == null) {
      usersRepository.save(createUserRoleAdmin());
    }
    ModelMapper modelMapper = new ModelMapper();

    this.user = modelMapper.map(createUserRoleAffiliate(), User.class);
    this.user.setUsername("TEST_CUSTOMER");
  }
}
