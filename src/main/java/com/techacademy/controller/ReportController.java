package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報 一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "reports/r-list";
    }

    // 日報 詳細画面
    @GetMapping(value = "/{employee_code}/")
    public String detail(@PathVariable String employee_code, Model model) {

        model.addAttribute("report", reportService.findByCode(employee_code));
        return "reports/r-detail";
    }

    // 日報 新規登録画面
    @GetMapping(value = "/r-add")
    public String r_create(@ModelAttribute Report report) {

        return "reports/r-new";
    }

    // 日報 更新画面
    @PostMapping(value = "/{employee_code}/r-renew")
    public String r_renew(@PathVariable String employee_code, Model model) {

        model.addAttribute("report", reportService.findByCode(employee_code));
        return "reports/r-renew";
    }

    // 日報 情報更新実行
    @PostMapping(value = "/{employee_code}/r-update")
    public String r_update(
            @PathVariable String employee_code,
            @AuthenticationPrincipal UserDetail userDetail,
            @Validated Report report, BindingResult res, Model model) {

        ErrorKinds result = reportService.r_update(employee_code, userDetail, report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(employee_code));
            return r_renew(employee_code, model);
        }
        return "redirect:reports";
    }
}