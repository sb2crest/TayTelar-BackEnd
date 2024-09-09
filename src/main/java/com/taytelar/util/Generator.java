package com.taytelar.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;
@Component
public class Generator {
    public String generateId(String generatorId) {
        UUID uuid = UUID.randomUUID();
        String numericUUID = new BigInteger(uuid.toString().replace("-", ""), 16).toString();
        String generatedId = switch (generatorId) {
            case Constants.ORDER_ID -> numericUUID.substring(0, 10);
            case Constants.ORDER_ITEM_ID -> numericUUID.substring(0, 8);
            case Constants.PAYMENT_ID -> numericUUID.substring(0, 12);
            case Constants.USER_ID, Constants.CART_ID, Constants.CART_ITEM_ID -> numericUUID.substring(0, 6);
            case null, default -> numericUUID;
        };

        return generatorId + generatedId;
    }
}
