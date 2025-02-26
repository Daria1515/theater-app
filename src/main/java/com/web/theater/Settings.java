package com.web.theater;

import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

//КЛАСС СЕРВИСНЫХ ОПЕРАЦИЙ
public class Settings {
    //подключение к серверу MySQL
    public static Connection GetConnection() throws ClassNotFoundException, SQLException {
        Class.forName(ApplicationProperties.getProperty(ApplicationProperties.DRIVER_SQL));
        return DriverManager.getConnection(ApplicationProperties.getProperty(ApplicationProperties.CONNECTION_STRING_SQL),
                ApplicationProperties.getProperty(ApplicationProperties.USER_SQL),
                ApplicationProperties.getProperty(ApplicationProperties.PASSWORD_SQL));
    }
    static String secretKey = "SECRETKEYSECRETKEYSECRETKEYSECRETKEYSECRETKEY123456890SECRETKEYSECRETKEY";//секретный ключ
    //кодирование пароля
    public static String Encrypt(String plainText) {
        StringBuffer encryptedString = new StringBuffer();
        for(int i = 0; i < plainText.length(); ++i) {
            int plainTextInt = plainText.charAt(i) - 32;
            int secretKeyInt = secretKey.charAt(i) - 32;
            int encryptedInt = (plainTextInt + secretKeyInt) % 93;
            encryptedString.append((char)(encryptedInt + 32));
        }
        return encryptedString.toString();
    }
    //расшифровка пароля
    public static String Decrypt(String decryptedText) {
        StringBuffer decryptedString = new StringBuffer();
        for(int i = 0; i < decryptedText.length(); ++i) {
            int decryptedTextInt = decryptedText.charAt(i) - 32;
            int secretKeyInt = secretKey.charAt(i) - 32;
            int decryptedInt = decryptedTextInt - secretKeyInt;
            if (decryptedInt < 1) decryptedInt += 93;
            decryptedString.append((char)(decryptedInt + 32));
        }
        return decryptedString.toString();
    }
    //проверка на существование сессии
    public static boolean CheckSession(HttpSession session) {
        if (session.getAttribute("UserStatus") == null) {
            return false;
        } else {
            return !session.getAttribute("UserStatus").toString().equals("");
        }
    }
    //получение даты в текстовом формате (конвертация даты SQL в текст)
    public static String GetDateText(Date date){
        String result = "";
        if(date!=null) {
            int year, month, day;
            LocalDate localDate = date.toLocalDate();
            day = localDate.getDayOfMonth();
            month = localDate.getMonthValue();
            year = localDate.getYear();
            if (day < 10) result = "0" + Integer.toString(day) + ".";
            else result += Integer.toString(day) + ".";
            if (month < 10) result += "0" + Integer.toString(month) + ".";
            else result += Integer.toString(month) + ".";
            result += Integer.toString(year);
        }
        return result;
    }
}
