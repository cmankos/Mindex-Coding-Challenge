package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

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
            reportingToEmployeeIdList.addAll(employeeRepository.findByEmployeeId(employeeId).getDirectReports());
        }

        while (!reportingToEmployeeIdList.isEmpty()) {

            Employee potentialReportToEmployeeId = reportingToEmployeeIdList.remove(0);
            boolean isEmployeeInDatabase =
                    !StringUtils.isEmpty(employeeRepository.findByEmployeeId(potentialReportToEmployeeId.getEmployeeId()));
            boolean isEmployeeAlreadyConsidered =
                    reportingToEmployeeIdSet.contains(potentialReportToEmployeeId.getEmployeeId());

            if (!isEmployeeInDatabase) {
                LOG.debug("Employee ID [{}] has not been found - ", potentialReportToEmployeeId.getEmployeeId());
            }

            if (isEmployeeInDatabase && !isEmployeeAlreadyConsidered) {
                reportingToEmployeeIdSet.add(potentialReportToEmployeeId.getEmployeeId());
                List<Employee> potentialReports = employeeRepository.findByEmployeeId(potentialReportToEmployeeId.getEmployeeId()).getDirectReports();
                if(potentialReports != null) {
                    reportingToEmployeeIdList.addAll(potentialReports);
                }
            }
        }

        return reportingToEmployeeIdSet.size();
    }
}
