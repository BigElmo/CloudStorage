package com.bigelmo.cloud.model;

import lombok.Data;

@Data
public class RequestInfoMessage implements ExchangeMessage {

    private final String fileName;

    public RequestInfoMessage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.INFO_REQUEST;
    }
}
