package com.vault.enums;

public enum ServiceIds {
    ADD_CARD("ADD_CARD"),
    DEPOSIT_FUNDS("DEPOSIT_FUNDS"),
    WITHDRAW_FUNDS("WITHDRAW_FUNDS");

    String serviceId;

    ServiceIds(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }
}
