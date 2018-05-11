/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mihailo
 */
public class Ip {

    // Attributes --start
    private String ip;
    private String comment;
    // Attributes --end
    
    
    @Override
    public String toString(){
        return this.ip;
    }
    
    /**
     *Searches in database for ip's that have exceeded the threshold for given date and time.
     * 
     * @param startDateTime - start date and time. 
     * @param endDateTime - end date and time.
     * @param threshold - threshold allowed for a given period of time.
     * 
     * @throws SQLException
     */
    public static Map <Integer,Ip> searchForIpsToBlock(LocalDateTime startDateTime,LocalDateTime endDateTime, int threshold) throws SQLException{
        Map <Integer,Ip> map = new HashMap <>();
        int counter = 0;
        DBConnect dataBase = DBConnect.getInstance();
        PreparedStatement preparedStatement = dataBase.conn.prepareStatement("SELECT DISTINCT ip FROM log WHERE date BETWEEN ? and ? GROUP BY ip HAVING COUNT(ip) > ?");
        preparedStatement.setString(1, startDateTime.toString());
        preparedStatement.setString(2, endDateTime.toString());
        preparedStatement.setInt(3, threshold);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
           Ip ip = new Ip();
           ip.setIp(resultSet.getString(1));
           map.put(counter++, ip);
        }
        return map;
    }
    
    /**
     * Inserts into database ip that needs to be blocked.
     * 
     * @throws SQLException
     */
    public void insertBlockedIpIntoDB () throws SQLException{
        DBConnect dataBase = DBConnect.getInstance();
        PreparedStatement preparedStatement = dataBase.conn.prepareStatement("INSERT INTO blocked_ips VALUES(?,?)");
        preparedStatement.setString(1, this.getIp());
        preparedStatement.setString(2, this.getComment());
        preparedStatement.execute();
        
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
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
