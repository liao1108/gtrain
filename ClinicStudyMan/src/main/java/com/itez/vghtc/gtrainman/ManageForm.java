package com.itez.vghtc.gtrainman;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ManageForm extends VerticalLayout {
	protected TabSheet tabSheet;
	protected VerticalLayout tabCases;
	protected RadioButtonGroup<java.lang.String> rbgStage;
	protected ComboBox<java.lang.String> combCmd;
	protected Button btnRun;
	protected HorizontalSplitPanel splitPane;
	protected VerticalLayout layoutListCase;
	protected VerticalLayout layoutListFile;

	public ManageForm() {
		Design.read(this);
	}
}
