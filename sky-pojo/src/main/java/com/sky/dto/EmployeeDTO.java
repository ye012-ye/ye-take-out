package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;// 身份证号

}
//生成请求数据JSON格式
 //{"username":"admin","name":"admin","phone":"12345678901","sex":"1","idNumber":"123456789012345678"}
