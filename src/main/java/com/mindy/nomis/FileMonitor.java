package com.mindy.nomis;

/**
 * File Monitor
 *
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Locale;

public class FileMonitor
{
    public static void main( String[] args )
    {
        
        System.out.println("Welcome to File Monitor");
        
        while (true) {
            System.out.println("Please enter a segment (segment is defined as balance, term, region, origin dat)");
            Scanner scan = new Scanner(System.in);
            String segmentString = scan.nextLine();
            
            if (segmentString.compareTo("quit") != 0) {
                //System.out.println("segmentString = " + segmentString);
                String[] segmentArray = segmentString.split(" ");

                /*
                for (int i=0; i<segmentArray.length; i++) {
                    System.out.println(i + " = " + segmentArray[i]);
                }
                */

                ObjectMapper mapper = new ObjectMapper();

                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader("segments.json"));
                    JsonNode root = mapper.readTree(fileReader);

                    boolean matched = false;
                    String debug = root.get("debug").asText();
                    if (debug.compareTo("on") == 0) {
                        // Check for balance
                        int minBalance = root.with("segment").with("balance").get("min").asInt();
                        int maxBalance= root.with("segment").with("balance").get("max").asInt();
                        int inputBalance = Integer.parseInt(segmentArray[0]);
                        if (inputBalance > minBalance && inputBalance < maxBalance) {

                            // Check for term
                            int term = root.with("segment").get("term").asInt();
                            if (Integer.parseInt(segmentArray[1]) == term) {

                                //Check for region                      
                                String region = root.with("segment").get("region").asText();
                                if (segmentArray[2].toLowerCase().compareTo(region.toLowerCase()) == 0) {

                                    // Check for originDate
                                    String originDate= root.with("segment").get("originDate").asText();
                                              
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("MMM,yyyy").parse(originDate);
                                    }
                                    catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Calendar originDateStart = Calendar.getInstance();                              
                                    originDateStart.setTime(date);
                                    originDateStart.set(Calendar.DATE, 1);
                                    int maxDay = originDateStart.getActualMaximum(Calendar.DATE);                                    

                                    Calendar originDateEnd = Calendar.getInstance();
                                    originDateEnd.setTime(date);
                                    originDateEnd.set(Calendar.DATE, maxDay);
                                    
                                    try {
                                        date = new SimpleDateFormat("dd-MMM-yyyy").parse(segmentArray[3]);
                                    }
                                    catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Calendar inputDate = Calendar.getInstance();
                                    inputDate.setTime(date);
                                    if ((inputDate.compareTo(originDateStart) > 0) && (inputDate.compareTo(originDateEnd) < 0)) {
                                        matched = true;
                                    }
                                    /*
                                    System.out.println("originDateStart = " + originDateStart.get(Calendar.YEAR) + "/" +
                                            originDateStart.get(Calendar.MONTH) + "/" +
                                            originDateStart.get(Calendar.DATE));
                                    System.out.println("originDateEnd = " + originDateEnd.get(Calendar.YEAR) + "/" +
                                            originDateEnd.get(Calendar.MONTH) + "/" +
                                            originDateEnd.get(Calendar.DATE));
                                    System.out.println("inputDate = " + inputDate.get(Calendar.YEAR) + "/" +
                                            inputDate.get(Calendar.MONTH) + "/" +
                                            inputDate.get(Calendar.DATE));
                                    */
                                   
                                }
                            }
                        }

                        if (matched) {
                            System.out.println("Segment matches");
                        }
                        else {
                            System.out.println("Segment doesn't matches");
                        }
                    }
                } catch (JsonGenerationException e) {
                        e.printStackTrace();
                } catch (JsonMappingException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }               
            }
            else {
                break;
            }
        }        
    }
}
