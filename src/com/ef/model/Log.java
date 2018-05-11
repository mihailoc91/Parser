/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Mihailo
 */
public class Log {

 // Attributes --start
    private int logID;
    private LocalDateTime dateTime;
    private String ip;
    private String request;
    private String status;
    private String userAgent;
// Attributes --end
    
    /**
     *Class constructor, sets class attributes.
     * 
     * @param id - id from database.
     * @param dateTime - date and time of access to web server. 
     * @param ip - ip address.
     * @param request - HTTP request.
     * @param status - server status.
     * @param userAgent - user agent.
     */
    public Log (int id,String dateTime, String ip, String request, String status,String userAgent){
        this.logID=id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.dateTime = LocalDateTime.parse(dateTime,formatter);
        this.ip=ip;
        this.request=request;
        this.status=status;
        this.userAgent=userAgent;
    }
    

    /**
     *Class constructor, sets class attributes.
     * 
     * @param dateTime - date and time of access to web server. 
     * @param ip - ip address.
     * @param request - HTTP request.
     * @param status - server status.
     * @param userAgent - user agent.
     */
    public Log (String dateTime, String ip, String request, String status,String userAgent){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.dateTime = LocalDateTime.parse(dateTime,formatter);
        this.ip=ip;
        this.request=request;
        this.status=status;
        this.userAgent=userAgent;
    }
    
    
    /**
     *Saves a Log object into database, and sets LogID.
     * 
     * @throws SQLException
     */
    public void saveLog () throws SQLException{
        DBConnect dataBase = DBConnect.getInstance();
        PreparedStatement preparedStatment = dataBase.conn.prepareStatement("INSERT INTO log VALUES (null,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
        preparedStatment.setString(1, this.getDateTime().toString());
        preparedStatment.setString(2, this.getIp());
        preparedStatment.setString(3, this.getRequest());
        preparedStatment.setString(4, this.getStatus());
        preparedStatment.setString(5, this.getUserAgent());
        preparedStatment.execute();
        ResultSet resultSet = preparedStatment.getGeneratedKeys();
        if(resultSet.next()){
            this.setLogID(resultSet.getInt(1));
        }
        
        
    }
    
     /**
     * @return the logID
     */
    public int getLogID() {
        return logID;
    }

    /**
     * @param logID the logID to set
     */
    public void setLogID(int logID) {
        this.logID = logID;
    }

    /**
     * @return the dateTime
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent the userAgent to set
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
}
