package com.coop.mwalletapi.functions;

import com.coop.mwalletapi.commons.ApiUsers;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author okahia
 */
public class Functions {
    public boolean isNullOrEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }
	
	public String formatAmounts(double inStr){
        return String.format("%,.2f", inStr);
    }
    
    public String encodeString(String stringToEncode){
        String encodedString = "";
        if (!isNullOrEmpty(stringToEncode)){
           encodedString = new String(Base64.getEncoder().encode(stringToEncode.getBytes()));
        }
        return encodedString;
    }
    
    public String decodeString(String stringToDecode){
        String returnStringDecoded = "";
        if (!isNullOrEmpty(stringToDecode)){
            returnStringDecoded = new String(Base64.getDecoder().decode(stringToDecode));
        }
        return returnStringDecoded;
    }
    
    public int ValidateApiUser(List<ApiUsers> apiUsersAll, String userNameIn, String passwordIn){
        int retVal = 0;
        List<ApiUsers> usersFiltered = null;
        
        Comparator<ApiUsers> compareByUserId = Comparator
            .comparing(ApiUsers::getUserId);
        
        if (apiUsersAll != null){
            usersFiltered = apiUsersAll.stream()
                .filter(n-> n.getUserName().trim().equalsIgnoreCase(userNameIn))
                .sorted(compareByUserId)
                .collect(Collectors.toList());
            
            if (!usersFiltered.isEmpty()) {
                if (decodeString(usersFiltered.get(0).getPassword().trim()).equalsIgnoreCase(passwordIn)){
                    retVal = Integer.parseInt(usersFiltered.get(0).getEntityId().trim());
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }

        return retVal;
    }
    
    public boolean emailValidate(String emailInput) {
        Matcher matcher = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailInput);

        return matcher.find();
    }
    
    public boolean passwordValidate(String passwd) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        //Explanations:
        //(?=.*[0-9]) a digit must occur at least once
        //(?=.*[a-z]) a lower case letter must occur at least once
        //(?=.*[A-Z]) an upper case letter must occur at least once
        //(?=.*[@#$%^&+=]) a special character must occur at least once
        //(?=\\S+$) no whitespace allowed in the entire string
        //.{8,} at least 8 characters
        
        return passwd.matches(pattern);
    }
    
    public boolean hasSpecialCharacters(String stringIn) {
        Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        if (pattern.matcher(stringIn).find()){
            return true;
        }
        return false;
    }
       
    
    public boolean validateDate(String dateIn){
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateIn);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    
    public String formatDate(String dateIn){
        String DateOut = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date formatedDate = null;
        try {
            formatedDate = formatter.parse(dateIn);
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        DateOut = formatter.format(formatedDate);
        return DateOut;
    }
    
    public boolean validateDateTime(String dateIn){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(dateIn);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    
    public String formatDateTime(String dateIn){
        String DateOut = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date formatedDate = null;
        try {
            formatedDate = formatter.parse(dateIn);
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        DateOut = formatter.format(formatedDate);
        return DateOut;
    }
               
    public boolean validateDateIsToday(String dateToday, String dateIn){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Date dateTodai = null;
        Date datePassed = null;
        
        try {
            dateTodai = sdf.parse(dateToday);
            datePassed = sdf.parse(dateIn);
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(datePassed.after(dateTodai)){
            return false;
        }
        
        if(datePassed.before(dateTodai)){
            return false;
        }

        return true;
    }
    
    public boolean validateDob(String dateToday, String dateIn){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        
        Date dateTodai = null;
        Date datePassed = null;
        
        try {
            dateTodai = sdf.parse(dateToday);
            datePassed = sdf.parse(dateIn);
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(datePassed.after(dateTodai)){
            return false;
        }
        
        if(datePassed.equals(dateTodai)){
            return false;
        }

        return true;
    }
    
	public String formatDob(String dateIn){
        String DateOut = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //1998-09-15
        Date formatedDate = null;
        try {
            formatedDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateIn);
        } catch (ParseException ex) {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        DateOut = formatter.format(formatedDate);
        return DateOut;
    }
    
	public String randomStrGen() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yearMonthStr = sdf.format(date);

        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 12; //final string length
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .toUpperCase();

        generatedString = yearMonthStr.concat(generatedString);

        return generatedString;
    }
}
