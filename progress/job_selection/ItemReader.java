package job_selection;

import helper_classes.*;
import utils.Location;

import java.util.HashMap;
import java.io.*;

public class ItemReader {
	public static HashMap<String, Item> parseItems(String wrfile, String lfile) {
		BufferedReader wrreader;
		BufferedReader lreader;
		String splitBy = ",";
		HashMap<String, Item> items = new HashMap<String, Item>();
		
		try {
			wrreader = new BufferedReader(new FileReader(wrfile));
			lreader = new BufferedReader(new FileReader(lfile));
			String wrline;
			String lline;
			while ((wrline = wrreader.readLine()) != null && (lline = lreader.readLine()) != null) {
				String[] wr = wrline.split(splitBy);
				String[] l = lline.split(splitBy);
				String iname = wr[0];
				float r = Float.parseFloat(wr[1]);
				float w = Float.parseFloat(wr[2]);
				Location loc = new Location(Integer.parseInt(l[0]), Integer.parseInt(l[1]), LocationType.EMPTY);
				Item i = new Item(iname, r, w, loc);
				items.put(iname, i);
			}
			
			wrreader.close();
			lreader.close();
			return items;
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			return null;
		} catch (IOException e) {
			System.out.println("IO Failed");
			return null;
		}
	}
}
