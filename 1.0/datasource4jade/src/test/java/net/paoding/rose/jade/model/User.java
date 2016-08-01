package net.paoding.rose.jade.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {

    private int id;
    private String username;
    private String realname;

    private String privs;
    private int status;
    private Date ctime;
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }


    public String getPrivs() {
        return privs;
    }

    public void setPrivs(String privs) {
        this.privs = privs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", privs='" + privs + '\'' +
                ", status=" + status +
                ", ctime=" + ctime +
                ", cityName=" + cityName +
                '}';
    }
}
