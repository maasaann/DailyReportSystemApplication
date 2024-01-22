package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
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
    public Report findByCode(String employee_code) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(employee_code);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報情報を更新
    @Transactional
    public ErrorKinds r_update(String employee_code,UserDetail userDetail,Report report) {

        // 現在の日報情報を取得
        Report existingReport = findByCode(employee_code);

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
}