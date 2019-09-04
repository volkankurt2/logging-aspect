package com.odeal.logaspect.controller;

import com.odeal.logaspect.logger.controller.annotation.Logging;
import com.odeal.logaspect.model.Employee;
import com.odeal.logaspect.service.EmployeeService;
import com.oracle.tools.packager.Log;
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

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping("/employees")
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        log.info("Burası birinci log");
        log.info("Burası ikinci log");

        return employeeService.createEmployee(employee);
    }

}
