package com.sedwt.common.constant;

public class ErrorCodeConstants {
    /**
     * 成功
     */
    public static final String SUCCESS = "0";

    /**
     * 认证失败
     */
    public static final String AUTHENTICATION_FAILED = "20001";

    /**
     * 权限不足
     */
    public static final String PERMISSION_ERROR = "20002";

    /**
     * 参数解析异常
     */
    public static final String PARSE_PARAMETER_ERROR = "31001";
    /**
     * 错误的请求方式
     */
    public static final String METHOD_NOT_ALLOWED = "31002";

    /**
     * 资源已存在
     */
    public static final String RESOURCE_EXISTED = "40003";

    /**
     * 资源未找到
     */
    public static final String RESOURCE_NOT_FOUND = "40004";

    /**
     * 资源不可用
     */
    public static final String RESOURCE_UNAVAILABLE = "40005";

    /**
     * 参数错误
     */
    public static final String PARAMETER_FORMAT_ERROR = "40006";

    /**
     * EXCEL模板错误
     */
    public static final String EXCEL_TEMPLATE_ERROR = "40007";

    /**
     * 数据导入错误
     */
    public static final String EXCEL_DATA_IMPORT_ERROR = "40008";

    /**
     * 文件类型错误
     */
    public static final String FILE_TYPE_ERROR = "40009";

    /**
     * 重复操作
     */
    public static final String REPEAT_OPERATION = "40010";

    /**
     * 非法操作
     */
    public static final String OPERATION_NOT_ALLOWED = "40011";

    /**
     * 新旧密码不能相同
     */
    public static final String SAME_PASSWORD_ERROR = "40012";

    /**
     * 密码错误
     */
    public static final String PASSWORD_ERROR = "40013";

    /**
     * 上传文件为空或文件过大
     */
    public static final String FILE_SIZE_ERROR = "40014";

    /**
     * 备份数据存在错误
     */
    public static final String BACKUP_ERROR_CODE = "40015";

    /**
     * 任务超时
     */
    public static final String TIMEOUT = "40016";

    /**
     * 任务失败
     */
    public static final String JOB_FAIL = "40017";

    /**
     * 服务器内部错误
     */
    public static final String SERVER_INTERNAL_ERROR = "50000";

    /**
     * emq服务器异常
     */
    public static final String EMQ_SERVER_EXCEPTION = "60001";

    /**
     * 数据库操作失败
     */
    public static final String DATABASE_OPERATION_FAILED = "60002";

    /**
     * redis操作异常
     */
    public static final String REDIS_EXCEPTION = "60003";

    /**
     * 服务器调用异常
     */
    public static final String SERVER_INVOCATION_EXCEPTION = "60004";
}
