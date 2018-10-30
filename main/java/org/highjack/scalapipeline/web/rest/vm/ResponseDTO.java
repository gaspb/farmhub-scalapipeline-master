package org.highjack.scalapipeline.web.rest.vm;

import java.io.Serializable;

public class ResponseDTO implements Serializable{


    public final static int SUCCESS = 1;
    public final static int FAILURE = 0;

    private int result;

    private String resultCode;

    private String status;

    private String message;

    private Object data;


    public ResponseDTO(int result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.setStatus(result);
    }

    public ResponseDTO(int result) {
        this.result = result;
        this.setStatus(result);
    }

    public ResponseDTO(int result, String message) {
        this.result = result;
        this.message = message;
        this.setStatus(result);
    }

    public ResponseDTO() {
    }


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(int result) {
        this.status = result ==SUCCESS ? "SUCCESS" : "FAILURE";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }



    @Override
    public String toString() {
        return "ResponseDTO{" +
            "result=" + result +
            ", resultCode='" + resultCode + '\'' +
            ", status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }
}
