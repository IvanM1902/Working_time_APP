package com.example.workingtimeapp.clockevent;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClockEventRepository
        extends JpaRepository<ClockEvent, Long> {
    List<ClockEvent> findAllByEmployeeId(Long employeeId);
}
