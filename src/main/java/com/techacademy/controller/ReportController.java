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
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    @Autowired
    public ReportController(
            ReportService reportService,
            EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報 一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail,Model model) {

        String role = userDetail.getEmployee().getRole().toString();

        // ADMIN と GENERAL で処理を分ける
        if ( role == "ADMIN" ) {
            
            // ADMIN なら全件表示
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
            
        } else {

            // GENERAL なら自分のだけ
            String EmpCode = userDetail.getEmployee().getCode();
            Employee employee = employeeService.findByCode(EmpCode);
            
            model.addAttribute("listSize", reportService.findByEmployee(employee).size());
            model.addAttribute("reportList", reportService.findByEmployee(employee));
            
        }
        return "reports/r-list";
    }

    // 日報 詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable String id,Model model) {

        model.addAttribute("report", reportService.findByCode(id));

        return "reports/r-detail";
    }

    // 日報 新規登録画面
    @GetMapping(value = "/r-add")
    public String r_create(
            @ModelAttribute Report report,
            @AuthenticationPrincipal UserDetail userDetail,Model model) {

        model.addAttribute("name", userDetail.getEmployee().getName());

        return "reports/r-new";
    }

    // 日報 新規登録処理
    @PostMapping(value = "/r-add")
    public String add(
            @AuthenticationPrincipal UserDetail userDetail,
            @Validated Report report,BindingResult res, Model model) {
        
        // 入力チェック
        if (res.hasErrors()) {
            return r_create(report,userDetail,model);
        }
        
        // employeeCode を取得
        String EmpCode = userDetail.getEmployee().getCode();
        // Employee を EmpCode でインスタンス化
        Employee employee = employeeService.findByCode(EmpCode);
        // setEmployee
        report.setEmployee(employee);

        // 現在の Report の総本数 +1 を取得する
        Integer ReportCounts = reportService.findAll().size() + 1;
        // setId
        report.setId(ReportCounts);

        // save
        ErrorKinds result = reportService.save(report,employee);

        if (ErrorMessage.contains(result)) {

            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));

            return r_create(report,userDetail,model);
        }

        return "redirect:/reports";
    }

    // 日報 削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {

            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(id));

            return detail(id,model);
        }

        return "redirect:/reports";
    }

    // 日報 更新画面
    @PostMapping(value = "/{id}/r-renew")
    public String r_renew(@PathVariable String id, Model model) {

        model.addAttribute("report", reportService.findByCode(id));

        return "reports/r-renew";
    }

    // 日報 情報更新実行
    @PostMapping(value = "/{id}/r-update")
    public String r_update(
            @PathVariable String id,
            @Validated Report report,
            @AuthenticationPrincipal UserDetail userDetail,
            BindingResult res, Model model) {

        // employeeCode を取得
        String EmpCode = userDetail.getEmployee().getCode();
        // Employee を EmpCode でインスタンス化
        Employee employee = employeeService.findByCode(EmpCode);

        ErrorKinds result = reportService.r_update(id, report, employee);

        if (ErrorMessage.contains(result)) {

            model.addAttribute(
                    ErrorMessage.getErrorName(result),
                    ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(id));

            return r_renew(id, model);
        }

        return "redirect:/reports";
    }
}
