package com.bigelmo.cloud.model;

import lombok.Data;

@Data
public class DirInMessage implements ExchangeMessage {

    private final String newDirName;

    public DirInMessage(String newDirName) {
        this.newDirName = newDirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.DIR_IN;
    }
}
