package com.bigelmo.cloud.model;

import lombok.Data;

@Data
public class FileRequestMessage implements ExchangeMessage {

    private final String fileName;

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
