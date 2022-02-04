package com.bigelmo.cloud.model;

import java.io.Serializable;

public interface ExchangeMessage extends Serializable {
    CommandType getType();
}
