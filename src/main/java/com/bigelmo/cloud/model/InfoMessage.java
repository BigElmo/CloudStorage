package com.bigelmo.cloud.model;

import lombok.Data;

@Data
public class InfoMessage implements ExchangeMessage {

    private final boolean isDir;

    public InfoMessage(boolean isDir) {
        this.isDir = isDir;
    }

    @Override
    public CommandType getType() {
        return CommandType.INFO;
    }
}
