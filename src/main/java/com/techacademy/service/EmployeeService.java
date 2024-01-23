package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員一覧表示処理
    public List<Employee> findAll() {
        
        return employeeRepository.findAll();
    }

    // 1件を検索
    public Employee findByCode(String code) {
        
        // findByIdで検索
        Optional<Employee> option = employeeRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Employee employee = option.orElse(null);
        
        return employee;
    }

    // 従業員情報を更新
    @Transactional
    public ErrorKinds update(
            String code,UserDetail userDetail,Employee employee) {

        // 従業員氏名の 空欄 と 20文字以下 チェック
        if ( isBlankName(employee) ) {
            return ErrorKinds.BLANK_ERROR_NAME;
        } else if ( isOutOfRangeName(employee) ) {
            return ErrorKinds.RANGECHECK_ERROR_NAME;
        }

        // 現在の従業員情報を取得
        Employee existingEmployee = findByCode(code);

        // 更新後の従業員情報を、現在の従業員情報に上書き
        existingEmployee.setName(employee.getName());
        existingEmployee.setRole(employee.getRole());

        //パスワード処理
        if ("".equals(employee.getPassword())) {

            // 空の場合は、現在のパスワードを挿入する
            existingEmployee.setPassword(existingEmployee.getPassword());

        } else {
            // 空ではない 入力ある場合は、チェックしてから挿入する

            // パスワードチェック
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }

            // ここで挿入
            existingEmployee.setPassword(employee.getPassword());
        }

        // 現在の時間を取得してセットする
        LocalDateTime now = LocalDateTime.now();
        existingEmployee.setUpdatedAt(now);

        // 上書き保存実行
        employeeRepository.save(existingEmployee);

        return ErrorKinds.SUCCESS;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Employee employee) {

        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
        
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Employee employee = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // （チェック）従業員氏名が空欄か
    public boolean isBlankName(Employee employee) {
        int NameLength = employee.getName().length();
        return 0 == NameLength;
    }
    // （チェック）従業員氏名が20文字以下か
    public boolean isOutOfRangeName(Employee employee) {
        int NameLength = employee.getName().length();
        return 20 < NameLength;
    }

    // （チェック）従業員パスワード
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {
            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // （チェック）（チェック）従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        
        return !matcher.matches();
    }
    // （チェック）（チェック）従業員パスワードの8文字～16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {

        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        
        return passwordLength < 8 || 16 < passwordLength;
    }

}
