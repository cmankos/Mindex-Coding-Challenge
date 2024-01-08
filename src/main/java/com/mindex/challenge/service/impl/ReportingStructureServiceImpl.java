package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);
    private EmployeeRepository employeeRepository;

    public ReportingStructureServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public ReportingStructure findReportingStructure (String employeeId) {
        return new ReportingStructure(employeeRepository.findByEmployeeId(employeeId), findNumberOfReports(employeeId));
    }

    // Edge cases I'm checking for:
    // 1. Are you actually an employee
    // 2. less than ideal report relationships - circular dependency or dependency showing up multiple places
    private int findNumberOfReports(String employeeId) {

        Set<String> reportingToEmployeeIdSet = new HashSet<>();
        List<Employee> reportingToEmployeeIdList = new ArrayList<>();
        Employee parentEmployee = employeeRepository.findByEmployeeId(employeeId);
        if (parentEmployee.getDirectReports() != null) {
            reportingToEmployeeIdList.addAll(parentEmployee.getDirectReports());
        }

        while (!reportingToEmployeeIdList.isEmpty()) {

            Employee potentialReportToEmployee = reportingToEmployeeIdList.remove(0);
            boolean isEmployeeInDatabase =
                    employeeRepository.findByEmployeeId(potentialReportToEmployee.getEmployeeId()) != null;
            boolean isEmployeeAlreadyConsidered =
                    reportingToEmployeeIdSet.contains(potentialReportToEmployee.getEmployeeId());

            if (isEmployeeInDatabase && !isEmployeeAlreadyConsidered) {
                reportingToEmployeeIdSet.add(potentialReportToEmployee.getEmployeeId());
                // Because the directReports doesn't seem to have all the employee information, we're hitting the db
                List<Employee> potentialReports = employeeRepository.findByEmployeeId(potentialReportToEmployee.getEmployeeId()).getDirectReports();
                if(potentialReports != null) {
                    reportingToEmployeeIdList.addAll(potentialReports);
                }
            }
        }

        return reportingToEmployeeIdSet.size();
    }
}
