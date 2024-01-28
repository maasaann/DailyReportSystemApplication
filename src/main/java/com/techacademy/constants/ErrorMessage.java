package com.techacademy.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// エラーメッセージ用クラス
public class ErrorMessage {

    // エラーメッセージ情報マップ
    private final static Map<ErrorKinds,List<String>> errorMessageMap = new HashMap<ErrorKinds,List<String>>() {

        // serialVersionUID 再起動してもログイン情報を保持できる仕組み
        private static final long serialVersionUID = 1L;
        {
            // （追加）氏名 空白チェック用エラーメッセージ
            put(ErrorKinds.BLANK_ERROR_NAME,new ArrayList<String>(Arrays.asList("nameError", "値を入力してください")));
            // （追加）氏名 空白チェック用エラーメッセージ
            put(ErrorKinds.RANGECHECK_ERROR_NAME,new ArrayList<String>(Arrays.asList("nameError", "20文字以下で入力してください")));

            // （追加）タイトル 空白チェック用エラーメッセージ
            put(ErrorKinds.BLANK_ERROR_TITLE,new ArrayList<String>(Arrays.asList("titleError", "値を入力してください")));
            // （追加）タイトル 名空白チェック用エラーメッセージ
            put(ErrorKinds.RANGECHECK_ERROR_TITLE,new ArrayList<String>(Arrays.asList("titleError", "100文字以下で入力してください")));

            // （追加）内容 空白チェック用エラーメッセージ
            put(ErrorKinds.BLANK_ERROR_CONTENT,new ArrayList<String>(Arrays.asList("contentError", "値を入力してください")));
            // （追加）内容 名空白チェック用エラーメッセージ
            put(ErrorKinds.RANGECHECK_ERROR_CONTENT,new ArrayList<String>(Arrays.asList("contentError", "６00文字以下で入力してください")));

            // パスワード空白チェック用エラーメッセージ
            put(ErrorKinds.BLANK_ERROR,new ArrayList<String>(Arrays.asList("passwordError", "値を入力してください")));
            // パスワードの半角英数字チェック用エラーメッセージ
            put(ErrorKinds.HALFSIZE_ERROR,new ArrayList<String>(Arrays.asList("passwordError", "パスワードは半角英数字のみで入力してください")));
            // パスワードの8文字～16文字チェック用エラーメッセージ
            put(ErrorKinds.RANGECHECK_ERROR,new ArrayList<String>(Arrays.asList("passwordError", "8文字以上16文字以下で入力してください")));

            // 従業員番号重複チェック用エラーメッセージ
            put(ErrorKinds.DUPLICATE_EXCEPTION_ERROR,new ArrayList<String>(Arrays.asList("codeError", "既に登録されている社員番号です")));
            // 従業員番号重複チェック(例外)用エラーメッセージ
            put(ErrorKinds.DUPLICATE_ERROR,new ArrayList<String>(Arrays.asList("codeError", "既に登録されている社員番号です")));
            // ログイン中の従業員削除チェック用エラーメッセージ
            put(ErrorKinds.LOGINCHECK_ERROR,new ArrayList<String>(Arrays.asList("deleteError", "ログイン中の従業員を削除することは出来ません")));

            // （追加）日付 空白エラーメッセージ
            put(ErrorKinds.DATECHECK_BLANK_ERROR,new ArrayList<String>(Arrays.asList("reportDateBlankError", "値を入力してください")));
            // 同一日付チェック用エラーメッセージ
            put(ErrorKinds.DATECHECK_ERROR,new ArrayList<String>(Arrays.asList("reportDateError", "既に登録されている日付です")));
        }
    };

    // エラーメッセージマップにあるエラーかどうかのチェック
    public static boolean contains(ErrorKinds errorKinds) {
        if (errorMessageMap.containsKey(errorKinds)) {
            return true;
        } else {
            return false;
        }
    }

    // エラーメッセージの名称を取得
    public static String getErrorName(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(0);
    }

    // エラーメッセージの値を取得
    public static String getErrorValue(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(1);
    }
}
