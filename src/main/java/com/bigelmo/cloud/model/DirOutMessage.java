package com.bigelmo.cloud.model;

import lombok.Data;

@Data
public class DirOutMessage implements ExchangeMessage {
    @Override
    public CommandType getType() {
        return CommandType.DIR_OUT;
    }
}
