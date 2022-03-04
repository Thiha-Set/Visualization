import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.File;
public class Driver{
    public static ArrayList<String[]> getData(String path){
        ArrayList<String[]> output = new ArrayList<>();

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
                    //store each data pair split by commas in instances
                    output.add(line.split(","));
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
        ArrayList<String[]> instances = new ArrayList<>();    //an arraylist containing all data split by commas

        //fill instances with all the data from the csv file
        instances = getData(path);

        //convert to JSON
        //try to create a new file in data
        try{
            //specify the directory to create the json file
            File jsonFile = new File("C:\\Users\\19175\\Desktop\\MOAF\\Adelphi\\HON 490\\Visualization\\docs\\jsonFiles\\data.json");

            //only create the file if the file does not exist in the specified location
            if(jsonFile.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File exists");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}