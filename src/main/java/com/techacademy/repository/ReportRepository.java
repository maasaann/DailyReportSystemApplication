package com.techacademy.repository;

import java.sql.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String> {

    Optional<Report> findByReportDateAndEmployeeCode(Date reportDate, String employeeCode);

}
