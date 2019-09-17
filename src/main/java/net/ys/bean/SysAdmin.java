package net.ys.bean;

import java.io.Serializable;

/**
 * 管理员表
 */
public class SysAdmin implements Serializable {

    private static final long serialVersionUID = 5017328858200144596L;

    private String id;    //主键

    private int magType;    //类型 0-普通管理员/1-超级管理员

    private String username;    //用户名

    private String pwd;    //密码

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setMagType(int magType) {
        this.magType = magType;
    }

    public int getMagType() {
        return this.magType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}