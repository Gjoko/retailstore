package com.gjoko.retailstore.helper;

import com.gjoko.retailstore.persistence.dao.ItemDto;
import com.gjoko.retailstore.persistence.dao.UserDto;
import com.gjoko.retailstore.persistence.enums.ItemType;
import com.gjoko.retailstore.rest.entity.Item;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static com.gjoko.retailstore.persistence.enums.ItemType.EQUIPMENT;
import static com.gjoko.retailstore.persistence.enums.ItemType.GROCERIES;
import static com.gjoko.retailstore.persistence.enums.Role.*;

public class TestHelper {

    public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // users
    public static UserDto createUserRoleAdmin() {
        UserDto adminDto = new UserDto();
        adminDto.setCreated(LocalDateTime.now());
        adminDto.setFirstName("root");
        adminDto.setLastName("root");
        adminDto.setPassword(encoder.encode("ROOT"));
        adminDto.setUsername("ROOT");
        adminDto.setRole(ROLE_ADMIN);
        return adminDto;
    }

    public static UserDto createUserRoleAffiliate() {
        UserDto affiliateDto = new UserDto();
        affiliateDto.setCreated(LocalDateTime.now());
        affiliateDto.setFirstName("affiliate");
        affiliateDto.setLastName("affiliate");
        affiliateDto.setPassword(encoder.encode("AFFILIATE"));
        affiliateDto.setUsername("AFFILIATE");
        affiliateDto.setRole(ROLE_AFFILIATE);
        return affiliateDto;
    }

    public static UserDto createUserRoleEmployee() {
        UserDto employeeDto = new UserDto();
        employeeDto.setCreated(LocalDateTime.now());
        employeeDto.setFirstName("employee");
        employeeDto.setLastName("employee");
        employeeDto.setPassword(encoder.encode("EMPLOYEE"));
        employeeDto.setUsername("EMPLOYEE");
        employeeDto.setRole(ROLE_EMPLOYEE);
        return employeeDto;
    }

    public static UserDto createUserRoleCustomer() {
        UserDto customerDto = new UserDto();
        customerDto.setCreated(LocalDateTime.now());
        customerDto.setFirstName("customer");
        customerDto.setLastName("customer");
        customerDto.setPassword(encoder.encode("CUSTOMER"));
        customerDto.setUsername("CUSTOMER");
        customerDto.setRole(ROLE_CUSTOMER);
        return customerDto;
    }



    public static UserDto createOldUserRoleCustomer() {
        UserDto customerDto = new UserDto();
        customerDto.setCreated(LocalDateTime.now().minusYears(3));
        customerDto.setFirstName("customer");
        customerDto.setLastName("customer");
        customerDto.setPassword(encoder.encode("OLD_CUSTOMER"));
        customerDto.setUsername("OLD_CUSTOMER");
        customerDto.setRole(ROLE_CUSTOMER);
        return customerDto;
    }

    // items
    public static Item createShirtItem() {
        Item item = new Item();
        item.setPrice("$60.00");
        item.setName("T-Shirt");
        item.setType(ItemType.CLOTHES);
        return item;
    }

    // item dtos

    public static ItemDto createJeansDtoItem() {
        ItemDto item = new ItemDto();
        item.setAmount(60d);
        item.setName("jeans");
        item.setType(ItemType.CLOTHES);
        return item;
    }

    public static ItemDto createMangoItem() {
        ItemDto item = new ItemDto();
        item.setAmount(100d);
        item.setName("mango");
        item.setType(GROCERIES);
        return item;
    }

    public static ItemDto createBBQItem() {
        ItemDto item = new ItemDto();
        item.setAmount(600d);
        item.setName("BBQ");
        item.setType(EQUIPMENT);
        return item;
    }

    public static ItemDto createChainsawItem() {
        ItemDto item = new ItemDto();
        item.setAmount(250d);
        item.setName("Chainsaw");
        item.setType(EQUIPMENT);
        return item;
    }

    public static ItemDto createPenItem() {
        ItemDto pen = new ItemDto();
        pen.setAmount(5d);
        pen.setName("pen");
        pen.setType(EQUIPMENT);
        return pen;
    }

    public static ItemDto createNotebookItem() {
        ItemDto notebook = new ItemDto();
        notebook.setAmount(20d);
        notebook.setName("notebook");
        notebook.setType(EQUIPMENT);
        return notebook;
    }

    public static ItemDto createCucumberItem() {
        ItemDto cucumber = new ItemDto();
        cucumber.setAmount(2d);
        cucumber.setName("cucumber");
        cucumber.setType(GROCERIES);
        return cucumber;
    }


}

