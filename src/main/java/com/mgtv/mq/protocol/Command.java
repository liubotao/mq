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

    private static SerializeType serializeType = SerializeType.JSON;

    public static Command createCommand(int code) {
        Command command = new Command();
        command.code = code;
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
        int length = 4;

        byte[] headerData;
        headerData = this.headerEncode();

        length += headerData.length;

        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + 4 + length - bodyLength);

        result.putInt(length);

        result.put(markProtocolType(headerData.length, serializeType));

        result.put(headerData);

        result.flip();

        return result;
    }

    private byte[] headerEncode() {
        return Serializable.encode(this);
    }

    public static Command decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        Command command = headerDecode(headerData, getProtocolType(oriHeaderLen));

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        command.body = bodyData;
        return command;
    }

    private static Command headerDecode(byte[] headerData, SerializeType serializeType) {
        switch (serializeType) {
            case JSON:
                Command resultJson = Serializable.decode(headerData, Command.class);
                return resultJson;
        }
        return null;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    public static SerializeType getProtocolType(int source) {
        return SerializeType.valueOf((byte) ((source >> 24) & 0xFF));
    }

    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    @Override
    public String toString() {
        return "Command [code=" + code + ", version=" + version + ", opaque=" + opaque + ", flag(B)="
                + Integer.toBinaryString(flag) + ", remark=" + remark + ", extFields=" + extFields + ", serializeType="
                + serializeType + "]";
    }
}
