package com.agencyapi.functions;

import java.util.Random;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author okahia
 */

@RestController
public class Functions {
    

    public boolean isNullOrEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }

    public String generateReferenceNo() {
        String msgRef = "";
        int len = 12;

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//!@#$%&
            Random rnd = new Random();
            StringBuilder sb = new StringBuilder(len);
            sb.append("AG");
            for (int i = 0; i < len; i++)
                    sb.append(chars.charAt(rnd.nextInt(chars.length())));
            msgRef = sb.toString();

        return msgRef;
    }

}
