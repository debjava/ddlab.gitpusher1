package com.ddlab.tornado.handlers;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ddlab.tornado.dialog.GitPushDialog;

public class GitPushHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    Shell shell = window.getShell();
    //		MyTitleAreaDialog dialog = new MyTitleAreaDialog(shell);
    //		dialog.create();
    ISelectionService service = window.getSelectionService();
    IStructuredSelection structuredSelection = (IStructuredSelection) service.getSelection();
    Object selctionElement = structuredSelection.getFirstElement();
    if (selctionElement instanceof IAdaptable) {
//    	IAdaptable adaptable = (IAdaptable) selctionElement;
    	
    	IResource resource = (IResource)((IAdaptable)selctionElement).getAdapter(IResource.class);
    	
    	
//    	System.out.println("It is ........" ); 
//    	IResource resource = (IResource) adaptable
//				.getAdapter(IResource.class);
    	String resourcePath = resource.getLocationURI().getPath();
    	System.out.println("Resource path : "+resourcePath); 
    	
    	System.out.println("Resource name:::"+resource.getName() ); 
    	
    	System.out.println("resource.getLocation()===>"+resource.getLocation() ); 
    	System.out.println("resource.getRawLocation()===>"+resource.getRawLocation() ); 
    	
    	System.out.println(resource.getFullPath().toFile().getAbsolutePath() );
//    	File selectedFile = resource.getFullPath().toFile();
    	
    	File selectedFile = resource.getLocation().toFile();
    	
    	GitPushDialog dialog = new GitPushDialog(shell, selectedFile);
        dialog.create();

        dialog.open();
    	
    }
//    System.out.println(structuredSelection.getFirstElement());

//    GitPushDialog dialog = new GitPushDialog(shell, File selectedFile);
//    dialog.create();
//
//    dialog.open();
    return null;
  }

  public Object execute11(ExecutionEvent event) throws ExecutionException {

    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    // set selection service
    ISelectionService service = window.getSelectionService();
    // set structured selection
    IStructuredSelection structured = (IStructuredSelection) service.getSelection();

    System.out.println(structured.getFirstElement());

    Iterator<?> itr = structured.iterator();
    while (itr.hasNext()) {
      Object selctionElement = itr.next();
      System.out.println(selctionElement);
      //			if (selctionElement instanceof IAdaptable) {
      //				IAdaptable adaptable = (IAdaptable) selctionElement;
      //				IResource resource = (IResource) adaptable
      //						.getAdapter(IResource.class);
      //				String resourcePath = resource.getLocationURI().getPath();
      //				resourcePath = resourcePath.startsWith("/") ? resourcePath
      //						.substring(resourcePath.indexOf("/") + 1,
      //								resourcePath.length()) : resourcePath;
      //				resourceList.add(resourcePath);
      //			}
    }

    //		Iterator<IStructuredSelection> selectItr =  structured.iterator();
    //		while( selectItr.hasNext() ) {
    //			System.out.println(selectItr.next().getFirstElement());
    //		}

    // check if it is an IFile
    //		if (structured.getFirstElement() instanceof IFile) {
    //			// get the selected file
    //			IFile file = (IFile) structured.getFirstElement();
    //			// get the path
    //			IPath path = file.getLocation();
    //			System.out.println(path.toPortableString());
    //		}

    // check if it is an ICompilationUnit
    //		if (structured.getFirstElement() instanceof ICompilationUnit) {
    //			ICompilationUnit cu = (ICompilationUnit) structured.getFirstElement();
    //			System.out.println(cu.getElementName());
    //		}

    //		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    MessageDialog.openInformation(window.getShell(), "Gitpusher1", "Push to Github");
    return null;
  }
}
