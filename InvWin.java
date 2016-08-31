package lab08;

import stuff.MyClass;
import lab08.Main;
import lab08.Parts;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class InvWin extends JFrame {
	private Parts[] partList;
	private int index;

	public InvWin(Parts[] pL, int i) { //set the appearance in the constructor
		partList = pL;
		index = i;

		setSize(343,127);
		setTitle("Inventory Utility");
		setLayout(new FlowLayout());
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new ListenWin());

		JButton btnLookup, btnAdd, btnDelete, btnSale, btnShipment, btnReport, btnSave;

		btnLookup = new JButton("Look up a part");
		btnAdd = new JButton("Add a part");
		btnDelete = new JButton("Delete a part");
		btnSale = new JButton("Process a sale");
		btnShipment = new JButton("Receive a shipment");
		btnReport = new JButton("Show inventory report");

		btnLookup.addActionListener(new ListenLookup());
		btnAdd.addActionListener(new ListenAdd());
		btnDelete.addActionListener(new ListenDelete());
		btnSale.addActionListener(new ListenSale());
		btnShipment.addActionListener(new ListenShipment());
		btnReport.addActionListener(new ListenReport());

		add(btnLookup);
		add(btnAdd);
		add(btnDelete);
		add(btnSale);
		add(btnShipment);
		add(btnReport);
		// add(btnSave);
		
		setLocationRelativeTo(null); //center on screen
		setVisible(true);
	}

	private class ListenLookup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			LookupPart(partList, index);
		}
	}
	private class ListenAdd implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (index < partList.length) //add part only if there is room in array
				index = AddPart(partList, index);
			else
				MyClass.errorDialog("Inventory capacity reached.");
		}
	}
	private class ListenDelete implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			index = DeletePart(partList, index);
		}
	}
	private class ListenSale implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ProcessSale(partList, index);
		}
	}
	private class ListenShipment implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ReceiveShip(partList, index);
		}
	}
	private class ListenReport implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ShowReport(partList, index);
		}
	}
	private class ListenWin implements WindowListener {
		public void windowClosing(WindowEvent e) {
			SaveInv(partList, index);
			setVisible(false);
			dispose();
		} //in case it's not apparent right away, the rest are there only because they must be to implement WindowListener
		public void windowOpened(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
	}

	private static void LookupPart(Parts[] partList, int index) { //Show a part's data
		int searchIndex;
		String searchValue;

		while (true) {
			searchValue = JOptionPane.showInputDialog(null,"Part ID number: ","Which part?",JOptionPane.QUESTION_MESSAGE);
			if (searchValue != null)
				break;
		}

		//the above while statement used to be inside FindPart(), but I externalized it so that I could use FindPart() in AddPart()
		searchIndex = FindPart(partList, index, searchValue);

		if (searchIndex < index) { //if search succeeded, dialog with part info
			Font font = new Font("Lucida Console", Font.PLAIN, 12);

			JTextField textHead = new JTextField(ShowHeader());
			textHead.setFont(font);
			textHead.setEditable(false);		

			String part = ShowPart(partList, searchIndex);

			JTextField textPart = new JTextField(part);
			textPart.setFont(font);
			textPart.setColumns(72);
			textPart.setEditable(false);

			JScrollPane scrollPane = new JScrollPane(textPart);
			scrollPane.setColumnHeaderView(textHead);

			JOptionPane.showMessageDialog(null,scrollPane,"Part Report",JOptionPane.PLAIN_MESSAGE);
		} else
			if (searchIndex == index) //if search failed, error message
				MyClass.errorDialog("Part ID not found.");
	}

	private static int FindPart(Parts[] partList, int index, String searchValue) { //Determine appropriate part
		for (int searchIndex = 0; searchIndex < index; searchIndex++) { //compare ID of each part in array with search value
			if (searchValue.equals(partList[searchIndex].getId()))
				return searchIndex; //return index of matching part
		}

		return index; //only reached if part not found
	}

	private static int AddPart(Parts[] partList, int index) { //Add a part
		JTextField idNum = new JTextField();
		JTextField desc = new JTextField();
		JTextField loc = new JTextField();
		JTextField cost = new JTextField();
		JTextField price = new JTextField();
		JTextField weight = new JTextField();
		Object[] text = {"ID number", idNum, "Description", desc, "Location", loc, "Cost", cost, "Price", price, "Weight", weight}; //components of input dialog

		while (true) {
			if (JOptionPane.showConfirmDialog(null, text, "Add a part", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) { //show dialog, then read fields only if "OK" selected
				try { //create new part via constructor and all six fields of the dialog
					if (FindPart(partList,index,idNum.getText())==index) { //accept input only if ID number is not already in inventory
						partList[index] = new Parts(idNum.getText(),desc.getText(),loc.getText(),Double.parseDouble(cost.getText()),Double.parseDouble(price.getText()),Double.parseDouble(weight.getText()),0);
						break;
					} else
						MyClass.errorDialog("ID number already exists.");
				} catch (NumberFormatException e) { //if failed to parse any fields, loop again
					MyClass.errorDialog("Invalid entry.");
				}
			} else {
				return index;
			}
		}

		JOptionPane.showMessageDialog(null,"Part added.","Inventory Utility",JOptionPane.INFORMATION_MESSAGE);
		return ++index;
	}

	private static int DeletePart(Parts[] partList, int index) { //Delete a part
		int searchIndex;
		String searchValue;

		while (true) {
			searchValue = JOptionPane.showInputDialog(null,"Part ID number: ","Which part?",JOptionPane.QUESTION_MESSAGE);
			if (searchValue != null)
				break;
		}

		searchIndex = FindPart(partList, index, searchValue);

		if (searchIndex < index) //if search succeeded, overwrite part
			return OverwritePart(partList, index, searchIndex);
		else
			if (searchIndex == index) //if search failed, error message
				MyClass.errorDialog("Part ID not found.");
		return index;
	}

	private static int OverwritePart(Parts[] partList, int index, int searchIndex) { //Overwrite part data
		index--;
		//set the specified part's data to the data of the last part in the list
		partList[searchIndex].setInfo(partList[index].getId(),partList[index].getDesc(),partList[index].getLoc(),partList[index].getCost(),partList[index].getPrice(),partList[index].getWeight(),partList[index].getQty());
		JOptionPane.showMessageDialog(null,"Part removed.","Delete a part",JOptionPane.INFORMATION_MESSAGE);
		return index;
	}

	private static void ProcessSale(Parts[] partList, int index) { //Process a part sale
		int searchIndex, qtySold;
		String searchValue;

		while (true) {
			searchValue = JOptionPane.showInputDialog(null,"Part ID number: ","Which part?",JOptionPane.QUESTION_MESSAGE);
			if (searchValue != null)
				break;
		}

		searchIndex = FindPart(partList, index, searchValue);

		if (searchIndex < index) { //if search succeeds, get a quantity to sell
			qtySold = SubQty(partList, searchIndex);
			if (qtySold > 0) { //this is here to allow user to exit without creating a slip--when zero parts are in stock, it's impossible to leave the prompt otherwise
				ShowSlip(partList, searchIndex, qtySold);
			}
		} else
			if (searchIndex == index) //if search failed, error message
				MyClass.errorDialog("Part ID not found.");
	}

	private static int SubQty(Parts[] partList, int index) { //Subtract part quantity
		int qtySold;
		String temp;
		while (true) {
			temp = JOptionPane.showInputDialog(null,"Quantity sold: ","Process sale",JOptionPane.QUESTION_MESSAGE); //get quantity to sell
			if (temp != null)
				try {
					qtySold = Integer.parseInt(temp);
					if (qtySold > 0 && qtySold <= partList[index].getQty()) { //if input valid, change quantity in stock
						partList[index].modQty(-qtySold);
						return qtySold;
					} else
						if (qtySold <= 0) //if zero entered, error message
							MyClass.errorDialog("Please enter a positive number.");
						else //if not enough parts in stock, error message
							MyClass.errorDialog("Insufficient quantity in stock.");
				} catch (NumberFormatException e) {
					MyClass.errorDialog("Invalid entry.");
				}
			else
				return 0;
		}
	}

	private static void ShowSlip(Parts[] partList, int index, int qty) { //Show sales slip
		double subtotal, taxtotal, total;
		subtotal = qty * partList[index].getPrice();
		taxtotal = subtotal * Main.TAX_RATE;
		total = subtotal + taxtotal;

		JTextArea slip = new JTextArea(String.format("%-9s %s\n%-9s %5d\n%-9s %8.2f\n%-9s %8.2f\n%-9s %8.2f\n%-9s %8.2f","PART",partList[index].getDesc(),"QTY",qty,"PRICE",partList[index].getPrice(),"SUBTOTAL",subtotal,"TAX 7.75%",taxtotal,"TOTAL",total));
		slip.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		slip.setEditable(false);
		JOptionPane.showMessageDialog(null,slip,"Sale slip",JOptionPane.PLAIN_MESSAGE);
	}

	private static void ReceiveShip(Parts[] partList, int index) { //Receive a shipment
		int searchIndex;
		String searchValue;

		while (true) {
			searchValue = JOptionPane.showInputDialog(null,"Part ID number: ","Which part?",JOptionPane.QUESTION_MESSAGE);
			if (searchValue != null)
				break;
		}

		searchIndex = FindPart(partList, index, searchValue);

		if (searchIndex < index)
			AddQty(partList, searchIndex);
		else
			if (searchIndex == index) //if search failed, error message
				MyClass.errorDialog("Part ID not found.");
	}

	private static void AddQty(Parts[] partList, int index) { //Add part quantity
		int qtyRecvd;
		while (true) {
			try {
				qtyRecvd = Integer.parseInt(JOptionPane.showInputDialog("Quantity receiving: "));
				if (qtyRecvd > 0)
					break;
				else
					MyClass.errorDialog("Please enter a positive number.");
			} catch (NumberFormatException e) {
				MyClass.errorDialog("Invalid entry.");
			}
		}

		partList[index].modQty(qtyRecvd);
		JOptionPane.showMessageDialog(null,"Shipment added.","Inventory Utility",JOptionPane.INFORMATION_MESSAGE);
	}

	private static void ShowReport(Parts[] partList, int index) { //Show inventory data
		Font font = new Font("Lucida Console", Font.PLAIN, 12);

		JTextField textHead = new JTextField(ShowHeader());
		textHead.setFont(font);
		textHead.setEditable(false);		

		String parts = "";
		for (int i = 0; i < index; i++) {
			parts += ShowPart(partList, i);
		}

		JTextArea textParts = new JTextArea(parts);
		textParts.setFont(font);
		textParts.setColumns(72);
		textParts.setRows(20);
		textParts.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textParts);
		scrollPane.setColumnHeaderView(textHead);

		JOptionPane.showMessageDialog(null,scrollPane,"Inventory Report",JOptionPane.PLAIN_MESSAGE);
	}

	private static String ShowHeader() { //Show header
		return String.format("%-11s %-25s %5s %7s %7s %7s %3s","ID NUMBER","DESCRIPTION","LOCTN","COST","PRICE","WT-LBS","QTY");
	}

	private static String ShowPart(Parts[] partList, int index) { //Display part data
		return String.format("%-10s  %-25s %5s %7.2f %7.2f %7.2f %3d\n",partList[index].getId(),partList[index].getDesc(),partList[index].getLoc(),partList[index].getCost(),partList[index].getPrice(),partList[index].getWeight(),partList[index].getQty());
	}

	private static void SaveInv(Parts[] partList, int index) { //Save inventory 
		while (true) {
			try { //prompt for file name then write 7 lines each loop
				BufferedWriter outFile = new BufferedWriter(new FileWriter(JOptionPane.showInputDialog("File to store inventory to: ")));
				for (int i = 0; i < index; i++) {
					outFile.write(String.format("%s\n%s\n%s\n%.2f\n%.2f\n%.2f\n%d\n",partList[i].getId(),partList[i].getDesc(),partList[i].getLoc(),partList[i].getCost(),partList[i].getPrice(),partList[i].getWeight(),partList[i].getQty()));
				}
				outFile.close();
				JOptionPane.showMessageDialog(null,"Inventory stored successfully.","Inventory Utility",JOptionPane.INFORMATION_MESSAGE);
				return;
			} catch (FileNotFoundException e) {
				MyClass.errorDialog("Failed to create file.");
			} catch (IOException e) {
				MyClass.errorDialog("Failed to write to file.");
			} catch (NullPointerException e) {
				if (JOptionPane.showConfirmDialog(null,"Quit without saving?","Inventory Utility",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) //prompt to quit
					return;
			}
		}
	}
}