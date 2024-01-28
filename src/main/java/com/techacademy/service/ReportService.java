package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    //private final EmployeeRepository employeeRepository;

    @Autowired
    public ReportService(
            ReportRepository reportRepository ) {
            //,EmployeeRepository employeeRepository
        this.reportRepository = reportRepository;
        //this.employeeRepository = employeeRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findByCode(String id) {

        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);

        return report;
    }

    // 日報 情報を更新
    @Transactional
    public ErrorKinds r_update(String id,Report report) {

        // 現在の日報情報を取得
        Report existingReport = findByCode(id);

        // 更新後の日報情報を、現在の日報情報に上書き
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());

        // 現在の時間を取得してセットする
        LocalDateTime now = LocalDateTime.now();
        existingReport.setUpdatedAt(now);

        // 上書き保存実行
        reportRepository.save(existingReport);

        return ErrorKinds.SUCCESS;
    }

    // 日報 保存
    @Transactional
    public ErrorKinds save(Report report,Employee employee) {

        // 日報 日付 の 空欄 と 重複 チェック
        if ( isBlankReportDate(report) ) {
            return ErrorKinds.DATECHECK_BLANK_ERROR;
        } else if ( isDuplicateReportDate(report,employee) ) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        // 日報 タイトル の 空欄 と 100文字以下 チェック
        if ( isBlankTitle(report) ) {
            return ErrorKinds.BLANK_ERROR_TITLE;
        } else if ( isOutOfRangeTitle(report) ) {
            return ErrorKinds.RANGECHECK_ERROR_TITLE;
        }

        // 日報 内容 の 空欄 と 600文字以下 チェック
        if ( isBlankContent(report) ) {
            return ErrorKinds.BLANK_ERROR_CONTENT;
        } else if ( isOutOfRangeContent(report) ) {
            return ErrorKinds.RANGECHECK_ERROR_CONTENT;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);

        return ErrorKinds.SUCCESS;
    }

    // （チェック）日報 日付が空欄か
    public boolean isBlankReportDate(Report report) {
        LocalDate RD = report.getReportDate();
        return null == RD;
    }
    
    // （チェック）日報 日付が重複か
    public boolean isDuplicateReportDate(Report report,Employee employee) {

        // 日付
        LocalDate Date = report.getReportDate();

        // findByIdで検索
        Optional<Report> option = reportRepository.findByReportDateAndEmployee(Date,employee);
        // 取得できなかった場合はnullを返す
        Report report1 = option.orElse(null);

        return null != report1;
    }

    // （チェック）日報 タイトルが空欄か
    public boolean isBlankTitle(Report report) {
        int Length = report.getTitle().length();
        return 0 == Length;
    }
    // （チェック）日報 タイトルが100文字以下か
    public boolean isOutOfRangeTitle(Report report) {
        int Length = report.getTitle().length();
        return 100 < Length;
    }

    // （チェック）日報 内容が空欄か
    public boolean isBlankContent(Report report) {
        int Length = report.getContent().length();
        return 0 == Length;
    }
    // （チェック）日報 内容が600文字以下か
    public boolean isOutOfRangeContent(Report report) {
        int Length = report.getContent().length();
        return 600 < Length;
    }

    // 日報 削除
    @Transactional
    public ErrorKinds delete(String id) {

        Report report = findByCode(id);

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }
}
