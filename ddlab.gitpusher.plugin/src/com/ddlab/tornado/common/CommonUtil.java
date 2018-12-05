package com.ddlab.tornado.common;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public class CommonUtil {

	// public static final Font BOLD_FONT =
	// JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

	public static void setDecorator(Control control, String message) {
		ControlDecoration decorator = new ControlDecoration(control, SWT.TOP);
		decorator.setDescriptionText(message);
		decorator.setImage(ImageUtil.PROPOSAL_IMAGE);
		decorator.show();
	}

	public static void setLayoutData(Control control) {
		GridData userNamegData = new GridData();
		userNamegData.heightHint = 20;
		userNamegData.grabExcessHorizontalSpace = true;
		userNamegData.horizontalAlignment = GridData.FILL;
		control.setLayoutData(userNamegData);
	}

}
