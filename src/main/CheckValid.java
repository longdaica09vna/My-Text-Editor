/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author LeSyThanhLong
 */
public class CheckValid {

    public boolean ckCharCode(String test) {
        if (test.contains("@")) {
            return false;
        }
        if (test.contains("#")) {
            return false;
        }
        if (test.contains("$")) {
            return false;
        }
        if (test.contains(" ")) {
            return false;
        }
        return true;
    }

    public boolean ckValidPhone(String test) {
        for (int i = 0; i < test.length(); i++) {
            if (!Character.isDigit(test.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean ckCharEmail(String test) {
        if (test.contains("!")) {
            return false;
        }
        if (test.contains("#")) {
            return false;
        }
        if (test.contains("$")) {
            return false;
        }
        return true;
    }

    public boolean ckValidEmail(String test) {
       String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return test.matches(regex);
    }

    public boolean ckValidName(String test) {
        if (!test.matches("[A-Za-z ]*")) {
            return false;
        }
        return true;
    }

    public boolean ckBirthDate(String test) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);
        try {
            Date date = formatter.parse(test);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
