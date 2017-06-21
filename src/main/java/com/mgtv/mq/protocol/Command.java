package com.mgtv.mq.protocol;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Command {

    private int code;

    private int version;

    private int opaque;

    private int flag;

    private String remark;

    private HashMap<String, String> extFields;

    transient byte[] body;

    public static Command createCommand(int code) {
        Command command = new Command();
        command.code = code;
        command.version = 2;
        return command;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public void setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public ByteBuffer encodeHeader() {
        return this.encodeHeader(this.body != null ? this.body.length : 0);
    }

    public ByteBuffer encodeHeader(final int bodyLength) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        return buffer;

    }
}
