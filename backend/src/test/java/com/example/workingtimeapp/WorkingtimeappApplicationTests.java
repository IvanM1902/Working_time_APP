package com.example.workingtimeapp;

import com.example.workingtimeapp.clockevent.ClockEvent;
import com.example.workingtimeapp.clockevent.ClockEventRepository;
import com.example.workingtimeapp.employee.Employee;
import com.example.workingtimeapp.employee.EmployeeRepository;
import com.example.workingtimeapp.enums.EventType;
import com.example.workingtimeapp.manager.Manager;
import com.example.workingtimeapp.manager.ManagerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WorkingtimeappApplicationTests {

	@Autowired
	private javax.sql.DataSource dataSource;

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

    @Autowired
    private ClockEventRepository clockEventRepository;


	@Test
	void checkTestDatabaseConnection() throws Exception {
		System.out.println("DB URL: " + dataSource.getConnection().getMetaData().getURL());
	}

	@Test
	void createManagerAndEmployeeAndTestClockIn() {
		// Create and save manager
		Manager manager = Manager.builder()
				.name("manager1")
				.password("password")
				.fullName("Ivan M")
				.email("ivanm@example.com")
				.build();
		managerRepository.save(manager);

		//Create and save employees
		Employee emp1 = new Employee(null, "John", "Doe", LocalDate.of(1990, 1, 1),
				LocalDate.now(), "Address 1", "john@example.com", "1234567890", "1111", null, new ArrayList<>());
		Employee emp2 = new Employee(null, "Jane", "Smith", LocalDate.of(1992, 2, 2),
				LocalDate.now(), "Address 2", "jane@example.com", "0987654321", "2222", null, new ArrayList<>());
		Employee emp3 = new Employee(null, "Bob", "Brown", LocalDate.of(1994, 3, 3),
				LocalDate.now(), "Address 3", "bob@example.com", "1122334455", "3333", null, new ArrayList<>());

		emp1 = employeeRepository.save(emp1);
		emp2 = employeeRepository.save(emp2);
		emp3 = employeeRepository.save(emp3);

		//Clock in for each employee
		ClockEvent clockEvent = new ClockEvent(null, EventType.CLOCK_IN, LocalDateTime.now(), emp1);
		ClockEvent clockEvent2 = new ClockEvent(null, EventType.CLOCK_IN, LocalDateTime.now(), emp2);
		ClockEvent clockEvent3 = new ClockEvent(null, EventType.CLOCK_IN, LocalDateTime.now(), emp3);

		clockEventRepository.saveAll(List.of(clockEvent, clockEvent2, clockEvent3));

		List<ClockEvent> johnEvents = clockEventRepository.findAllByEmployeeId(emp1.getId());
		Assertions.assertTrue(johnEvents.stream().anyMatch(e -> e.getEventType() == EventType.CLOCK_IN));
	}

	@Test
	void employeeClockInWithPincode() {
		clockEventRepository.deleteAll();
		employeeRepository.deleteAll();
		// 1. Create and save employee with pincode
		Employee emp = new Employee(null, "Alice", "Wonder", LocalDate.of(1995, 5, 5),
				LocalDate.now(), "Address X", "alice@example.com", "5555555555", "9999", null, new ArrayList<>());
		emp = employeeRepository.save(emp);

		// 2. Simulate entering pincode (find employee by pincode)
		Employee found = employeeRepository.findByPinCode(emp.getPinCode()).orElse(null);
		Assertions.assertNotNull(found);

		// 3. Clock in
		ClockEvent clockIn = new ClockEvent(null, EventType.CLOCK_IN, LocalDateTime.now(), found);
		clockEventRepository.save(clockIn);

		// 4. Assert clock-in event exists
		List<ClockEvent> events = clockEventRepository.findAllByEmployeeId(found.getId());
		Assertions.assertTrue(events.stream().anyMatch(e -> e.getEventType() == EventType.CLOCK_IN));
		System.out.println("Employee " + found.getFirstName() + " has clocked in successfully at: " + clockIn.getEventTime());
	}

}
