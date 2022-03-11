import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.File;

//wrapper class for anomaly
class Anomaly{
    private String sIP;
    private String dIP;
    private String sPort;
    private String dPort;
    private String protocol;
    private String packets;
    private String bytes;
    private String flags;
    private String sTime;
    private String duration;
    private String eTime;
    private String sensor;

    public Anomaly(String sIPVal, String dIPVal, String sPortVal, String dPortVal, String protocolVal, String packetsVal, String bytesVal, String flagsVal, String sTimeVal, String durationVal, String eTimeVal, String sensorVal){
        this.sIP = sIPVal;
        this.dIP = dIPVal;
        this.sPort = sPortVal;
        this.dPort = dPortVal;
        this.protocol = protocolVal;
        this.packets = packetsVal;
        this.bytes = bytesVal;
        this.flags = flagsVal;
        this.sTime = sTimeVal;
        this.duration = durationVal;
        this.eTime = eTimeVal;
        this.sensor = sensorVal;
    }

    //accessor methods
    public String getsIP(){
        return new String(this.sIP);
    }

    public String getdIP(){
        return new String(this.dIP);
    }

    public String getsPort(){
        return new String(this.sPort);
    }

    public String getdPort(){
        return new String(this.dPort);
    }

    public String getProtocol(){
        return new String(this.protocol);
    }

    public String getPackets(){
        return new String(this.packets);
    }

    public String getBytes(){
        return new String(this.bytes);
    }

    public String getFlags(){
        return new String(this.flags);
    }

    public String getsTime(){
        return new String(this.sTime);
    }

    public String getDuration(){
        return new String(this.duration);
    }

    public String geteTime(){
        return new String(this.eTime);
    }

    public String getSensor(){
        return new String(this.sensor);
    }

    //toString method
    //override toString from Obj superclass
    public String toString(){
        return this.getsIP()+", "+this.getdIP()+", "+this.getsPort()+", "+this.getdPort()+", "+this.getProtocol()+", "+this.getPackets()+", "+this.getBytes()+", "+this.getFlags()+", "+this.getsTime()+", "+this.getDuration()+", "+this.geteTime()+", "+this.getSensor();
    }
}

public class Driver{
    

    public static ArrayList<Anomaly> getData(String path){
        ArrayList<Anomaly> output = new ArrayList<>();

        ArrayList<String> expectPorts = new ArrayList<>();
        expectPorts.add("53");
        expectPorts.add("88");
        boolean first = true;   //flag var to skip the first line (attributes)

        String line = "";       //current line
        
        //use BufferedReader to read input from the csv file
        //using a fileReader and specifying the path of the csv file
        //throws FileNotFound Exception if the file is not found in the specified path
        try{
            BufferedReader buffRead = new BufferedReader(new FileReader(path));
            
            //while the next line is not null
            while((line = buffRead.readLine()) != null){
                //Skip on reading the first line of the file (attributes)
                if(!first){

                    //look for anomalies before adding the line to the output
                    //we only add lines to instances if there are anomalies
                    //patterns to look for to spot anomalies? => what criteria to decide if anomaly?
                    //place some placeholders => EX: Unusual dIP for subnets

                    //most traffic will go to TCP port 80, 443, 135, 67
                    //Write program to exclude all expected traffic going to those ports
                    
                    
                    //check to see if dPort != 53 or 88
                    //get all attributes
                    String sIP = line.split(",")[0];
                    String dIP = line.split(",")[1];
                    String sPort = line.split(",")[2];
                    String dPort = line.split(",")[3];
                    String protocol = line.split(",")[4];
                    String packets = line.split(",")[5];
                    String bytes = line.split(",")[6];
                    String flags = line.split(",")[7];
                    String sTime = line.split(",")[8];
                    String duration = line.split(",")[9];
                    String eTime = line.split(",")[10];
                    String sensor = line.split(",")[11];
                    
                    //if we can't find the dPort in the list of
                    //expected ports, then add it to the output
                    if(expectPorts.indexOf(dPort)==-1){
                        //create new Anomaly obj
                        Anomaly curr = new Anomaly(sIP,dIP,sPort,dPort,protocol,packets,bytes,flags,sTime,duration,eTime,sensor);

                        output.add(curr);
                    }

                }

                //toggle flag var
                first = false;
            }
            //close the buffered reader
            buffRead.close();
        }
        //general exception catch statement
        catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }

    public static void createJSON(String path){
        
    }

    public static void main(String[]args){
        /**
         * REFERENCE FOR INDEX OF ATTRIBUTES (ACCESSING DATA FROM ARRAYLIST INSTANCES)
         * 0 : sIP
         * 1 : dIP
         * 2 : sPort
         * 3 : dPort
         * 4 : protocol
         * 5 : packets
         * 6 : bytes
         * 7 : flags
         * 8 : sTime
         * 9 : duration
         * 10 : eTime
         * 11 : sensor
         */
        //the absolute path of the file

        String path = "C:\\Users\\19175\\Desktop\\MOAF\\Adelphi\\HON 490\\Visualization\\docs\\Java\\netflow_sample";

        //vars 
        ArrayList<Anomaly> instances = new ArrayList<>();    //an arraylist containing all data split by commas

        //fill instances with all the data from the csv file
        instances = getData(path);

        System.out.println(instances.get(0));
    }
}