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

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
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
    public ErrorKinds save(Report report) {

        report.setDeleteFlg(false);

        LocalDate now_yyyymmdd = LocalDate.now();
        report.setReportDate(now_yyyymmdd);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        
        return ErrorKinds.SUCCESS;
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
