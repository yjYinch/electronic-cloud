package com.sedwt.common.entity;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {
	//
	private static final long serialVersionUID = -8157613083634272196L;

	public R() {
		put("code", 0);
		put("message", "success");
		put("success", true);
		
	}

	public static R error() {
		return error(500, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(500, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("message", msg);
		r.put("success", false);
		return r;
	}

    public static R error(String code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        r.put("success", false);
        return r;
    }

	public static R ok(String msg) {
		R r = new R();
		r.put("message", msg);
		r.put("success", true);
		return r;
	}

	public static R data(Object obj) {
		R r = new R();
		r.put("result", obj);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}