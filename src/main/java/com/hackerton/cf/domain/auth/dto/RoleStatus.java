package com.hackerton.cf.domain.auth.dto;

public enum RoleStatus {
    NONE("NONE"),// NONE: SMS 미인증
    REGISTER("REGISTER"),// REGISTER: 프로필 등록 여부
    COMPLETE("COMPLETE");// 프로필이 등록되었으면 COMPLETE로 전환

    private final String status;

    RoleStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}