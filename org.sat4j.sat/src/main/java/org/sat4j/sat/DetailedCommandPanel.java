/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004, 2012 Artois University and CNRS
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
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
 * Contributors:
 *   CRIL - initial API and implementation
 *******************************************************************************/
package org.sat4j.sat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sat4j.core.ASolverFactory;
import org.sat4j.minisat.core.ICDCL;
import org.sat4j.minisat.core.ICDCLLogger;
import org.sat4j.minisat.core.IPhaseSelectionStrategy;
import org.sat4j.minisat.core.RestartStrategy;
import org.sat4j.minisat.core.SearchParams;
import org.sat4j.minisat.core.Solver;
import org.sat4j.minisat.orders.RandomWalkDecorator;
import org.sat4j.minisat.orders.VarOrderHeap;
import org.sat4j.minisat.restarts.LubyRestarts;
import org.sat4j.minisat.restarts.NoRestarts;
import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.core.IPBCDCLSolver;
import org.sat4j.pb.reader.PBInstanceReader;
import org.sat4j.reader.InstanceReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.ISolverService;
import org.sat4j.specs.Lbool;
import org.sat4j.specs.SearchListener;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ConflictDepthTracing;
import org.sat4j.tools.ConflictLevelTracing;
import org.sat4j.tools.DecisionTracing;
import org.sat4j.tools.HeuristicsTracing;
import org.sat4j.tools.LearnedClausesSizeTracing;
import org.sat4j.tools.LearnedTracing;
import org.sat4j.tools.MultiTracing;


/**
 * 
 * This panel contains buttons that control restart and clean on solver.
 * It also displays history of commands.
 * 
 * @author sroussel
 *
 */
public class DetailedCommandPanel extends JPanel implements ICDCLLogger,SearchListener{


	private static final long serialVersionUID = 1L;

	private static final EmptyBorder border5 = new EmptyBorder(5,5,5,5);

	private String ramdisk;

	private RemoteControlStrategy telecomStrategy;
	private RandomWalkDecorator randomWalk;
	private ICDCL solver;
	private Reader reader;
	private IProblem problem;

	private boolean useCustomizedSolver;

	private Thread solveurThread;

	private StringWriter stringWriter;

	private JPanel instancePanel;
	private final static String INSTANCE_PANEL = "Instance";
	private JLabel instanceLabel;
	private final static String INSTANCE  = "Path to instance: ";
	private JTextField instancePathField;
	private String instancePath;
	private JButton browseButton;
	private final static String BROWSE = "Browse";


	private final static String MINISAT_PREFIX = "minisat";
	private final static String PB_PREFIX = "pb";
	private JPanel choixSolverPanel;
	private final static String CHOIX_SOLVER_PANEL = "Solver";
	private JLabel choixSolver;
	private final static String CHOIX_SOLVER  = "Choose solver: ";
	private String selectedSolver;
	private JComboBox listeSolvers;

	private JCheckBox useCustomizedSolverCB;
	private final static String USE_CUSTOMIZED_SOLVER = "Use customized solver";

	private JButton startStopButton;
	private static final String START = "Start";
	private static final String STOP = "Stop";


	private final static String RESTART_PANEL = "Restart strategy";	
	private final static String RESTART = "Restart";

	private JPanel restartPanel;

	private JPanel restartPropertiesPanel;
	private JPanel restartButtonPanel;

	private JLabel chooseRestartStrategyLabel;
	private final static String CHOOSE_RESTART_STRATEGY = "Choose restart strategy: ";

	private final static String NO_PARAMETER_FOR_THIS_STRATEGY = "No paramaters for this strategy";
	private JLabel noParameterLabel;

	private JComboBox listeRestarts;
	private String currentRestart;
	//	private final static String RESTART_NO_STRATEGY = "No strategy";
	private final static String RESTART_DEFAULT = "NoRestarts";
	private final static String RESTART_STRATEGY_CLASS = "org.sat4j.minisat.core.RestartStrategy";
	private final static String RESTART_PATH="org.sat4j.minisat.restarts";


	//	private JPanel lubyPanel;
	private JLabel factorLabel;
	private final static String FACTOR = "Factor: ";
	private JTextField factorField;


	private JButton restartButton;

	private JPanel rwPanel;

	private JLabel probaRWLabel;
	private JTextField probaRWField;

	private JButton applyRWButton;

	private final static String RW_PANEL = "Random Walk";
	private final static String RW_LABEL = "Probabilty : ";
	private final static String RW_APPLY = "Apply";


	private JPanel cleanPanel;
	private final static String CLEAN_PANEL = "Learned Constraint Deletion Strategy";

	private JSlider cleanSlider;


	private JButton cleanButton;
	private final static String CLEAN = "Clean now";
	private final static String MANUAL_CLEAN = "Manual clean: ";
	private JLabel manualCleanLabel;

	private final JLabel deleteClauseLabel = new JLabel(DELETE_CLAUSES);
	private final static String DELETE_CLAUSES = "Automated clean: ";

	private Hashtable<Integer, JLabel> cleanValuesTable;
	private final JLabel clean5000Label = new JLabel(CLEAN_5000);
	private final JLabel clean10000Label = new JLabel(CLEAN_10000);
	private final JLabel clean20000Label = new JLabel(CLEAN_20000);
	private final JLabel clean50000Label = new JLabel(CLEAN_50000);
	private final JLabel clean100000Label = new JLabel(CLEAN_100000);
	private final JLabel clean500000Label = new JLabel(CLEAN_500000);
	private final static int[] cleanValues ={5000,10000,20000,50000,100000,500000};
	private final static int CLEAN_MIN = 0;
	private final static int CLEAN_MAX = 5;
	private final static int CLEAN_INIT = 1;
	private final static int CLEAN_SPACE = 1;

	private final static String CLEAN_5000 = "5000";
	private final static String CLEAN_10000 = "10000";
	private final static String CLEAN_20000 = "20000";
	private final static String CLEAN_50000 = "50000";
	private final static String CLEAN_100000 = "100000";
	private final static String CLEAN_500000 = "500000";

	private JCheckBox cleanUseOriginalStrategyCB;
	private final static String USE_ORIGINAL_STRATEGY = "Use solver's original deletion strategy";



	private JPanel phasePanel;
	private final static String PHASE_PANEL = "Phase Strategy";

	private String currentPhaseSelectionStrategy;

	private JComboBox phaseList;
	private JLabel phaseListLabel;
	private final static String PHASE_STRATEGY = "Choose phase strategy :";

	private JButton phaseApplyButton;
	private final static String PHASE_APPLY = "Apply";

	private final static String PHASE_STRATEGY_CLASS = "org.sat4j.minisat.core.IPhaseSelectionStrategy";
	private final static String PHASE_PATH_SAT="org.sat4j.minisat.orders";


	private JPanel simplifierPanel;
	private final static String SIMPLIFIER_PANEL = "Simplification strategy";
	private final static String SIMPLIFICATION_APPLY = "Apply";
	private final static String SIMPLIFICATION_NO = "No reason simplification";
	private final static String SIMPLIFICATION_SIMPLE = "Simple reason simplification";
	private final static String SIMPLIFICATION_EXPENSIVE = "Expensive reason simplification";
	private final static String NO_SIMPLIFICATION = "NO_SIMPLIFICATION";
	private final static String SIMPLE_SIMPLIFICATION = "SIMPLE_SIMPLIFICATION";
	private final static String EXPENSIVE_SIMPLIFICATION = "EXPENSIVE_SIMPLIFICATION";
	private JButton simplificationApplyButton;
	private ButtonGroup simplificationGroup;
	private JRadioButton simplificationNoRadio;
	private JRadioButton simplificationSimpleRadio;
	private JRadioButton simplificationExpensiveRadio;



	private JTextArea console;
	private JScrollPane scrollPane;

	private boolean isPlotActivated;

	private Process gnuplotProcess;


	public DetailedCommandPanel(String filename){
		this(filename,"");
	}

	public DetailedCommandPanel(String filename, String ramdisk){
		this(filename,ramdisk,null);
	}

	public DetailedCommandPanel(String filename, String ramdisk, ICDCL solver){
		super();

		this.telecomStrategy = new RemoteControlStrategy(this);
		this.instancePath=filename;
		this.ramdisk = ramdisk;
		this.solver=solver;

		this.isPlotActivated=false;

		this.useCustomizedSolver=(this.solver!=null);

		if(solver!=null){
			if(solver.getOrder() instanceof RandomWalkDecorator){
				randomWalk = (RandomWalkDecorator)solver.getOrder();
			}
			else{
				randomWalk = new RandomWalkDecorator((VarOrderHeap)((Solver)solver).getOrder(), 0);
			}
		}


		this.setPreferredSize(new Dimension(700,1300));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


		createInstancePanel();
		createChoixSolverPanel();
		createRestartPanel();
		createRWPanel();
		createCleanPanel();
		createPhasePanel();
		createSimplifierPanel();

		console = new JTextArea();

		scrollPane = new JScrollPane(console);

		//scrollPane.setMinimumSize(new Dimension(100,100));
		scrollPane.setPreferredSize(new Dimension(400,200));
		scrollPane.getVerticalScrollBar().setValue(
				scrollPane.getVerticalScrollBar().getMaximum());
		//	scrollPane.setAutoscrolls(true);

		initFactorParam();

		restartPropertiesPanel.setPreferredSize(new Dimension(100,50));


		this.add(instancePanel);
		this.add(choixSolverPanel);
		this.add(restartPanel);
		this.add(rwPanel);
		this.add(cleanPanel);
		this.add(phasePanel);
		this.add(simplifierPanel);
		this.add(scrollPane);

		setRestartPanelEnabled(false);
		setRWPanelEnabled(false);
		setCleanPanelEnabled(false);
		setPhasePanelEnabled(false);
		setSimplifierPanelEnabled(false);
	}



	public void createInstancePanel(){
		instancePanel = new JPanel();

		instancePanel.setName(INSTANCE_PANEL);
		instancePanel.setBorder(new CompoundBorder(new TitledBorder(null, instancePanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		instancePanel.setLayout(new BorderLayout(0,0));

		instanceLabel = new JLabel(INSTANCE);
		instancePathField = new JTextField(20);
		instancePathField.setText(instancePath);

		instanceLabel.setLabelFor(instancePathField);

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.add(instanceLabel);
		tmpPanel1.add(instancePathField);

		browseButton = new JButton(BROWSE);

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileChooser();
			}
		});

		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(browseButton);

		instancePanel.add(tmpPanel1,BorderLayout.CENTER);
		instancePanel.add(tmpPanel2,BorderLayout.SOUTH);


	}

	public void createChoixSolverPanel(){
		choixSolverPanel = new JPanel();

		choixSolverPanel.setName(CHOIX_SOLVER_PANEL);
		choixSolverPanel.setBorder(new CompoundBorder(new TitledBorder(null, choixSolverPanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		choixSolverPanel.setLayout(new BorderLayout());

		choixSolver = new JLabel(CHOIX_SOLVER);
		updateListOfSolvers();


		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.add(choixSolver);
		tmpPanel1.add(listeSolvers);

		startStopButton = new JButton(START);

		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(startStopButton.getText().equals(START)){
					launchSolver();
					setInstancePanelEnabled(false);
					setRestartPanelEnabled(true);
					setRWPanelEnabled(true);
					setCleanPanelEnabled(true);
					setCleanPanelOriginalStrategyEnabled(true);
					setPhasePanelEnabled(true);
					setChoixSolverPanelEnabled(false);
					setSimplifierPanelEnabled(true);
					startStopButton.setText(STOP);
					getThis().paintAll(getThis().getGraphics());
				}
				else {

					//assert solveurThread!=null;
					((ISolver)problem).expireTimeout();
					log("Asked the solver to stop");
					setInstancePanelEnabled(true);
					setChoixSolverPanelEnabled(true);
					setRestartPanelEnabled(false);
					setRWPanelEnabled(false);
					setCleanPanelEnabled(false);
					setPhasePanelEnabled(false);
					setSimplifierPanelEnabled(false);
					startStopButton.setText(START);
					getThis().paintAll(getThis().getGraphics());
				}
			}
		});

		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(startStopButton);


		useCustomizedSolverCB = new JCheckBox(USE_CUSTOMIZED_SOLVER);
		JPanel tmpPanel3 = new JPanel();
		tmpPanel3.add(useCustomizedSolverCB);

		useCustomizedSolverCB.setSelected(useCustomizedSolver);

		useCustomizedSolverCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useCustomizedSolver=!useCustomizedSolver;
				useCustomizedSolverCB.setEnabled(useCustomizedSolver);
				listeSolvers.setEnabled(!useCustomizedSolver);
				choixSolverPanel.repaint();
			}
		});

		setChoixSolverPanelEnabled(useCustomizedSolver);

		choixSolverPanel.add(tmpPanel3,BorderLayout.NORTH);
		choixSolverPanel.add(tmpPanel1,BorderLayout.CENTER);
		choixSolverPanel.add(tmpPanel2,BorderLayout.SOUTH);

	}


	public void createRestartPanel(){
		restartPanel = new JPanel();

		restartPanel.setName(RESTART_PANEL);
		restartPanel.setBorder(new CompoundBorder(new TitledBorder(null, restartPanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		restartPanel.setLayout(new BorderLayout());

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.setLayout(new FlowLayout());

		chooseRestartStrategyLabel = new JLabel(CHOOSE_RESTART_STRATEGY);

		listeRestarts = new JComboBox(getListOfRestartStrategies().toArray());	
		currentRestart = telecomStrategy.getRestartStrategy().getClass().getSimpleName();
		listeRestarts.setSelectedItem(RESTART_DEFAULT);

		listeRestarts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyRestartParamPanel();
			}
		});

		tmpPanel1.add(chooseRestartStrategyLabel);
		tmpPanel1.add(listeRestarts);

		noParameterLabel = new JLabel(NO_PARAMETER_FOR_THIS_STRATEGY);

		Font newLabelFont=new Font(noParameterLabel.getFont().getName(),Font.ITALIC,noParameterLabel.getFont().getSize());

		noParameterLabel.setFont(newLabelFont);

		restartPropertiesPanel = new JPanel();
		restartPropertiesPanel.add(noParameterLabel);


		restartButton = new JButton(RESTART);

		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnRestart();
			}
		});

		restartButtonPanel = new JPanel();
		restartButtonPanel.add(restartButton);

		restartPanel.add(tmpPanel1,BorderLayout.NORTH);
		restartPanel.add(restartPropertiesPanel,BorderLayout.CENTER);
		restartPanel.add(restartButtonPanel,BorderLayout.SOUTH);

	}

	public void createRWPanel(){
		rwPanel = new JPanel();

		rwPanel.setName(RW_PANEL);
		rwPanel.setBorder(new CompoundBorder(new TitledBorder(null, rwPanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		rwPanel.setLayout(new BorderLayout());

		probaRWLabel = new JLabel(RW_LABEL);
		probaRWField = new JTextField("0",10);

		probaRWLabel.setLabelFor(probaRWField);

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.setLayout(new FlowLayout());

		tmpPanel1.add(probaRWLabel);
		tmpPanel1.add(probaRWField);

		JPanel tmpPanel2 = new JPanel();
		applyRWButton = new JButton(RW_APPLY);

		applyRWButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnApplyRW();
			}
		});

		tmpPanel2.add(applyRWButton);

		rwPanel.add(tmpPanel1, BorderLayout.CENTER);
		rwPanel.add(tmpPanel2, BorderLayout.SOUTH);


	}

	public void createCleanPanel(){
		cleanPanel = new JPanel();

		cleanPanel.setName(CLEAN_PANEL);
		cleanPanel.setBorder(new CompoundBorder(new TitledBorder(null, cleanPanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		cleanPanel.setLayout(new BorderLayout());


		cleanSlider = new JSlider(JSlider.HORIZONTAL,CLEAN_MIN,CLEAN_MAX,CLEAN_INIT);

		cleanSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!cleanSlider.getValueIsAdjusting()) {
					hasChangedCleaningValue();
				}
			}
		});
		cleanSlider.setMajorTickSpacing(CLEAN_SPACE);
		cleanSlider.setPaintTicks(true);

		//Create the label table
		cleanValuesTable = new Hashtable<Integer, JLabel>();
		cleanValuesTable.put(new Integer(0),clean5000Label);
		cleanValuesTable.put(new Integer(1),clean10000Label);
		cleanValuesTable.put(new Integer(2),clean20000Label);
		cleanValuesTable.put(new Integer(3),clean50000Label);
		cleanValuesTable.put(new Integer(4),clean100000Label);
		cleanValuesTable.put(new Integer(5),clean500000Label);
		cleanSlider.setLabelTable(cleanValuesTable);

		cleanSlider.setPaintLabels(true);
		cleanSlider.setSnapToTicks(true);

		cleanSlider.setPreferredSize(new Dimension(400,50));

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.add(deleteClauseLabel);
		tmpPanel1.add(cleanSlider);


		JPanel tmpPanel2 = new JPanel();

		manualCleanLabel = new JLabel(MANUAL_CLEAN);

		cleanButton = new JButton(CLEAN);

		cleanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnClean();
			}
		});

		tmpPanel2.add(manualCleanLabel);
		tmpPanel2.add(cleanButton);

		JPanel tmpPanel3 = new JPanel();
		cleanUseOriginalStrategyCB = new JCheckBox(USE_ORIGINAL_STRATEGY);
		cleanUseOriginalStrategyCB.setSelected(true);

		cleanUseOriginalStrategyCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnUseOriginalStrategy();
			}
		});

		tmpPanel3.add(cleanUseOriginalStrategyCB);

		cleanPanel.add(tmpPanel3,BorderLayout.NORTH);
		cleanPanel.add(tmpPanel2,BorderLayout.CENTER);
		cleanPanel.add(tmpPanel1,BorderLayout.SOUTH);

	}

	public void createPhasePanel(){
		phasePanel = new JPanel();

		phasePanel.setName(PHASE_PANEL);
		phasePanel.setBorder(new CompoundBorder(new TitledBorder(null, phasePanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		phasePanel.setLayout(new BorderLayout());

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.setLayout(new FlowLayout());

		phaseListLabel = new JLabel(PHASE_STRATEGY);

		phaseList = new JComboBox(getListOfPhaseStrategies().toArray());	
		currentPhaseSelectionStrategy = telecomStrategy.getPhaseSelectionStrategy().getClass().getSimpleName();
		phaseList.setSelectedItem(currentPhaseSelectionStrategy);

		//		phaseList.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				modifyRestartParamPanel();
		//			}
		//		});

		tmpPanel1.add(phaseListLabel);
		tmpPanel1.add(phaseList);




		phaseApplyButton = new JButton(PHASE_APPLY);

		phaseApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnApplyPhase();
			}
		});

		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(phaseApplyButton);

		phasePanel.add(tmpPanel1,BorderLayout.CENTER);
		phasePanel.add(tmpPanel2,BorderLayout.SOUTH);
	}

	public void createSimplifierPanel(){
		simplifierPanel = new JPanel();

		simplifierPanel.setName(SIMPLIFIER_PANEL);
		simplifierPanel.setBorder(new CompoundBorder(new TitledBorder(null, simplifierPanel.getName(), 
				TitledBorder.LEFT, TitledBorder.TOP), border5));

		simplifierPanel.setLayout(new BorderLayout());

		//		simplificationRadio = new Radio
		simplificationGroup = new ButtonGroup();
		simplificationExpensiveRadio = new JRadioButton(SIMPLIFICATION_EXPENSIVE);
		simplificationNoRadio = new JRadioButton(SIMPLIFICATION_NO);
		simplificationSimpleRadio = new JRadioButton(SIMPLIFICATION_SIMPLE);

		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.setLayout(new BoxLayout(tmpPanel1, BoxLayout.Y_AXIS));

		simplificationGroup.add(simplificationNoRadio);
		simplificationGroup.add(simplificationSimpleRadio);
		simplificationGroup.add(simplificationExpensiveRadio);


		tmpPanel1.add(simplificationNoRadio);
		tmpPanel1.add(simplificationSimpleRadio);
		tmpPanel1.add(simplificationExpensiveRadio);

		simplificationApplyButton = new JButton(SIMPLIFICATION_APPLY);

		simplificationApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasClickedOnApplySimplification();
			}
		});

		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(simplificationApplyButton);

		simplifierPanel.add(tmpPanel1,BorderLayout.NORTH);
		simplifierPanel.add(tmpPanel2,BorderLayout.SOUTH);

	}

	public void initFactorParam(){
		//		lubyPanel = new JPanel();
		//		//		lubyPanel.setLayout(new FlowLayout());

		factorLabel = new JLabel(FACTOR);
		factorField = new JTextField(LubyRestarts.DEFAULT_LUBY_FACTOR+"",5);
		//factorField.setMargin(new Insets(0, 0, 0, 0));
		//factorLabel.setLabelFor(factorField);

		//		lubyPanel.add(factorLabel);
		//		lubyPanel.add(factorField);

	}

	public void launchSolver(){

		if(!useCustomizedSolver)
		{
			selectedSolver = (String)listeSolvers.getSelectedItem();
			String[] partsSelectedSolver = selectedSolver.split("\\.");

			assert partsSelectedSolver.length==2;
			assert (partsSelectedSolver[0].equals(MINISAT_PREFIX) || partsSelectedSolver[0].equals(PB_PREFIX)) ;

			ASolverFactory factory;

			if(partsSelectedSolver[0].equals(MINISAT_PREFIX)){
				factory = org.sat4j.minisat.SolverFactory.instance();
			}
			else{
				factory = org.sat4j.pb.SolverFactory.instance();
			}
			solver = (ICDCL)factory.createSolverByName(partsSelectedSolver[1]);
			//log(solver.toString());



			telecomStrategy.setSolver(solver);
			telecomStrategy.setRestartStrategy(solver.getRestartStrategy());
			currentRestart = telecomStrategy.getRestartStrategy().getClass().getSimpleName();

			solver.setRestartStrategy(telecomStrategy);


			telecomStrategy.setPhaseSelectionStrategy(solver.getOrder().getPhaseSelectionStrategy());
			currentPhaseSelectionStrategy = telecomStrategy.getPhaseSelectionStrategy().getClass().getSimpleName();

			solver.getOrder().setPhaseSelectionStrategy(telecomStrategy);
			
			
			if(solver.getSimplifier().toString().equals(SIMPLIFICATION_EXPENSIVE)){
				simplificationExpensiveRadio.setSelected(true);
			}
			else if(solver.getSimplifier().toString().equals(SIMPLIFICATION_SIMPLE)){
				simplificationSimpleRadio.setSelected(true);
			}
			else{
				simplificationNoRadio.setSelected(true);
			}

			phaseList.setSelectedItem(currentPhaseSelectionStrategy);
			phasePanel.repaint();

			updateRestartStrategyPanel();

			//pbSolver.setNeedToReduceDB(true);

			double proba=0;
			if(probaRWField.getText()!=null){
				proba = Double.parseDouble(probaRWField.getText());
			}
			randomWalk = new RandomWalkDecorator((VarOrderHeap)((Solver)solver).getOrder(), proba);

			solver.setOrder(randomWalk);
		}


		String whereToWriteFiles = instancePath;

		if(ramdisk.length()>0){
			String[] instancePathSplit= instancePath.split("/");
			whereToWriteFiles = ramdisk+"/"+ instancePathSplit[instancePathSplit.length-1];

		}

		solver.setVerbose(true);

		solver.setSearchListener(new MultiTracing(this,
				new ConflictLevelTracing(whereToWriteFiles
						+ "-conflict-level"), new DecisionTracing(
								whereToWriteFiles + "-decision-indexes"),
								new LearnedClausesSizeTracing(whereToWriteFiles
										+ "-learned-clauses-size"),
										new ConflictDepthTracing(whereToWriteFiles
												+ "-conflict-depth"), 
												new HeuristicsTracing(whereToWriteFiles + "-heuristics"),
												new LearnedTracing(whereToWriteFiles + "-learned")));

		solver.setLogger(this);

		reader = createReader(solver, instancePath);


		try{
			problem = reader.parseInstance(instancePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ContradictionException e) {
			log("Unsatisfiable (trivial)!");
		}

		boolean optimisation=false;
		if(reader instanceof PBInstanceReader){
			optimisation = ((PBInstanceReader)reader).hasObjectiveFunction();
			if(optimisation){
				problem = new OptToPBSATAdapter(new PseudoOptDecorator((IPBCDCLSolver)solver));
			}
		}


		log("# Started solver " + solver.getClass().getSimpleName());
		log("# on instance " + instancePath);
		log("# Optimisation = " + optimisation);
		log("# Restart strategy = " + solver.getRestartStrategy().getClass().getSimpleName());
		log("# Random walk probability = " +randomWalk.getProbability());
		//log("# Number of conflicts before cleaning = " + nbConflicts);


		solveurThread = new Thread() {
			public void run() {
				//Thread thisThread = Thread.currentThread();
				//				if(shouldStop){
				//					System.out.println("coucou");
				//				}
				//				while(!shouldStop){
				try{
					stringWriter = new StringWriter();
					if(problem.isSatisfiable()){
						log("Satisfiable !");
						log(((OptToPBSATAdapter)problem).getCurrentObjectiveValue()+"");
						reader.decode(((OptToPBSATAdapter)problem).model(new PrintWriter(stringWriter)), new PrintWriter(stringWriter));
						log(stringWriter.toString());
					}
					else{
						log("Unsatisfiable !");
					}
				} catch (TimeoutException e) {
					log("Timeout, sorry!");      
				}
				//log("Solver has stopped");
				//				}
			}
		};
		solveurThread.start();


		if(isPlotActivated){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			traceGnuplot();
		}

	}

	public void hasClickedOnRestart(){
		telecomStrategy.setHasClickedOnRestart(true);
		String choix = (String)listeRestarts.getSelectedItem();

		boolean isNotSameRestart = !choix.equals(currentRestart);
		boolean shouldInit = isNotSameRestart;

		RestartStrategy restart = new NoRestarts();
		SearchParams params = telecomStrategy.getSearchParams();

		if(choix.equals("LubyRestarts")){
			boolean factorChanged = false;
			int factor = LubyRestarts.DEFAULT_LUBY_FACTOR;
			if(factorField.getText()!=null){
				factor = Integer.parseInt(factorField.getText());
			}
			// if the current restart is a LubyRestart
			if(isNotSameRestart){
				restart = new LubyRestarts(factor);
				telecomStrategy.setRestartStrategy(restart);
			}
			else{
				factorChanged = !(factor==((LubyRestarts)telecomStrategy.getRestartStrategy()).getFactor());
			}
			// if the factor has changed
			if(factorChanged){
				restart = telecomStrategy.getRestartStrategy();
				((LubyRestarts)restart).setFactor(factor);
			}
			shouldInit = isNotSameRestart || factorChanged;

			if(shouldInit){
				restart.init(params);
				log("Init restart");
			}

		}

		else try{
			restart = (RestartStrategy)Class.forName(RESTART_PATH+"."+choix).newInstance();
			assert restart!=null;
			telecomStrategy.setRestartStrategy(restart);
			telecomStrategy.init(params);

		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
		}
		catch(InstantiationException e){
			e.printStackTrace();
		}

		currentRestart = choix;


		//		if(shouldInit)
		//			telecomStrategy.setRestartStrategy(restart,params);

		//log("Has clicked on " + RESTART + " with "+ choix);
	}

	public void hasClickedOnApplyRW(){
		double proba=0;
		if(probaRWField!=null)
			proba = Double.parseDouble(probaRWField.getText());

		randomWalk.setProbability(proba);
		log("Set probability to " + proba);
	}

	public void hasClickedOnApplyPhase(){
		String phaseName = (String)phaseList.getSelectedItem();
		currentPhaseSelectionStrategy = phaseName;
		IPhaseSelectionStrategy phase = null;
		try{
			phase= (IPhaseSelectionStrategy)Class.forName(PHASE_PATH_SAT+"."+phaseName).newInstance();
			phase.init(solver.nVars()+1);
			telecomStrategy.setPhaseSelectionStrategy(phase);
			log("Told the solver to apply a new phase strategy :" + currentPhaseSelectionStrategy);
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
		}
		catch(InstantiationException e){
			e.printStackTrace();
		}

	}

	public void hasChangedCleaningValue(){
		int nbConflicts = cleanValues[cleanSlider.getValue()];
		telecomStrategy.setNbClausesAtWhichWeShouldClean(nbConflicts);
		log("Changed number of conflicts before cleaning to " + nbConflicts);
	}

	public void hasClickedOnClean(){
		log("Told the solver to clean");
		telecomStrategy.setHasClickedOnClean(true);
		//log("Has clicked on " + CLEAN);
	}

	public void hasClickedOnUseOriginalStrategy(){
		int nbConflicts = cleanValues[cleanSlider.getValue()];
		telecomStrategy.setNbClausesAtWhichWeShouldClean(nbConflicts);

		solver.setLearnedConstraintsDeletionStrategy(telecomStrategy);

		setCleanPanelOriginalStrategyEnabled(false);
	}


	public void hasClickedOnApplySimplification(){
		if(simplificationSimpleRadio.isSelected()){
			solver.setSimplifier(SIMPLE_SIMPLIFICATION);
			log("Told the solver to use " + SIMPLIFICATION_SIMPLE);
		}
		else if(simplificationExpensiveRadio.isSelected()){
			solver.setSimplifier(EXPENSIVE_SIMPLIFICATION);
			log("Told the solver to use " + SIMPLIFICATION_EXPENSIVE);
		}
		else{
			solver.setSimplifier(NO_SIMPLIFICATION);
			log("Told the solver to use " + SIMPLIFICATION_NO);
		}

	}


	public List<String> getListOfRestartStrategies(){
		List<String> resultRTSI = RTSI.find(RESTART_STRATEGY_CLASS);
		List<String> finalResult = new ArrayList<String>();

		//		finalResult.add(RESTART_NO_STRATEGY);

		for(String s:resultRTSI){
			if(!s.contains("Remote")){
				finalResult.add(s);
			}
		}

		return finalResult;
	}

	public List<String> getListOfPhaseStrategies(){
		List<String> resultRTSI = RTSI.find(PHASE_STRATEGY_CLASS);
		List<String> finalResult = new ArrayList<String>();

		//		finalResult.add(RESTART_NO_STRATEGY);

		for(String s:resultRTSI){
			if(!s.contains("Remote")){
				finalResult.add(s);
			}
		}

		return finalResult;
	}


	public List<String> getListOfSolvers(){
		ASolverFactory factory;

		List<String> result = new ArrayList<String>();

		factory = org.sat4j.minisat.SolverFactory.instance();
		for(String s:factory.solverNames()){
			result.add(MINISAT_PREFIX+"."+s);
		}

		factory = org.sat4j.pb.SolverFactory.instance();
		for(String s:factory.solverNames()){
			result.add(PB_PREFIX+"."+s);
		}

		Collections.sort(result);

		return result;
	}

	public List<String> getListOfPBSolvers(){
		ASolverFactory factory;

		List<String> result = new ArrayList<String>();

		factory = org.sat4j.pb.SolverFactory.instance();
		for(String s:factory.solverNames()){
			result.add(PB_PREFIX+"."+s);
		}

		Collections.sort(result);

		return result;
	}

	public void modifyRestartParamPanel(){
		restartPropertiesPanel.removeAll();
		if(listeRestarts.getSelectedItem().equals("LubyRestarts")){
			restartPropertiesPanel.add(factorLabel);
			restartPropertiesPanel.add(factorField);
		}
		else{
			restartPropertiesPanel.add(noParameterLabel);
		}
		setRestartPropertiesPanelEnabled(true);
		restartPropertiesPanel.repaint();
		restartPanel.repaint();
		restartPanel.paintAll(restartPanel.getGraphics());
		this.repaint();
	}

	public void log(String message){
		console.append(message +"\n");
		//		scrollPane.getVerticalScrollBar().setValue(
		//	               scrollPane.getVerticalScrollBar().getMaximum()+1);
		console.setCaretPosition(console.getDocument().getLength() );
		console.repaint();
		this.repaint();
	}


	public void openFileChooser(){
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(this, "Choose instance");
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			instancePath = file.getAbsolutePath();
			instancePathField.setText(instancePath);
			updateListOfSolvers();
		}
	}

	protected Reader createReader(ICDCL theSolver, String problemname) {
		if (theSolver instanceof IPBSolver) {
			return new PBInstanceReader((IPBSolver) theSolver);
		}
		return new InstanceReader(theSolver);
	}

	public void updateListOfSolvers(){

		if(instancePath.endsWith(".opb")){
			listeSolvers = new JComboBox(getListOfPBSolvers().toArray());
			listeSolvers.setSelectedItem("pb.Default");
			selectedSolver = "pb.Default";
		}
		else{
			listeSolvers = new JComboBox(getListOfSolvers().toArray());
			listeSolvers.setSelectedItem("minisat.Default");
			selectedSolver = "minisat.Default";
		}

	}

	public void updateRestartStrategyPanel(){
		listeRestarts.setSelectedItem(currentRestart);

	}

	public void setInstancePanelEnabled(boolean enabled){
		instanceLabel.setEnabled(enabled);
		instancePathField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
		instancePanel.repaint();
	}

	public void setChoixSolverPanelEnabled(boolean enabled){
		listeSolvers.setEnabled(enabled && !useCustomizedSolver);
		choixSolver.setEnabled(enabled && !useCustomizedSolver);
		useCustomizedSolverCB.setEnabled(enabled && useCustomizedSolver);
		choixSolverPanel.repaint();
	}

	public void setRestartPanelEnabled(boolean enabled){
		listeRestarts.setEnabled(enabled);
		restartButton.setEnabled(enabled);
		chooseRestartStrategyLabel.setEnabled(enabled);
		setRestartPropertiesPanelEnabled(enabled);
		restartPanel.repaint();
	}

	public void setRestartPropertiesPanelEnabled(boolean enabled){
		for(Component c:restartPropertiesPanel.getComponents()){
			c.setEnabled(enabled);
		}
		restartPropertiesPanel.repaint();
	}

	public void setRWPanelEnabled(boolean enabled){
		probaRWLabel.setEnabled(enabled);
		probaRWField.setEnabled(enabled);
		applyRWButton.setEnabled(enabled);
		rwPanel.repaint();
	}

	public void setCleanPanelEnabled(boolean enabled){
		manualCleanLabel.setEnabled(enabled);
		deleteClauseLabel.setEnabled(enabled);
		cleanSlider.setEnabled(enabled);
		cleanButton.setEnabled(enabled);
		cleanUseOriginalStrategyCB.setEnabled(enabled);
		cleanPanel.repaint();
	}

	public void setCleanPanelOriginalStrategyEnabled(boolean enabled){
		cleanUseOriginalStrategyCB.setEnabled(enabled);
		manualCleanLabel.setEnabled(!enabled);
		deleteClauseLabel.setEnabled(!enabled);
		cleanSlider.setEnabled(!enabled);
		cleanButton.setEnabled(!enabled);
		cleanPanel.repaint();
	}

	public void setPhasePanelEnabled(boolean enabled){
		phaseList.setEnabled(enabled);
		phaseListLabel.setEnabled(enabled);
		phaseApplyButton.setEnabled(enabled);
		restartPanel.repaint();
	}

	public void setSimplifierPanelEnabled(boolean enabled){
		simplificationNoRadio.setEnabled(enabled);
		simplificationExpensiveRadio.setEnabled(enabled);
		simplificationSimpleRadio.setEnabled(enabled);
		simplificationApplyButton.setEnabled(enabled);
		simplifierPanel.repaint();
	}


	public void activateGnuplotTracing(){
		isPlotActivated=true;
	}

	public void traceGnuplot(){

		int nbVariables = solver.nVars();
		int yVar = (nbVariables/1000+1)*1000;

		try {

			PrintStream out = new PrintStream(new FileOutputStream(instancePath+"-gnuplot.gnuplot"));
			out.println("set terminal x11");
			out.println("set multiplot");
			out.println("set autoscale");
			out.println("set ytics auto");
			//top left: Decision Level when conflict
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.0, 0.5");
			out.println("set title \"Decision level at which the conflict occurs\"");
			out.println("plot \"" + instancePath+ "-conflict-level-restart.dat\" with impulses ls 3 title \"Restart\",\""+ instancePath +"-conflict-level.dat\" ls 1 title \"Conflict level\"");
			//top right: size of learned clause
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.66, 0.5");
			out.println("set title \"Size of the clause learned (after minimization if any)\"");
			out.println("plot \"" + instancePath+ "-learned-clauses-size.dat\" title \"Size\"");
			//top middle: clause activity
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.33, 0.5");
			out.println("set title \"Value of clauses activity\"");
			out.println("plot \"" + instancePath+ "-learned.dat\" title \"Activity\"");
			// for bottom graphs, y range should be O-maxVar
			out.println("set yrange [0:"+nbVariables+"]");
			out.println("set ytics add ("+ nbVariables +")");
			//bottom left: index decision variable
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.0, 0.0");
			out.println("set title \"Index of the decision variables\"");
			out.println("plot \"" + instancePath+ "-decision-indexes-restart.dat\" with impulses ls 3 title \"Restart\",\"" 
					+ instancePath+ "-decision-indexes-pos.dat\" lt 2 title \"Positive decision\",\""
					+ instancePath+ "-decision-indexes-neg.dat\" lt 1 title \"Negative Decision\"");
			//bottom right: depth search when conflict
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.66, 0.0");
			out.println("set logscale y");
			out.println("set yrange [1:"+nbVariables+"]");
			out.println("set title \"Decision and trail levels when the conflict occurs\"");
			out.println("plot \"" + instancePath+ "-conflict-depth.dat\" using 1 title \"Decision Level\" lt 1, \"" +
					instancePath+ "-conflict-depth.dat\" using 2 title \"Trail Level\" lt 4,"
					+nbVariables/2+" ls 2 title \"#Var/2\"");
			//bottom middle: variable activity
			out.println("set nologscale y");
			out.println("set logscale x");
			out.println("set xrange [0.5:1.0e+100]");
			out.println("set size 0.33, 0.5");
			out.println("set origin 0.33, 0.0");
			out.println("set title \"Value of variables activity\"");
			out.println("plot \"" + instancePath+ "-heuristics.dat\" with lines title \"Activity\"");
			out.println("set nologscale x");
			out.println("unset multiplot");
			out.println("pause 2");
			out.println("reread");
			out.close();

			String[] cmd = new String[2];
			cmd[0]="/usr/local/bin/gnuplot";
			cmd[1]=instancePath+"-gnuplot.gnuplot";

			gnuplotProcess = Runtime.getRuntime().exec(cmd);



			Thread errorStreamThread = new Thread(){
				public void run(){
					BufferedReader gnuInt = new BufferedReader(new InputStreamReader(gnuplotProcess.getErrorStream()));
					String s;
					try{
						while( (s=gnuInt.readLine())!=null){
							System.out.println(s);
						}
					}
					catch(IOException e){
						e.printStackTrace();
					}
				}
			};
			errorStreamThread.start();


		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

	public void stopGnuplot(){
		if(gnuplotProcess!=null){
			gnuplotProcess.destroy();
		}
	}

	public DetailedCommandPanel getThis(){
		return this;
	}

	public ISolver getSolver(){
		return (ISolver)problem;
	}

	public void init(ISolverService solverService) {
	}

	public void assuming(int p) {
	}

	public void propagating(int p, IConstr reason) {
	}

	public void backtracking(int p) {
	}

	public void adding(int p) {
	}

	public void learn(IConstr c) {
	}

	public void delete(int[] clause) {
	}

	public void conflictFound(IConstr confl, int dlevel, int trailLevel) {
	}

	public void conflictFound(int p) {
	}

	public void solutionFound() {
		log("Found a solution !! ");
	}

	public void beginLoop() {
	}

	public void start() {
	}

	public void end(Lbool result) {
	}

	public void restarting() {
		//log("Restarting");
		//System.out.println("Restarting");
	}

	public void backjump(int backjumpLevel) {
	}

	public void cleaning() {
	}
}
