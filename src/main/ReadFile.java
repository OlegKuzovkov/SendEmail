package main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadFile {
			@SuppressWarnings("finally")
			String[] Read(String file){
				BufferedReader br = null;
				String[] out = null;
				try {
		 
					String sCurrentLine;
		 
					br = new BufferedReader(new FileReader(file));
					
					ArrayList<String> arr = new ArrayList<String>();
					while ((sCurrentLine = br.readLine()) != null) {
						arr.add(sCurrentLine);
					}
					out = new String[arr.size()];
					arr.toArray(out);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)br.close();
					} catch (final IOException ex) {
						ex.printStackTrace();
					}
					return out;
				}
	}

}
