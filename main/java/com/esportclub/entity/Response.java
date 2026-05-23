package com.esportclub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Response<T> ok() { return new Response<>(200, "success", null); }
    public static <T> Response<T> ok(T data) { return new Response<>(200, "success", data); }
    public static <T> Response<T> ok(String msg, T data) { return new Response<>(200, msg, data); }
    public static <T> Response<T> fail(int code, String msg) { return new Response<>(code, msg, null); }
    public static <T> Response<T> fail(String msg) { return new Response<>(500, msg, null); }
}
