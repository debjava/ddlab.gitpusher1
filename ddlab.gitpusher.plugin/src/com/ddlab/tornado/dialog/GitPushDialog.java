package com.ddlab.tornado.dialog;

import static com.ddlab.tornado.common.CommonConstants.*;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ddlab.tornado.common.CommonUtil;
import com.ddlab.tornado.common.ImageUtil;

public class GitPushDialog extends TitleAreaDialog {

  private Combo gitActCombo = null;
  private Text userNameText = null;
  private Text passwordText = null;
  private Button showRepoBtn = null;
  private Combo myRepoCombo = null;

  public GitPushDialog(Shell parentShell) {
    super(parentShell);
  }

  @Override
  public void create() {
    super.create();
    getShell().setText(DLG_SHELL_TXT);
    getShell().setImage(ImageUtil.getImage(SHELL_IMG_16));
    setTitle(DLG_TITLE_TXT);
    setTitleImage(ImageUtil.getImage(SHELL_IMG_64));
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite dialogComposite = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(dialogComposite, SWT.NONE);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    GridLayout layout = new GridLayout(2, false);
    container.setLayout(layout);

    createGitAccountCombo(container);
    createUserName(container);
    createPassword(container);
    createShowRepo(container);

    return dialogComposite;
  }

  private void createGitAccountCombo(Composite container) {
    Label gitActLabel = new Label(container, SWT.NONE);
    gitActLabel.setText(ACT_TYPE_LBL_TXT);
    gitActLabel.setFont(BOLD_FONT);
    gitActLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    gitActCombo = new Combo(container, SWT.READ_ONLY);
    gitActCombo.setItems(GIT_ACCOUNTS);
    gitActCombo.select(0);
    gitActCombo.setFont(PLAIN_TXT_FONT);
    CommonUtil.setLayoutData(gitActCombo);
    CommonUtil.setDecorator(gitActCombo, ACT_TYPE_DECORATOR_TXT);
  }

  private void createUserName(Composite container) {
    Label userNameLabel = new Label(container, SWT.NONE);
    userNameLabel.setText(USER_NAME_TEXT);
    userNameLabel.setFont(BOLD_FONT);
    userNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    userNameText = new Text(container, SWT.BORDER);
    userNameText.setFont(PLAIN_TXT_FONT);
    addUserNameTextListener();
    CommonUtil.setLayoutData(userNameText);
    CommonUtil.setDecorator(userNameText, USER_NAME_DECORATOR_TXT);
  }

  private void createPassword(Composite container) {
    Label passwordLabel = new Label(container, SWT.NONE);
    passwordLabel.setText(PWD_LBL_TXT);
    passwordLabel.setFont(BOLD_FONT);
    passwordLabel.setLayoutData(new GridData(GridData.END, SWT.CENTER, false, false));

    passwordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
    CommonUtil.setLayoutData(passwordText);
    CommonUtil.setDecorator(passwordText, PWD_DECORATOE_TXT);
  }

  private void createShowRepo(Composite container) {
    showRepoBtn = new Button(container, SWT.PUSH);
    showRepoBtn.setText(REPO_LBL_TXT);
    showRepoBtn.setFont(BOLD_FONT);
    showRepoBtn.setLayoutData(new GridData(GridData.END, SWT.CENTER, false, false));
    showRepoBtn.setToolTipText(REPO_BTN_TOOL_TIP_TXT);;

    myRepoCombo = new Combo(container, SWT.READ_ONLY);
    myRepoCombo.setFont(PLAIN_TXT_FONT);
    myRepoCombo.setToolTipText(REPO_COMBO_DECORATOR_TXT);
    CommonUtil.setLayoutData(myRepoCombo);
    addRepoBtnListener();
  }

  private void addRepoBtnListener() {
    showRepoBtn.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            populateRepoCombo();
          }
        });
  }

  private void addUserNameTextListener() {
    userNameText.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            setMessage("");
          }
        });
  }

  private void populateRepoCombo() {
    // get the list of repositories;
    String[] repos = new String[] {"a", "b", "c", "d"};
    myRepoCombo.setItems(repos);
    myRepoCombo.select(0); // if items are not empty
  }

  @Override
  protected void okPressed() {
    if (isAccountValid()) {
      // Perform the operation
    }
    //    super.okPressed();
  }

  @Override
  protected void cancelPressed() {
    super.cancelPressed();
  }
  
  @Override
  protected boolean isResizable() {
    return true;
  }

  private boolean isAccountValid() {
    boolean isValidFlag = false;
    if (userNameText.getText().isEmpty())
      setMessage("User name cannot be empty", IMessageProvider.ERROR);
    else if (passwordText.getText().isEmpty())
      setMessage("Password cannot be empty", IMessageProvider.ERROR);
    else {
      isValidFlag = true;
      setMessage("");
    }
    return isValidFlag;
  }
}
