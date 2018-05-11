/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.controller;

import com.ef.model.Ip;
import com.ef.model.Log;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Mihailo
 */
public class Controller {
    
/**
 * Parse the given file and fill the Map with Log objects.
 * 
 * @param path - path to the file.
 * 
 * @return map of Log objects. 
 */    
    public static Map <Integer,Log> parseFile (String path){  
        InputStream fileInputStream=null;
        try{
            if(path.equalsIgnoreCase("access.log")){
                fileInputStream = Controller.class.getClassLoader().getResourceAsStream(path);
            }else{
                fileInputStream = new FileInputStream(path);
            }
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(fileInputStream));
            System.out.println("Reading file...");
            String line;
            int counter = 0;
            Map <Integer,Log> logMap = new HashMap<>();
            while((line=bufferedReader.readLine())!=null){
                String [] array = line.split("\\|");
                Log log = new Log (array[0],array[1],array[2],array[3],array[4]);
                logMap.put(counter++, log);
            }
           
            return logMap;
        }catch(FileNotFoundException fnfex){
            System.out.println("File not found. Check the path and try again!");
            System.exit(1);
        }catch(IOException ioex){
            System.out.println("Could not read from file.");
            System.exit(1);
        }finally{
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    
                }
            }
        }
        return null;
    }

/**
 * Saves Log objects from a map into database.
 * 
 * @param map - map <Integer,Log> filled with Log objects.
 */    
    public static void saveFileToDB (Map <Integer,Log> map){
        try{
            System.out.println("Saving log to database...");
            for(int i=0;i<map.size();i++){
                map.get(i).saveLog();
            }
        }catch(SQLException ex){
            System.out.println("Could not save log to database. Check your settings and try again!");
            System.exit(1);
        }
    }
    
    
    /**
     * Search in database if there are any ip's that needs to be blocked, if there is, then it blocks them and write them on console.
     * 
     * @param startDateTime  - start date and time.
     * @param endDateTime - end date and time.
     * @param threshold - threshold allowed.
     * 
     */
    public static void blockIps (LocalDateTime startDateTime,LocalDateTime endDateTime, int threshold){
        try{
            Map <Integer, Ip> map = Ip.searchForIpsToBlock(startDateTime, endDateTime, threshold);

            for(int i=0; i< map.size();i++){
                Ip ip = map.get(i);
                try{
                    ip.setComment("This ip is blocked on " +LocalDateTime.now()+" because it tried to access web server on "+startDateTime+" to "+endDateTime+" more times then it is allowed by the threshold("+ threshold +").");
                    ip.insertBlockedIpIntoDB();
                    System.out.println("Ip address "+ip+" is added to block list.");
                }catch(MySQLIntegrityConstraintViolationException e){
                    String [] message = e.getMessage().split("'");
                    System.out.println("Ip address " + message[1] + " is already in block list! ");
                }
            }
        }catch(SQLException ex){
            System.out.println("Could not block ip's. Check your settings and try again.");
            System.exit(1);
        }
    }
    
  /**
   *Parses startDate argument and checks if it's in the right format.
   * 
   * @param options - class Options.
   * @param args - array of arguments.
   * @cmd - class CommandLine.
   * @parser - class CommandLineParser.
   * 
   * @return LocalDateTime object.
   * 
   * @throws ParseException
   */   
    
  public static LocalDateTime getStartDate (Options options,String[] args,CommandLine cmd,CommandLineParser parser) throws ParseException{
        cmd = parser.parse(options, args);
       
        if(cmd.hasOption("s")){
            String startDate = cmd.getOptionValue("s");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd.HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                LocalDateTime date =LocalDateTime.parse(startDate, formatter);
                return date;
            }catch (DateTimeParseException ex) {
                System.out.println("You have entered the wrong startDate format. Please enter format yyyy-MM-dd.HH:mm:ss and try again!");
                System.exit(1);
            }
        }
        return null;
}
  
  /**
   *Parses duration argument and checks if it's in the right format, and converts it in end date and time.
   * 
   * @param options - class Options.
   * @param args - array of arguments.
   * @cmd - class CommandLine.
   * @parser - class CommandLineParser.
   * 
   * @return LocalDateTime object.
   * 
   * @throws ParseException
   */ 
  public static LocalDateTime getDuration (Options options,String[] args,CommandLine cmd,CommandLineParser parser,LocalDateTime startDate)throws ParseException{
        cmd = parser.parse(options, args);
       
        if(cmd.hasOption("d")){
                String duration = cmd.getOptionValue("d").toLowerCase();
                try {
                    LocalDateTime endDate;
                    endDate = duration.contentEquals("hourly")? startDate.plusHours(1):duration.contentEquals("daily")? endDate = startDate.plusDays(1) : null;
                    if(endDate==null){
                        throw new NullPointerException();
                    }
                    return endDate;
                } catch (NullPointerException ex) {
                    System.out.println("You have entered the wrong duration format. Please enter hourly or daily and try again!");
                    System.exit(1);
                }
            }
        return null;
  }
  
  /**
   *Parses threshold argument and checks if it's in the right format.
   * 
   * @param options - class Options.
   * @param args - array of arguments.
   * @cmd - class CommandLine.
   * @parser - class CommandLineParser.
   * 
   * @return int
   * 
   * @throws ParseException
   */ 
  public static int getThreshold (Options options,String[] args,CommandLine cmd,CommandLineParser parser) throws ParseException{
        cmd = parser.parse(options, args);
       
        if(cmd.hasOption("t")){
            String threshold = cmd.getOptionValue("t");
            try {
                int th = Integer.parseInt(threshold);
                return th;
            } catch (NumberFormatException ex) {
                System.out.println("You have entered the wrong threshold format. Please enter whole number like 100 and try again!");
                System.exit(1);
            }
        }
        return 0;
}
  
  
  /**
   *Parses accesslog argument.
   * 
   * @param options - class Options.
   * @param args - array of arguments.
   * @cmd - class CommandLine.
   * @parser - class CommandLineParser.
   * 
   * @return Path object with a path to access.log file.
   * 
   * @throws ParseException
   */ 
  public static String getAccessLog (Options options,String[] args,CommandLine cmd,CommandLineParser parser) throws ParseException{
        cmd = parser.parse(options, args);
        String path;
        if(cmd.hasOption("a")){
            path = cmd.getOptionValue("a");
            return path;
        }else{
            path="access.log";
            return path;
        }
}
    
 
}
