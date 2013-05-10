package TestCases;

import gui.HTTPLib;
import gui.StudentGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import server.Utils;

public class GridTable {

	// It is required the set the number of rows based on the number of homeworks
	private int columns = 4;
	private int rows = 3;

	private String privateKey = "";
	private String preDefinedPathOfPrivateKey = "/Users/tdgunes/homeworks/privatekey.txt";



	public static void main(String[] args) {
		GridTable gd = new GridTable();
		gd.initializeTable();
	}


	public void initializeTable(){

		// Ask for the private key
		this.privateKey = this.getPrivateKey();
		System.out.println("Using the key: "+this.privateKey);


		Object[][] data = {};
		String hostName = "http://localhost:8000/";


		//starting the loop
		data = this.fetchData(data,hostName);
		//        System.out.println( data.toString());



		JFrame frame = new JFrame("");
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(columns*150,rows*30);
		frame.setLayout(new BorderLayout());

		// This is the margin on the left-hand side of the frame
		JLabel margin = new JLabel("    ");
		frame.add(margin, BorderLayout.WEST);

		JPanel panel = new JPanel(new GridLayout(rows, columns));

		
		addTitles(panel);

		for(Object[] o: data)
			for(Object a: o)
				System.out.println((String) a);

		// Set number of rows based on the data
		rows = data.length + 1;

		System.out.println(data.length);
		System.out.println(data[1].length);


		for(int i=0; i< (rows-1)*columns; i++){
			if(i%columns != columns-1)
				panel.add(new JLabel( (String) data[i/columns][i%columns]));
			else{
				JButton b = new JButton((String) data[i/columns][i%columns]);
				b.addActionListener(new ButtonHandler());
				panel.add(b);
			}
		}
		frame.add(panel);
		frame.setVisible(true);
	}

	
	/*
	 * Adds titles of the GUI
	 */
	private void addTitles(JPanel panel) {
		JLabel hwName = new JLabel("Homework Name");

		// Sets the font to bold
		Font newLabelFont=new Font(hwName.getFont().getName(),Font.BOLD,hwName.getFont().getSize());
		hwName.setFont(newLabelFont);

		JLabel deadline = new JLabel("Deadline");
		deadline.setFont(newLabelFont);

		JLabel grade = new JLabel("Grade");
		grade.setFont(newLabelFont);

		JLabel action = new JLabel("Action");
		action.setFont(newLabelFont);

		panel.add(hwName);
		panel.add(deadline);
		panel.add(grade);
		panel.add(action);
	}


	// Private Key is used to authenticate Students
	private String getPrivateKey() {
		String readFile = Utils.readFromFile(this.preDefinedPathOfPrivateKey);
		String reply = "";
		if (readFile.equals("FNF") ||
				readFile.equals("") || readFile.equals("null")){

			reply = JOptionPane.showInputDialog(null,"Welcome to GrayDeer,"
					+ "as for first start, you need to enter your private-key,"
					+ " which is given by your instructor to you", "your private key!");
			try {
				//writing the given reply to the predefined path     
				Utils.writeAFile(this.preDefinedPathOfPrivateKey, reply);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else {
			reply = readFile;
		}

		return reply;
	}

	private Object[][] fetchData(Object[][] data, String hostName) {

		try {

			// get privatekey from a window



			HTTPLib httpLib = new HTTPLib(hostName, privateKey);
			int listNum = 0;
			String response = httpLib.postData("fetch","");
			System.out.println("||| GrayDeer POST response: " + response);
			String[] lines = HTTPLib.splitItWithString(response, "+=+");
			//fetched data

			listNum = lines.length;
			System.out.println("ListNum: " + listNum);
			data = new Object[listNum][4];

			for (int i = 0; i < lines.length; i++) {
				
				String string = lines[i];
				System.out.println(string);
				String[] parsedItems = HTTPLib.splitItWithString(string, "**");
				System.arraycopy(parsedItems, 0, data[i], 0, 4);
		
			}
			return data;
		} catch (ConnectException er) {
			//unable to get the list, server is unavailable

			int reply = JOptionPane.showConfirmDialog(null, "Unable to access to the host(\"" + hostName + ")\"\n"
					+ "Do you want to try to connect again ?", "Warning!", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				//looping :)
				return this.fetchData(data, hostName);
			} else {
				System.exit(0);
			}
		} catch (MalformedURLException ex) {
			Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;

	}
}

class ButtonHandler implements ActionListener {
	public ButtonHandler(){
	}

	public void actionPerformed(ActionEvent e){
		FileChooser fc = new FileChooser();
		fc.showFileChooser();
	}

}