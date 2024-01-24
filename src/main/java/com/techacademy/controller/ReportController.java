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
    public String list(
            @AuthenticationPrincipal UserDetail userDetail, Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());
        // 名前取得
        model.addAttribute("name", userDetail.getEmployee().getName());

        return "reports/r-list";
    }

    // 日報 詳細画面
    @GetMapping(value = "/{employeeCode}/")
    public String detail(
            @PathVariable String employeeCode, 
            @AuthenticationPrincipal UserDetail userDetail, Model model) {

        model.addAttribute("report", reportService.findByCode(employeeCode));
        
        System.out.println("11111111111");
        System.out.println(model);
        
        model.addAttribute("name", userDetail.getEmployee().getName());
        
        System.out.println("22222222222");
        System.out.println(model);
        
        return "reports/r-detail";
    }

    // 日報 新規登録画面
    @GetMapping(value = "/r-add")
    public String r_create(@ModelAttribute Report report,
            @AuthenticationPrincipal UserDetail userDetail, Model model) {
        
        model.addAttribute("name", userDetail.getEmployee().getName());
        
        return "reports/r-new";
    }

    // 日報 新規登録処理
    @PostMapping(value = "/r-add")
    public String add(
            @AuthenticationPrincipal UserDetail userDetail,
            @Validated Report report, BindingResult res, Model model) {

        ErrorKinds result = reportService.save(report, userDetail);

        if (ErrorMessage.contains(result)) {
            
            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));
            
            return r_create(report, null, null);
        }

        return "redirect:/reports";
    }

    // 日報 更新画面
    @PostMapping(value = "/{employeeCode}/r-renew")
    public String r_renew(@PathVariable String employeeCode,
            @AuthenticationPrincipal UserDetail userDetail, Model model) {

        model.addAttribute("report", reportService.findByCode(employeeCode));
        model.addAttribute("name", userDetail.getEmployee().getName());
        
        return "reports/r-renew";
    }

    // 日報 情報更新実行
    @PostMapping(value = "/{employeeCode}/r-update")
    public String r_update(
            @PathVariable String employeeCode,
            @AuthenticationPrincipal UserDetail userDetail,
            @Validated Report report, BindingResult res, Model model) {

        ErrorKinds result = reportService.r_update(employeeCode, userDetail, report);

        if (ErrorMessage.contains(result)) {
            
            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(employeeCode));
            
            return r_renew(employeeCode, null, model);
        }

        return "redirect:/reports";
    }
}
