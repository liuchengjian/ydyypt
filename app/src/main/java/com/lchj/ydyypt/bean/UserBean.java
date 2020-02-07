package com.lchj.ydyypt.bean;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2020/1/20.
 */
public class UserBean {

    /**
     * id : 378b2c5c9e2a49ff89d9ece7c1cbba76
     * username : zhouqiang-ds
     * realname : 周强
     * erpId : 0
     * password : null
     * orgName : 生产运行处
     * orgId : 4dba7e4e624c48cf83152cb4249e3d9e
     * orgCode : SCSH-SCYXC
     * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1Nzk1MjAxMjYsInVzZXJuYW1lIjoiemhvdXFpYW5nLWRzIiwicGFzc3dvcmQiOiJlMTBhZGMzOTQ5YmE1OWFiYmU1NmUwNTdmMjBmODgzZSIsImlhdCI6MTU3OTUxMjkyNn0.ykHIyuN7ccg8-UaG3MpsoAbEKvfdsGCgVTcLuk0ds6w
     * illegalRecord : false
     * personImage : null
     * birthDay : null
     */

    private String id;
    private String username;
    private String realname;
    private String erpId;
    private String password;
    private String orgName;
    private String orgId;
    private String orgCode;
    private String token;
    private boolean illegalRecord;
    private String personImage;
    private String birthDay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getErpId() {
        return erpId;
    }

    public void setErpId(String erpId) {
        this.erpId = erpId;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isIllegalRecord() {
        return illegalRecord;
    }

    public void setIllegalRecord(boolean illegalRecord) {
        this.illegalRecord = illegalRecord;
    }

    public Object getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public Object getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
}
