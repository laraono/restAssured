package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Consent {
    List<String> permissions;
    String expDate;

    public Consent(List<String> permissions, String date) {
        this.permissions = permissions;
        this.expDate = date;
    }

    public Consent(List<String> permissions) {
        this.permissions = permissions;
        this.expDate = LocalDate.now().plusYears(1).toString();
    }

    public Consent(String date) {
        this.permissions = new ArrayList<>();
        this.permissions.add("ACCOUNTS_READ");
        this.permissions.add("TRANSACTIONS_READ");
        this.expDate = date;
    }

    public Consent() {
        this.permissions = new ArrayList<>();
        this.permissions.add("ACCOUNTS_READ");
        this.permissions.add("TRANSACTIONS_READ");
        this.expDate = LocalDate.now().plusYears(1).toString();
    }
}