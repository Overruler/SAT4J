/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j.sat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import org.sat4j.minisat.core.ICDCL;
import org.sat4j.minisat.orders.RandomWalkDecorator;

/**
 * 
 * JFrame for the remote control.
 * 
 * @author sroussel
 *
 */
public class TelecommandeFrame extends JFrame implements ILog{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String lookAndFeel;


	public static final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	private JMenuBar barreMenu;
	private JMenu menu;
	
	private CommandeDetailleePanel commandePanel;
	private String filename;

	private TelecommandeStrategy telecomStrategy;
	private RandomWalkDecorator randomWalk;
	private ICDCL solver;


	public TelecommandeFrame(String filename){	
		super("T�l�commande");
		
		this.filename=filename;
		initLookAndFeel();

		createAndShowGUI();
	}


	public void reinitialiser(){
	}


	public void initLookAndFeel(){
		JFrame.setDefaultLookAndFeelDecorated(true);
	}

	public void createAndShowGUI(){
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());

		
		createMenuBar();
		
		commandePanel = new CommandeDetailleePanel(filename);
		
		JScrollPane scrollPane = new JScrollPane(commandePanel);
		
		this.add(scrollPane);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	public void createMenuBar(){
		barreMenu = new JMenuBar();
		menu = new JMenu("Menu");
		barreMenu.add(menu);

//		JMenuItem reinitialiserItem = new JMenuItem("R�initialiser");
//		menu.add(reinitialiserItem);
//
//		reinitialiserItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				reinitialiser();
//			}
//		});

		JMenuItem quit = new JMenuItem("Quitter");
		menu.add(quit);

		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(NORMAL);
			}
		});
		this.setJMenuBar(barreMenu);
		
	}
	
	public void log(String message){
		commandePanel.log(message);
	}





}