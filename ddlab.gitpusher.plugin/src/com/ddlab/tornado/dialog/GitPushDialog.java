package com.ddlab.tornado.dialog;

// import static com.ddlab.tornado.common.CommonConstants.ACT_TYPE_DECORATOR_TXT;
// import static com.ddlab.tornado.common.CommonConstants.ACT_TYPE_LBL_TXT;
// import static com.ddlab.tornado.common.CommonConstants.BOLD_FONT;
// import static com.ddlab.tornado.common.CommonConstants.DLG_SHELL_TXT;
// import static com.ddlab.tornado.common.CommonConstants.DLG_TITLE_TXT;
// import static com.ddlab.tornado.common.CommonConstants.GIT_ACCOUNTS;
// import static com.ddlab.tornado.common.CommonConstants.PLAIN_TXT_FONT;
// import static com.ddlab.tornado.common.CommonConstants.PWD_DECORATOE_TXT;
// import static com.ddlab.tornado.common.CommonConstants.PWD_LBL_TXT;
// import static com.ddlab.tornado.common.CommonConstants.REPO_BTN_TOOL_TIP_TXT;
// import static com.ddlab.tornado.common.CommonConstants.REPO_COMBO_DECORATOR_TXT;
// import static com.ddlab.tornado.common.CommonConstants.REPO_BTN_TXT;
// import static com.ddlab.tornado.common.CommonConstants.SHELL_IMG_16;
// import static com.ddlab.tornado.common.CommonConstants.SHELL_IMG_64;
// import static com.ddlab.tornado.common.CommonConstants.USER_NAME_DECORATOR_TXT;
import static com.ddlab.tornado.common.CommonConstants.ACT_TYPE_DECORATOR_TXT;
import static com.ddlab.tornado.common.CommonConstants.ACT_TYPE_LBL_TXT;
import static com.ddlab.tornado.common.CommonConstants.BOLD_FONT;
import static com.ddlab.tornado.common.CommonConstants.DLG_SHELL_TXT;
import static com.ddlab.tornado.common.CommonConstants.DLG_TITLE_TXT;
import static com.ddlab.tornado.common.CommonConstants.GIT_ACCOUNTS;
import static com.ddlab.tornado.common.CommonConstants.PLAIN_TXT_FONT;
import static com.ddlab.tornado.common.CommonConstants.PWD_DECORATOE_TXT;
import static com.ddlab.tornado.common.CommonConstants.PWD_LBL_TXT;
import static com.ddlab.tornado.common.CommonConstants.PWD_NOT_EMPTY_TXT;
import static com.ddlab.tornado.common.CommonConstants.READ_ME_DECO_TXT;
import static com.ddlab.tornado.common.CommonConstants.READ_ME_INFO_TXT;
import static com.ddlab.tornado.common.CommonConstants.REPO_BTN_TOOL_TIP_TXT;
import static com.ddlab.tornado.common.CommonConstants.REPO_BTN_TXT;
import static com.ddlab.tornado.common.CommonConstants.REPO_COMBO_DECORATOR_TXT;
import static com.ddlab.tornado.common.CommonConstants.SHELL_IMG_16;
import static com.ddlab.tornado.common.CommonConstants.SHELL_IMG_64;
import static com.ddlab.tornado.common.CommonConstants.UNAME_NOT_EMPTY_TXT;
import static com.ddlab.tornado.common.CommonConstants.USER_NAME_DECORATOR_TXT;
import static com.ddlab.tornado.common.CommonConstants.USER_NAME_TEXT;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.ddlab.generator.IGitIgnoreGen;
import com.ddlab.generator.IReadMeGen;
import com.ddlab.generator.gitignore.GitIgnoreGenerator;
import com.ddlab.generator.readme.ReadMeGenerator;
import com.ddlab.tornado.Activator;
import com.ddlab.tornado.common.CommonUtil;
import com.ddlab.tornado.common.ImageUtil;
import com.ddlab.tornado.github.RepoLoaderThread;

public class GitPushDialog extends TitleAreaDialog {

  private Combo gitActCombo = null;
  private Text userNameText = null;
  private Text passwordText = null;
  private Button showRepoBtn = null;
  private Combo myRepoCombo = null;
  private Text readMeTxt = null;
  private File selectedFile;

  public GitPushDialog(Shell parentShell, File selectedFile) {
    super(parentShell);
    this.selectedFile = selectedFile;
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
    createForReadMe(container);

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
    CommonUtil.setProposalDecorator(gitActCombo, ACT_TYPE_DECORATOR_TXT);
  }

  private void createUserName(Composite container) {
    Label userNameLabel = new Label(container, SWT.NONE);
    userNameLabel.setText(USER_NAME_TEXT);
    userNameLabel.setFont(BOLD_FONT);
    userNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    CommonUtil.setRequiredDecorator(userNameLabel, USER_NAME_DECORATOR_TXT);

    userNameText = new Text(container, SWT.BORDER);
    userNameText.setFont(PLAIN_TXT_FONT);
    addUserNameTextListener();
    CommonUtil.setLayoutData(userNameText);
  }

  private void createPassword(Composite container) {
    Label passwordLabel = new Label(container, SWT.NONE);
    passwordLabel.setText(PWD_LBL_TXT);
    passwordLabel.setFont(BOLD_FONT);
    passwordLabel.setLayoutData(new GridData(GridData.END, SWT.CENTER, false, false));
    CommonUtil.setRequiredDecorator(passwordLabel, PWD_DECORATOE_TXT);

    passwordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
    addPwdTextListener();
    CommonUtil.setLayoutData(passwordText);
  }

  private void createShowRepo(Composite container) {
    showRepoBtn = new Button(container, SWT.PUSH);
    showRepoBtn.setText(REPO_BTN_TXT);
    showRepoBtn.setFont(BOLD_FONT);
    showRepoBtn.setLayoutData(new GridData(GridData.END, SWT.CENTER, false, false));
    showRepoBtn.setToolTipText(REPO_BTN_TOOL_TIP_TXT);

    myRepoCombo = new Combo(container, SWT.READ_ONLY);
    myRepoCombo.setFont(PLAIN_TXT_FONT);
    myRepoCombo.setToolTipText(REPO_COMBO_DECORATOR_TXT);
    CommonUtil.setLayoutData(myRepoCombo);
    addRepoBtnListener();
  }

  private void createForReadMe(Composite container) {
    Label readMeLbl = new Label(container, SWT.NONE);
    readMeLbl.setText(READ_ME_INFO_TXT);
    readMeLbl.setFont(BOLD_FONT);
    GridData userNamegData = new GridData();
    userNamegData.horizontalSpan = 2;
    readMeLbl.setLayoutData(userNamegData);

    readMeTxt = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    readMeTxt.setFont(PLAIN_TXT_FONT);

    GridData gData = new GridData();
    gData.heightHint = 40;
    gData.horizontalAlignment = SWT.FILL; // GridData.FILL;
    gData.horizontalSpan = 2;
    readMeTxt.setLayoutData(gData);

    CommonUtil.setRightSideControlDecorator(readMeLbl, READ_ME_DECO_TXT);
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

  private void addPwdTextListener() {
    passwordText.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            setMessage("");
          }
        });
  }

  private void populateRepoCombo() {
    // get the list of repositories;
    if (!isAccountValid()) return;
    myRepoCombo.removeAll();
    List<String> repoList = new ArrayList<>();
    IRunnableWithProgress op =
        new RepoLoaderThread(userNameText.getText(), passwordText.getText(), myRepoCombo, repoList);

    //    Display.getDefault()
    //        .syncExec(
    //            new Runnable() {
    //              public void run() {
    //                try {
    //                  new ProgressMonitorDialog(new Shell()).run(true, true, op);
    //                } catch (InvocationTargetException | InterruptedException e) {
    //                  e.printStackTrace();
    //                }
    //              }
    //            });

    try {
      new ProgressMonitorDialog(new Shell()).run(true, true, op);
      if (repoList.size() != 0) {
        String[] repos = repoList.toArray(new String[0]);
        myRepoCombo.setItems(repos);
        myRepoCombo.select(0);
      }

    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
      Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e);
      ErrorDialog.openError(new Shell(), "Error", e.getMessage(), status);
    }

    //
    //
    //    String[] repos;
    //    try {
    //      repos = GitHubUtil.getAllRepositories(userNameText.getText(), passwordText.getText());
    //      myRepoCombo.setItems(repos);
    //      // if items are not empty
    //      if (repos != null && repos.length != 0) myRepoCombo.select(0);
    //    } catch (Exception e) {
    //      Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(),
    // e);
    //
    //      //    	String PID = Activator.PLUGIN_ID;
    //      //    	MultiStatus status = new MultiStatus(PID, 1, "Error 1", null);
    //      //    	status.add(new Status(IStatus.ERROR, PID, 1, "Error 2", null));
    //      //    	status.add(new Status(IStatus.ERROR, PID, 1, "Error 3", null));
    //      //    	status.add(new Status(IStatus.ERROR, PID, 1, "Error 4", null));
    //
    //      ErrorDialog.openError(new Shell(), "Error", e.getMessage(), status);
    //      System.out.println("=========>" + e.getMessage());
    //      e.printStackTrace();
    //    }
    //
    //

  }

  @Override
  protected void okPressed() {
    if (isAccountValid()) {
      // Perform the operation
    	 WorkbenchPlugin.getDefault().getPreferenceStore().setValue("RUN_IN_BACKGROUND", false);
//    	runInBackgroundProgressService();
    	
    	show13();
    	
//    	MessageDialog.openInformation(new Shell(), "Done", "All Done Paa");
    	
      generateGitIgnoreFile();
      generateReadMeFile();
      close();
      
      
      
    }
    //    super.okPressed();
  }
  
//  private void runInBackgroundProgressService() {
//	    Job job =
//	        new Job("Initiating a critical service for "+selectedFile.getName()) {
//
//	          @Override
//	          protected IStatus run(IProgressMonitor monitor) {
//	            monitor.beginTask("Mission critical App for "+selectedFile.getName(), 10);
//	            // execute the task ...
//	            
//	            try {
//	            	for( int i = 0 ; i < 10; i++) {
//	            		TimeUnit.SECONDS.sleep(3);
//	            		monitor.setTaskName("Completed task "+i+"of 10");
////	            		monitor.worked(i*10);
//	            		monitor.worked(i);
//	            	}
//	            	MessageDialog.openInformation(new Shell(), "Done", "All Done Paa");
//	            }
//	            catch(Exception e) {
//	            	e.printStackTrace();
//	            }
//	            monitor.done();
//	            return Status.OK_STATUS;
//	          }
//	        };
//	    job.schedule();
//	    //		job.setUser(true);
//
//	  }
  
  void show13() {

	    //	  WorkbenchPlugin.getDefault().getPreferenceStore().setValue("RUN_IN_BACKGROUND", false);
	    // https://www.eclipse.org/forums/index.php/t/262592/

	    Job job =
	        new Job("Progress Information for "+selectedFile.getName()) {

	          @Override
	          protected IStatus run(IProgressMonitor monitor) {
	            monitor.beginTask("Validation in Progress fro "+selectedFile.getName(), 10);
	            for (int i = 0; i < 10; i++) {
	              if (monitor.isCanceled()) {
	                return Status.CANCEL_STATUS;
	              }

	              monitor.subTask("Performing validation " + (i + 1) + " of " + "10" + "...");

	              try {
//	                Thread.sleep(100);
	            	  TimeUnit.SECONDS.sleep(2);
	              } catch (InterruptedException e) {

	                e.printStackTrace();
	              }
	              monitor.worked(1);
	            }
	            monitor.done();
	            
	            Display.getDefault().syncExec( () -> {
	            	System.out.println( ); 
	            	MessageDialog.openInformation(new Shell(), "Done", "All Done Paa");
	            }
	            );
	            
	            
	            return Status.OK_STATUS;
	          }
	        };

	    //      PlatformUI.getWorkbench().getProgressService().showInDialog(new Shell(), job);

	    //      PlatformUI.getWorkbench().getProgressService().showInDialog(getSite().getShell(), job);

	    job.setUser(true);
	    job.schedule();
	    PlatformUI.getWorkbench().getProgressService().showInDialog(getShell(), job);
	  }

  private void generateReadMeFile() {
    IReadMeGen readMeGen = new ReadMeGenerator();
    String projectName = selectedFile.getName();
    String description =
        (readMeTxt.getText() == null || readMeTxt.getText().trim().isEmpty())
            ? "To be updated later"
            : readMeTxt.getText();
    String readMeContents = readMeGen.generateReadMeMdContents(projectName, description, null);
    Path readMePath = Paths.get(selectedFile.getAbsolutePath() + File.separator + "README.md");
    try {
      if (Files.exists(readMePath)) return;
      Files.write(readMePath, readMeContents.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void generateGitIgnoreFile() {
    IGitIgnoreGen gitIgnoreGenerator = new GitIgnoreGenerator();
    String gitIgnoreContents = gitIgnoreGenerator.generateGitIgnoreContents();
    Path gitIgnorePath = Paths.get(selectedFile.getAbsolutePath() + File.separator + ".gitignore");
    try {
      if (Files.exists(gitIgnorePath)) return;
      Files.write(gitIgnorePath, gitIgnoreContents.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    if (userNameText.getText().isEmpty()) setMessage(UNAME_NOT_EMPTY_TXT, IMessageProvider.ERROR);
    else if (passwordText.getText().isEmpty())
      setMessage(PWD_NOT_EMPTY_TXT, IMessageProvider.ERROR);
    else {
      isValidFlag = true;
      setMessage("");
    }
    return isValidFlag;
  }
}
