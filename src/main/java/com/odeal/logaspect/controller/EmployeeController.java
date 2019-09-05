package com.odeal.logaspect.controller;

import com.odeal.logaspect.logger.controller.annotation.Logging;
import com.odeal.logaspect.logger.controller.filter.SpringLoggingFilter;
import com.odeal.logaspect.model.Employee;
import com.odeal.logaspect.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Logging
@RequestMapping("/api/v1")
public class EmployeeController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLoggingFilter.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping("/employees")
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        log.info("Başladı");

        log.info("test methodu çağırılıyor");
        int result = test();
        log.info("test methodu çağırıldı");
        log.info(String.format("test methodu sonucu: %d", result));

        log.info("Bitti");

        return employeeService.createEmployee(employee);
    }

    public int test() {
        log.info("burası test bişeyler bişeyler");
        return 1+2;
    }

}
