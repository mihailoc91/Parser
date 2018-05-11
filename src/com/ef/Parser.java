
package com.ef;

import com.ef.controller.Controller;
import java.time.LocalDateTime;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;





/**
 *
 * @author Mihailo
 */
public class Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         try {
            
            //Declaring and setting options --start
            Options o = new Options();
            o.addOption("a", "accesslog", true, "Request a path to the access.log file.");
            o.addRequiredOption("s", "startDate", true, "Request an argument that takes date and time.");
            o.addRequiredOption("d", "duration", true, "Request an argument that takes duration.");
            o.addRequiredOption("t", "threshold", true, "Request an argument that takes threshold.");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(o, args);
            //Declaring and setting options --end
            
            //Taking the value of command line arguments --start
            LocalDateTime startDate = Controller.getStartDate(o, args, cmd, parser);
            LocalDateTime endDate = Controller.getDuration(o, args, cmd, parser, startDate);
            int threshold = Controller.getThreshold(o, args, cmd, parser);
            String path = Controller.getAccessLog(o, args, cmd, parser);
            //Taking the value of command line arguments --end
            
            //Parsing and loading into database access.log file
            Controller.saveFileToDB(Controller.parseFile(path));
            
            //Searching for ip's to block and blocking them
            Controller.blockIps(startDate, endDate, threshold);
            
        } catch (ParseException ex) {
            System.out.println("You did not entered the required arguments. Enter startDate, duration and threshold and try again!.");
        }
    }
    
}
