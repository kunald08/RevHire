package com.revhire.app;

import com.revhire.util.DBUtil;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        Connection con = DBUtil.getConnection();
        if (con != null) {
            System.out.println("✅ Database Connected Successfully!");
        }
    }
}
