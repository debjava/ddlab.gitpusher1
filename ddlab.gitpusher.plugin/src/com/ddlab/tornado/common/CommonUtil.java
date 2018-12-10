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

  public static void setProposalDecorator(Control control, String message) {
    ControlDecoration decorator = getDecorator(control, message);
    decorator.setImage(ImageUtil.PROPOSAL_IMAGE);
    decorator.show();
  }

  public static void setInfoDecorator(Control control, String message) {
    ControlDecoration decorator = getDecorator(control, message);
    decorator.setImage(ImageUtil.INFO_IMAGE);
    decorator.show();
  }

  public static void setRequiredDecorator(Control control, String message) {
    ControlDecoration decorator = getDecorator(control, message);
//    decorator.setImage(ImageUtil.ERROR_IMAGE);
    decorator.setImage(ImageUtil.PROPOSAL_IMAGE);
    decorator.show();
  }

  public static ControlDecoration getDecorator(Control control, String message) {
    ControlDecoration decorator = new ControlDecoration(control, SWT.TOP);
    decorator.setDescriptionText(message);
    return decorator;
  }

  public static void setLayoutData(Control control) {
    GridData gData = new GridData();
    gData.heightHint = 20;
    gData.grabExcessHorizontalSpace = true;
    gData.horizontalAlignment = GridData.FILL;
    control.setLayoutData(gData);
  }

  public static void setRightSideControlDecorator(Control control, String message) {
    ControlDecoration decorator = new ControlDecoration(control, SWT.CENTER | SWT.RIGHT);
    decorator.setDescriptionText(message);
    decorator.setImage(ImageUtil.INFO_IMAGE);
    decorator.setMarginWidth(2);
    decorator.show();
  }
}
