package com.odeal.logging.controller;

import com.odeal.logging.logger.controller.annotation.Logging;
import com.odeal.logging.logger.controller.annotation.NoLogging;
import com.odeal.logging.logger.controller.filter.SpringLoggingFilter;
import com.odeal.logging.model.Employee;
import com.odeal.logging.model.InfoLog;
import com.odeal.logging.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.List;

@RestController
@Logging
@RequestMapping("/api/v1")
public class EmployeeController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLoggingFilter.class);

    @Autowired
    private InfoLog logProvider;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees()
    {
        logProvider.addMessage("başladı.");
        logProvider.addMessage("getAllEmployees çağırıldı.");

        return employeeService.getAllEmployees();
    }

    @GetMapping("/test")
    public ResponseEntity teest()
    {
        logProvider.addMessage("başladı.");

        logProvider.addMessage("merhaba test");
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/employees")
    public Employee createEmployee(@Valid @RequestBody Employee employee) {

        logProvider.addMessage("başladı.");

        int result = test();
        logProvider.addMessage("sonucu aldım." + result);

        logProvider.addMessage(String.format("test methodu sonucu: %d", result));


        return employeeService.createEmployee(employee);
    }

    public int test() {
        log.info("burası test bişeyler bişeyler");
        return 1+2;
    }

}
