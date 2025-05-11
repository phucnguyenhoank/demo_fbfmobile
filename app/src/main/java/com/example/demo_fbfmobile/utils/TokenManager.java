package com.example.demo_fbfmobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONObject;

public class TokenManager {
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "jwt_token";
    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    // 🔍 Decode a token without relying on internal storage
    public String getClaimFromRawToken(String token, String claimKey) {
        if (token == null) return null;

        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject jsonObject = new JSONObject(payload);
            return jsonObject.optString(claimKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Decode JWT and get a field from the payload
    public String getClaim(String claimKey) {
        String token = getToken();
        return getClaimFromRawToken(token, claimKey); // ← Fixed: return was missing
    }

    // 🧪 Convenience methods
    public String getUsername() {
        return getClaim("sub"); // 'sub' is the subject, usually the username
    }

    public String getCartId() {
        return getClaim("cartId");
    }

    public boolean isTokenExpired() {
        String exp = getClaim("exp");
        if (exp == null) return true;

        long expTime = Long.parseLong(exp) * 1000;
        return System.currentTimeMillis() > expTime;
    }
}

