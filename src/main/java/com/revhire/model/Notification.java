package com.revhire.model;

/**
 * Represents an in-app notification for a user.
 */
public class Notification {

    private int     id;
    private int     userId;
    private String  message;
    private boolean read;
    private String  createdAt;

    // ── Getters ──

    public int     getId()        { return id; }
    public int     getUserId()    { return userId; }
    public String  getMessage()   { return message; }
    public boolean isRead()       { return read; }
    public String  getCreatedAt() { return createdAt; }

    // ── Setters ──

    public void setId(int id)              { this.id = id; }
    public void setUserId(int userId)      { this.userId = userId; }
    public void setMessage(String msg)     { this.message = msg; }
    public void setRead(boolean read)      { this.read = read; }
    public void setCreatedAt(String ts)    { this.createdAt = ts; }

    @Override
    public String toString() {
        return String.format("  %s [%d] %s  (%s)",
                read ? "📖" : "🔔", id, message,
                createdAt != null ? createdAt : "");
    }
}
