package com.c3ssoftware;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//import com.c3ssoftware.process.Process;
import com.c3ssoftware.process.Process;
import com.c3ssoftware.util.CustomOutputStream;

public class BullyAlgorithm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame f; // Main frame
	private JTextArea ta; // Text area
	private JScrollPane sbrText; // Scroll pane for text area
	private JButton btnQuit; // Quit Program

	BullyAlgorithm() {

		// Create Frame
		f = new JFrame("Swing Demo");
		f.getContentPane().setLayout(new FlowLayout());

		// Create Scrolling Text Area in Swing
		ta = new JTextArea("", 20, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sbrText.setAutoscrolls(false);
		ta.setAutoscrolls(false);
//		DefaultCaret caret = (DefaultCaret) ta.getCaret();
//		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
//		sbrText.getViewport().setViewPosition(new Point(0,0));
		// Redirect the output stream to the TextArea
		PrintStream printStream = new PrintStream(new CustomOutputStream(ta));
		System.setOut(printStream);
		System.setErr(printStream);
		// Create Quit Button
		btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	public void launchFrame() { // Create Layout
		// Add text area and button to frame
		f.getContentPane().add(sbrText);
		f.getContentPane().add(btnQuit);

		// Close when the close button is clicked
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display Frame
		f.pack(); // Adjusts frame to size of components
		f.setVisible(true);
	}

	public static void main(String args[]) {
		new BullyAlgorithm().launchFrame();
		Process process = new Process();
		process.run();

	}
}
