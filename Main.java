/*	Program Name:	Lab 08
	Programmer:		Marcus Ross
	Date Due:		08 Nov 2013
	Description:	This program will help the user maintain an inventory of parts. It gets all current inventory data from a file on startup and then provides them with a menu at which they can choose between looking up a part, adding a part, removing a part, receiving a shipment of parts, printing a sales slip, displaying the inventory listing, and quitting.	*/

package lab08;

import stuff.MyClass;
import lab08.Parts;
import lab08.InvWin;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Main {
	static final double TAX_RATE = 0.0775;

	public static void main(String args[]) {
		int index;
		Parts[] partList;

		index = 0;
		partList = new Parts[250];

		while (true) {
			try {
				index = GetInvData(partList, index, JOptionPane.showInputDialog(null,"Inventory file path: ","Inventory Utility",JOptionPane.QUESTION_MESSAGE)); //prompt for file name
				break;
			} catch (FileNotFoundException e) {
				MyClass.errorDialog("File not found.");
			} catch (IOException e) {
				MyClass.errorDialog("Failed to read file.");
			} catch (NumberFormatException e) { //error if file does not contain expected data
				MyClass.errorDialog("Invalid file format.");
			} catch (NullPointerException e) { //prompt to quit if file name prompt was cancelled
				if (JOptionPane.showConfirmDialog(null,"Quit?","Inventory Utility",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					return; //shoudn't return be the best way to end the program from main()?
			}
		}

		new InvWin(partList,index); //Show menu
	}

	public static int GetInvData(Parts[] partList, int index, String fileName) throws FileNotFoundException, IOException, NumberFormatException, NullPointerException { //Get inventory data from file
		BufferedReader inFile = new BufferedReader(new FileReader(fileName));

		while (inFile.ready()) { //read 7 lines each loop to construct a part
			partList[index] = new Parts(inFile.readLine(),inFile.readLine(),inFile.readLine(),Double.parseDouble(inFile.readLine()),Double.parseDouble(inFile.readLine()),Double.parseDouble(inFile.readLine()),Integer.parseInt(inFile.readLine()));
			index += 1; //increment logical length of subarray
		}

		Arrays.sort(partList, new PartSorter()); //sort part list by ID number

		return index; //return logical length
	}

	public static class PartSorter implements Comparator<Parts> {
		@Override
		public int compare(Parts part1, Parts part2) {
			try { 
				return part1.getId().compareTo(part2.getId());
			} catch (NullPointerException e) {
				return 0;
			}
		}
	}
}