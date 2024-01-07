package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

// Tests I'm interested in:
// A -> B; B -> A yields 1
// A -> B, C; B -> C yields 2
// straightforwards like A -> B yields 1, A -> B; B -> C, D yields 3
// Going to need to mock all of this. Core idea. Or maybe I'll just add it to separate testing db. Undecided.
public class ReportingStructureServiceImplTest {

    @Autowired
    ReportingStructureService reportingStructureService;

    @Test
    public void findReportingStructureTest() {
        Employee A1 = new Employee();
        Employee B1 = new Employee();
        Employee A2 = new Employee();
        Employee B2 = new Employee();
        Employee C2 = new Employee();
        Employee A3 = new Employee();
        Employee B3 = new Employee();
        Employee C3 = new Employee();
        Employee D3 = new Employee();

        // Shortcutting a little here
        A1.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        B1.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        A1.setDirectReports(new ArrayList() {{add(B1);}});
        B1.setDirectReports(new ArrayList() {{add(A1);}});

        A2.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        B2.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        C2.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        A2.setDirectReports(new ArrayList() {{add(B2); add(C2);}});
        B2.setDirectReports(new ArrayList() {{add(C2);}});

        A3.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        B3.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        C3.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        D3.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        A3.setDirectReports(new ArrayList() {{add(B3);}});
        B3.setDirectReports(new ArrayList() {{add(C3); add(D3);}});

        assert(reportingStructureService.findReportingStructure(A1.getEmployeeId()).getNumberOfReports() == 1);
    }
}
