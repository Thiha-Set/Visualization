import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//10.3.16.14,10.2.10.51,3910,57028,6,3,2698,SPA,2022-02-24 00:01:48.984,3.000,2022-02-24 00:01:51.984,centerForRecreationAndSportsLab-v4
//return traffic => 57028 to 3910 originally 
//but this record = reply
//port # > 50,000 => client-side (generally)
//port # < 5,000 => servide-side (generally)
//wrapper class for anomaly

//combine sIP, dIP, sPort, dPort criteria
//EX:
//combination 10.20.11.33 tcp port 80 <- expected web traffic
//10.20.11.33 with tcp port 443 <- any record with those attributes in either sIP, dIP, sPort, dPort

public class combinedDriver {
    // wrapper for network pairs (composed of IP and portnums)
    static class NetworkPair {
        private String IP;
        private String portNum;

        public NetworkPair(String IPStr, String portNumStr) {
            this.IP = new String(IPStr);
            this.portNum = new String(portNumStr);
        }

        public NetworkPair(NetworkPair pairObj) {
            this.IP = new String(pairObj.getIP());
            this.portNum = new String(pairObj.getPortNum());
        }

        // getters
        public String getIP() {
            return new String(this.IP);
        }

        public String getPortNum() {
            return new String(this.portNum);
        }

        // toString
        public String toString() {
            return "(" + new String(this.IP) + ", " + new String(this.portNum) + ")";
        }
    }

    // wrapper for frequencies of network pairs
    static class NetworkPairFrequencies {
        private NetworkPair pair;
        private int frequency;

        public NetworkPairFrequencies(NetworkPair pairObj) {
            this.pair = new NetworkPair(pairObj);
            this.frequency = 0;
        }

        // getters
        public int getFrequency() {
            return this.frequency;
        }

        public NetworkPair getNetworkPair() {
            return new NetworkPair(this.pair);
        }

        // toString method
        public String toString() {
            return "(" + pair.getIP() + ", " + pair.getPortNum() + ") -> " + this.frequency;
        }

        // class methods
        public void incrementFreq() {
            this.frequency++;
        }

        public boolean equals(NetworkPair anotherPair) {
            return this.pair.getIP().equals(anotherPair.getIP())
                    && this.pair.getPortNum().equals(anotherPair.getPortNum());
        }
    }

    public static String getData(String path, String IPNum, String portNum) {
        int count = 0;
        String output = "(" + IPNum + " ," + portNum + ") -> ";

        boolean first = true; // flag var to skip the first line (attributes)

        String line = ""; // current line

        // use BufferedReader to read input from the csv file
        // using a fileReader and specifying the path of the csv file
        // throws FileNotFound Exception if the file is not found in the specified path
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(path));

            // while the next line is not null
            while ((line = buffRead.readLine()) != null) {
                // Skip on reading the first line of the file (attributes)
                if (!first) {

                    // look for anomalies before adding the line to the output
                    // we only add lines to instances if there are anomalies
                    // patterns to look for to spot anomalies? => what criteria to decide if
                    // anomaly?
                    // place some placeholders => EX: Unusual dIP for subnets

                    // most traffic will go to TCP port 80, 443, 135, 67
                    // Write program to exclude all expected traffic going to those ports

                    // check to see if dPort != 53 or 88
                    // get all attributes
                    String sIP = line.split(",")[0];
                    String dIP = line.split(",")[1];
                    String sPort = line.split(",")[2];
                    String dPort = line.split(",")[3];

                    // if sIP/dIP and sPort/dPort combinations match w/ current line
                    if ((sIP.equals(IPNum) || dIP.equals(IPNum)) && (sPort.equals(portNum) || dPort.equals(portNum))) {
                        // DEBUG:
                        // System.out.println(sIP+", "+dIP+", "+sPort+", "+dPort);

                        count++;
                    }
                }

                // toggle flag var
                first = false;
            }
            // close the buffered reader
            buffRead.close();
        }
        // general exception catch statement
        catch (Exception e) {
            e.printStackTrace();
        }
        return output + count;
    }

    // validateIP() -> helper method to validate IP format
    public static boolean validateIP(String IPString) {
        String[] splitIPString = IPString.split("\\.");
        return splitIPString.length == 4;
    }

    // getNetPairs() -> helper method to get user inputted network pairs
    public static ArrayList<NetworkPair> getNetPairs() {
        ArrayList<NetworkPair> output = new ArrayList<>();

        // declare new Scanner obj and ask user to enter in network pairs
        // with IP address and port num separated by a comma
        Scanner scan = new Scanner(System.in);
        System.out.println(
                "Enter IP address, followed by the port number (no spaces). Enter -1 to stop:\n (EX: 10.23.15.4,80)");

        String input = scan.nextLine();
        while (!(input.equals("-1"))) {
            try {
                String IP = input.split(",")[0];
                String portNum = input.split(",")[1];

                if (input.split(",").length > 2) {
                    throw new Exception();
                } else if (input.indexOf(" ") != -1) {
                    throw new Exception();
                }

                // validation for IP and portNums
                Integer.parseInt(portNum);
                boolean validIP = validateIP(IP);
                if (!validIP) {
                    System.out.println("not valid IP");
                    throw new Exception();
                }

                // if the format user entered is valid
                // append user input into the ArrayList output
                output.add(new NetworkPair(IP, portNum));

            }
            // general exception catch statement
            catch (Exception e) {
                System.out.println("Error: Unexpected exception. Try again");
            }
            // allow user to enter next input (if they haven't enter -1)
            input = scan.nextLine();
        }
        return output;
    }

    public static ArrayList<NetworkPairFrequencies> generateFreq(ArrayList<NetworkPair> pairList, String path) {
        ArrayList<NetworkPairFrequencies> output = new ArrayList<>();

        boolean first = true; // flag var to skip the first line (attributes)

        String line = ""; // current line

        // use BufferedReader to read input from the csv file
        // using a fileReader and specifying the path of the csv file
        // throws FileNotFound Exception if the file is not found in the specified path
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(path));

            for (NetworkPair curr : pairList) {
                
                NetworkPairFrequencies freqObj = new NetworkPairFrequencies(curr);
                // while the next line is not null
                while ((line = buffRead.readLine()) != null) {
                    // Skip on reading the first line of the file (attributes)
                    if (!first) {

                        // look for anomalies before adding the line to the output
                        // we only add lines to instances if there are anomalies
                        // patterns to look for to spot anomalies? => what criteria to decide if
                        // anomaly?
                        // place some placeholders => EX: Unusual dIP for subnets

                        // most traffic will go to TCP port 80, 443, 135, 67
                        // Write program to exclude all expected traffic going to those ports

                        // check to see if dPort != 53 or 88
                        // get all attributes
                        String sIP = line.split(",")[0];
                        String dIP = line.split(",")[1];
                        String sPort = line.split(",")[2];
                        String dPort = line.split(",")[3];

                        // check to see if any item in output
                        // has the same IP/portNum combination of the current
                        // line
                        if((curr.getIP().equals(sIP)||curr.getIP().equals(dIP))&&(curr.getPortNum().equals(sPort)||curr.getPortNum().equals(dPort))){
                            
                            freqObj.incrementFreq();
                        }
                        
                        
                    }
                    
                    // toggle flag var
                    first = false;
                    
                }
                output.add(freqObj);
                buffRead = new BufferedReader(new FileReader(path));
            }
            // close the buffered reader
            buffRead.close();
        }
        // general exception catch statement
        catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static void main(String[] args) {
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
        // the absolute path of the file

        String path = "C:\\Users\\19175\\Desktop\\MOAF\\Adelphi\\HON 490\\Visualization\\docs\\Java\\netflow_sample";

        // vars
        ArrayList<NetworkPair> netPairs = getNetPairs();

        ArrayList<NetworkPairFrequencies> netFreq = generateFreq(netPairs, path);
        
        for(NetworkPairFrequencies curr:netFreq){
            System.out.println(curr);
        }
        
        
    }
}