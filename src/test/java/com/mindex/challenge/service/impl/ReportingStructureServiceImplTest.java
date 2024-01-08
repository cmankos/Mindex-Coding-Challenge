package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class ReportingStructureServiceImplTest {

    private ReportingStructureService reportingStructureService;

    @MockBean
    EmployeeRepository employeeRepository;

    @Before
    public void init(){
        reportingStructureService = new ReportingStructureServiceImpl(employeeRepository);
    }

    @Test
    public void whenStraightforwardTreeStructureThenCountedAsExpectedTest(){

        Employee employeeA = new Employee();
        Employee employeeB = new Employee();
        Employee employeeC = new Employee();
        Employee employeeD = new Employee();

        employeeA.setEmployeeId("employeeA");
        employeeB.setEmployeeId("employeeB");
        employeeC.setEmployeeId("employeeC");
        employeeD.setEmployeeId("employeeD");
        employeeA.setDirectReports(new ArrayList() {{add(employeeB);}});
        employeeB.setDirectReports(new ArrayList() {{add(employeeC); add(employeeD);}});

        when(employeeRepository.findByEmployeeId("employeeA")).thenReturn(employeeA);
        when(employeeRepository.findByEmployeeId("employeeB")).thenReturn(employeeB);
        when(employeeRepository.findByEmployeeId("employeeC")).thenReturn(employeeC);
        when(employeeRepository.findByEmployeeId("employeeD")).thenReturn(employeeD);

        assert(reportingStructureService.findReportingStructure(employeeA.getEmployeeId()).getNumberOfReports() == 3);
        assert(reportingStructureService.findReportingStructure(employeeB.getEmployeeId()).getNumberOfReports() == 2);
        assert(reportingStructureService.findReportingStructure(employeeC.getEmployeeId()).getNumberOfReports() == 0);
        assert(reportingStructureService.findReportingStructure(employeeD.getEmployeeId()).getNumberOfReports() == 0);

    }

    @Test
    public void whenMultipleAppearancesInTreeThenCountedOnceTest(){

        Employee employeeA = new Employee();
        Employee employeeB = new Employee();
        Employee employeeC = new Employee();

        employeeA.setEmployeeId("employeeA");
        employeeB.setEmployeeId("employeeB");
        employeeC.setEmployeeId("employeeC");
        employeeA.setDirectReports(new ArrayList() {{add(employeeB); add(employeeC);}});
        employeeB.setDirectReports(new ArrayList() {{add(employeeC);}});

        when(employeeRepository.findByEmployeeId("employeeA")).thenReturn(employeeA);
        when(employeeRepository.findByEmployeeId("employeeB")).thenReturn(employeeB);
        when(employeeRepository.findByEmployeeId("employeeC")).thenReturn(employeeC);

        assert(reportingStructureService.findReportingStructure(employeeA.getEmployeeId()).getNumberOfReports() == 2);
        assert(reportingStructureService.findReportingStructure(employeeB.getEmployeeId()).getNumberOfReports() == 1);
        assert(reportingStructureService.findReportingStructure(employeeC.getEmployeeId()).getNumberOfReports() == 0);
    }

    // Normally, I'd ask the appropriate party how they'd want to handle cases like this,
    // but for the sake of expediency I am making some assumptions
    @Test
    public void whenCircularDependencyThenCountSelfTest() {

        Employee employeeA = new Employee();
        Employee employeeB = new Employee();

        employeeA.setEmployeeId("employeeA");
        employeeB.setEmployeeId("employeeB");
        employeeA.setDirectReports(new ArrayList() {{add(employeeB);}});
        employeeB.setDirectReports(new ArrayList() {{add(employeeA);}});

        when(employeeRepository.findByEmployeeId("employeeA")).thenReturn(employeeA);
        when(employeeRepository.findByEmployeeId("employeeB")).thenReturn(employeeB);

        assert(reportingStructureService.findReportingStructure(employeeA.getEmployeeId()).getNumberOfReports() == 2);
        assert(reportingStructureService.findReportingStructure(employeeB.getEmployeeId()).getNumberOfReports() == 2);
    }
}
