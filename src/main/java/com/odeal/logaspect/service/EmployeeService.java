package com.odeal.logaspect.service;

import com.odeal.logaspect.model.Employee;
import com.odeal.logaspect.model.InfoLog;
import com.odeal.logaspect.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Employee Service
 * @author Ramesh
 *
 */
@Service
public class EmployeeService {

    @Autowired
    private InfoLog logProvider;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List <Employee> getAllEmployees() {

        logProvider.addMessage("repo get çağırdım.");
        return employeeRepository.findAll();
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

}
