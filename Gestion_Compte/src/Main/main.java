package Main;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import InterfaceGUI.MainWindow;
import Model.Banque;

import Model.CompteEpargne;
public class main {
	   public static void main(String[] args) {
	        Banque banque = new Banque();
	        banque.chargerDonnees();
	        
	        SwingUtilities.invokeLater(() -> {
	            MainWindow mainWindow = new MainWindow(banque);
	            mainWindow.setVisible(true);
	            
	            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	                banque.sauvegarderDonnees();
	            }));
	        });
	    }
}