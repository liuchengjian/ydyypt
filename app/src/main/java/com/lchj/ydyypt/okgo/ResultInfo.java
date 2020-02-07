package com.lchj.ydyypt.okgo;

import java.io.Serializable;
import java.util.List;

public class ResultInfo<T> implements Serializable {
    public Boolean success;//成功标识:成功true,失败false
    public String msgType;//请求类型,可用
    public String msg;//请求内容，如失败原因
    public int totalSize;//总条数
    public List<T> list;//集合-实体
    public T data;//实体
    @Override
    public String toString() {
        return "ResultInfo{\n" +//
                "\tsuccess=" + success + "\n" +//
                "\tmsgType=" + msgType + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                "\ttotalSize=" + totalSize + "\n" +//
                "\tlist=" + list + "\n" +//
                '}';
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
