package com.example.PasswordEmailSecurityDemo.ModelResponse;

public class OperationModel {

    private String operationStatus;
    private String operationName;


    public OperationModel() {
    }

    public OperationModel(String operationResult, String operationName) {
        this.operationStatus = operationResult;
        this.operationName = operationName;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
