package com.itez.vghtc.gtrainman;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
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
public class SettingForm extends VerticalLayout {
	protected RadioButtonGroup<java.lang.String> rbgParams;
	protected Panel btnSubmit;
	protected TextArea txtJson;
	protected HorizontalLayout layoutUpload;

	public SettingForm() {
		Design.read(this);
	}
}
