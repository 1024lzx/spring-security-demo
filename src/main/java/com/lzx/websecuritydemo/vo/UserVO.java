package com.lzx.websecuritydemo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1832574402487423998L;

    private Long id;
    private String name;
    private String password;
    private Character isLock;
    private Date createTime;
}
