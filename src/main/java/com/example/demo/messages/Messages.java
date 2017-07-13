package com.example.demo.messages;

public class Messages {
    private Messages() {}

    public static String success() {
        return "success";
    }

    public static String userNotFound(Long personalId) {
        return "user not found for personal Id " + personalId;
    }

    public static String notEnoughAmount() {
        return "there is not enough amount for withdrawal";
    }

    public static String countOfOperationsExceeds() {
        return "count of operations per time on account is exceeds";
    }
}
