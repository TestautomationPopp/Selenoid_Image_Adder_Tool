package TwsSelenoidStarter.SelenoidStarter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Hello world!
 *
 */
public class SelenoidStarter 
{
    public static void main( String[] args )
    {
    	System.out.println("########################################################################");
    	System.out.println("#                  Test With a Smile Selenoid Starter                  #");
    	System.out.println("#                       Written By Andreas Popp                        #");
    	System.out.println("#           For more infos visit https://test-with-a-smile.de          #");
    	System.out.println("########################################################################");
    	
    	if (args.length != 3) {
    		System.out.println("Parameter Error");
    		System.out.println("Please call wie the following Parameters:");
    		System.out.println("java -jar SelenoidStarter.jar PathToBrowserJSON, BrowserName, BrowserVersion");
    		System.exit(1);
    	}
    	
    	
    	JSONParser jsonParser = new JSONParser();

    	String pathToBrowserJSON = args[0];
        if(!pathToBrowserJSON.endsWith("/") || !pathToBrowserJSON.endsWith("\\")) {
        	pathToBrowserJSON = pathToBrowserJSON + "/";
        }
    	String browserName = args[1];
    	String versionName = args[2];
    	
    	Object obj = null;
        
        try {
			obj = jsonParser.parse(new 
			        FileReader(pathToBrowserJSON + "browsers.json"));
		} catch (FileNotFoundException e) {
			System.out.println("browser.json not found at " + pathToBrowserJSON + "browsers.json");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception while reading " + pathToBrowserJSON + "browsers.json");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("JSON Parser Exception while reading " + pathToBrowserJSON + "browsers.json");
			e.printStackTrace();
		}
        
        JSONObject jsonObject = (JSONObject) obj;

        JSONObject browser = (JSONObject) jsonObject.get(browserName);
        
        JSONObject versions = (JSONObject) browser.get("versions");
        
        JSONObject version = (JSONObject) versions.get(versionName);
        
        
        if (version == null) {

            JSONObject newTmpfs = new JSONObject();
            newTmpfs.put("/tmp", "size=512m");
        	
            JSONObject newBrowser = new JSONObject();
            newBrowser.put("image", "selenoid/" + browserName + ":" + versionName);
            newBrowser.put("port", "4444");
            newBrowser.put("tmpfs", newTmpfs);
            
            
        	versions.put(versionName, newBrowser);
        	

            BufferedWriter writer = null;
            try {
				writer = new BufferedWriter(new FileWriter(pathToBrowserJSON + "browsers.json"));
				writer.write(jsonObject.toString());
				writer.close();
			} catch (IOException e) {
				System.out.println("IO Exception while writing " + pathToBrowserJSON + "browsers.json");
				e.printStackTrace();
			}

            String cmd = "docker restart selenoid_selenoid_1 && docker restart selenoid_selenoid-ui_1";
            Runtime run = Runtime.getRuntime();
            Process pr = null;
			try {
				pr = run.exec(cmd);
				pr.waitFor();

			} catch (IOException e) {
				System.out.println("IO Exception while restarting the Selenoid Docker containers");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception while restarting the Selenoid Docker containers");
				e.printStackTrace();
			}
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            System.out.println("Added " + browserName + " Version " + versionName + " to browser.json and restarted");
        }else {
        	System.out.println("Browser already in browser.json. No restart necessary.");
        }
        

        

        
        
    }
}
