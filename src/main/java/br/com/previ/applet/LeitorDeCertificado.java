package src.main.java.br.com.previ.applet;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.KeyStore;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import netscape.javascript.JSObject;
import br.gov.frameworkdemoiselle.certificate.applet.action.AppletExecute;
import br.gov.frameworkdemoiselle.certificate.applet.config.AppletConfig;
import br.gov.frameworkdemoiselle.certificate.applet.factory.AppletExecuteFactory;
import br.gov.frameworkdemoiselle.certificate.applet.factory.FactoryException;
import br.gov.frameworkdemoiselle.certificate.applet.view.JKeyStoreDialog;



public class LeitorDeCertificado extends JApplet { 
	
	private static final long serialVersionUID = 1L; 

	private JKeyStoreDialog keyStoreDialog;

	
	
	/**
	 * Inicializacao da Applet 
	 */ 
	@Override 
	public void init() { 

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) { 
			if (AppletConfig.LOOK_AND_FEEL.getValue().equals(info.getName())) { 
				try { 
					UIManager.setLookAndFeel(info.getClassName()); 
				} catch (ClassNotFoundException erro) { 
					erro.printStackTrace(); 
				} catch (InstantiationException erro) { 
					erro.printStackTrace(); 
				} catch (IllegalAccessException erro) { 
					erro.printStackTrace(); 
				} catch (UnsupportedLookAndFeelException erro) { 
					erro.printStackTrace(); 
				} 
				break; 
			} 
		} 

		AppletConfig.setApplet(this); 
		keyStoreDialog = new JKeyStoreDialog(); 

		this.getContentPane().setLayout(null); 
		this.setSize(keyStoreDialog.getDimension()); 
		this.getRootPane().setDefaultButton(keyStoreDialog.getRunButton()); 

		keyStoreDialog.addButtonCancelActionListener(new ActionListener() { 
			
			public void actionPerformed(ActionEvent actionEvent) { 
				cancelButton_actionPerformed(); 
			} 
		}); 

		keyStoreDialog.addButtonRunActionListener(new ActionListener() { 
			
			public void actionPerformed(ActionEvent actionEvent) { 
				runButton_actionPerformed(); 
			} 
		}); 

		keyStoreDialog.addScrollPaneLineKeyListener(new KeyListener() { 

		 
			public void keyTyped(KeyEvent keyEvent) { 

			} 

			public void keyReleased(KeyEvent keyEvent) { 

			} 

			
			public void keyPressed(KeyEvent keyEvent) { 
				table_KeyPressed(keyEvent); 
			} 
		}); 

		keyStoreDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 
		keyStoreDialog.setComponentOrientation(this.getComponentOrientation()); 
		keyStoreDialog.setLocationRelativeTo(this); 

		if (keyStoreDialog.getCertificatesCount() != 0) { 
			keyStoreDialog.setVisible(true); 
		} 

		// Se ocorrer uma falha no carregamento do keystore, efetua a chamada do 
		// javascript a ser executado neste caso 
		if (!keyStoreDialog.isLoaded()) { 
			JSObject window = JSObject.getWindow(this); 
			window.call(AppletConfig.PARAM_APPLET_JAVASCRIPT_POSTACTION_FAILURE.getValue(), null); 
		} 
	} 

	private void table_KeyPressed(KeyEvent keyEvent) { 
		switch (keyEvent.getKeyCode()) { 
		case KeyEvent.VK_TAB: // se a tecla pressionada for tab 
			int rowCount = keyStoreDialog.getTable().getRowCount(); 
			int selectedRow = keyStoreDialog.getTable().getSelectedRow(); 

			if (selectedRow == rowCount - 1) { 
				keyStoreDialog.getTable().requestFocus(); 
				keyStoreDialog.getTable().changeSelection(0, 0, false, false); 
			} else { 
				keyStoreDialog.getTable().requestFocus(); 
				keyStoreDialog.getTable().changeSelection(selectedRow + 1, 0, false, false); 
			} 
			break; 

		case KeyEvent.VK_SPACE: // Se a tecla pressionada for o espaco 
			runButton_actionPerformed(); 
			break; 
		} 
	} 

	/**
	 * Chamado ao clique do botao Ok 
	 */ 
	private void runButton_actionPerformed() { 

		try { 
			KeyStore keystore = keyStoreDialog.getKeyStore(); 
			String alias = keyStoreDialog.getAlias();
			
			//---------------------------------------------------------//

			if (keystore != null) {
				String className = AppletConfig.PARAM_APPLET_ACTION_EXECUTE.getValue(); 
				
				//----------------------------------------------------------------//
				AppletExecute appletExecute = AppletExecuteFactory.factory(className); 
				appletExecute.execute(keystore, alias, this); 
			} 
		} catch (FactoryException erro) { 
			erro.printStackTrace(); 
			JOptionPane.showMessageDialog(this, erro.getMessage(), AppletConfig.LABEL_DIALOG_OPTION_PANE_TITLE.getValue(), JOptionPane.ERROR_MESSAGE); 
		} catch (Throwable erro) { 
			erro.printStackTrace(); 
			JOptionPane.showMessageDialog(this, AppletConfig.MESSAGE_ERROR_UNEXPECTED.getValue() + " - " + erro.getMessage(), AppletConfig.LABEL_DIALOG_OPTION_PANE_TITLE.getValue(), JOptionPane.ERROR_MESSAGE); 
		} finally { 
			keyStoreDialog.dispose(); 
		} 
	} 

	/**
	 * Chamado ao clique do botao Cancelar
	 */ 
	private void cancelButton_actionPerformed() { 

		KeyStore keystore = keyStoreDialog.getKeyStore(); 
		String alias = keyStoreDialog.getAlias(); 

		keyStoreDialog.dispose(); 
		String className = AppletConfig.PARAM_APPLET_ACTION_EXECUTE.getValue(); 
		AppletExecute appletExecute = AppletExecuteFactory.factory(className); 
		appletExecute.cancel(keystore, alias, this); 
	} 

	public static Frame getParentFrame(Component child) { 
		Container container = child.getParent(); 
		while (container != null) { 
			if (container instanceof Frame) { 
				return (Frame) container; 
			} 
			container = container.getParent(); 
		} 
		return null; 
	} 

}