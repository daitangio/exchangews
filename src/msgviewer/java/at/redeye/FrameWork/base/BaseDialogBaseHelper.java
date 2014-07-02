/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.base.translation.TranslationHelper;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.NoticeIfChangedTextField;
import at.redeye.FrameWork.widgets.datetime.IDateTimeComponent;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import javax.swing.*;

/**
 * 
 * @author martin
 */
public class BaseDialogBaseHelper implements BindVarInterface {
	/**
	 * reference to the root object
	 */
	protected Root root;
	private Transaction transaction = null;

	/**
	 * title of the dialog
	 */
	protected String title;
	private DBConnection con = null;

	/**
	 * reference to the logger object
	 */
	protected static Logger logger = Logger
			.getLogger(BaseDialogBaseHelper.class.getName());
	private Transaction seq_transaction = null;
	public Timer autoRefreshTimer = null;
	public TimerTask autoRefreshTask = null;

	boolean edited = false;
	public BindVarInterface bind_vars;
	protected List<Runnable> onCloseListeners;
	protected CloseSubDialogHelper close_subdialog_helper;

	/**
	 * All keys ESC, or F1, F2 listeners are registered in this container
	 */
	protected HashMap<KeyStroke, Vector<Runnable>> listen_key_events = null;
	private JRootPane myrootPane;
	protected Runnable HelpWinRunnable;
	protected UniqueDialogHelper unique_dialog_helper;
	protected TranslationHelper translation_helper;
	protected boolean autoswitch_trans_first_run = true;

	private static int default_pos_x = 300;
	private static int default_pos_y = 300;
	/**
	 * language the dialog is programmed in if not set, the settings from
	 * Root.getBaseLangague() are used
	 */
	private String base_language;

	private class ActionKeyListener implements ActionListener {
		KeyStroke key;

		public ActionKeyListener(KeyStroke key) {
			this.key = key;
		}

		public void actionPerformed(ActionEvent e) {
			if (listen_key_events == null) {
				return;
			}

			Vector<Runnable> functions = listen_key_events.get(key);

			for (Runnable runnable : functions) {
				runnable.run();
			}
		}
	}

	BaseDialogBase parent;

	public BaseDialogBaseHelper(final BaseDialogBase parent, Root root,
			String title, JRootPane myrootPane, boolean do_not_inform_root) {
		this.parent = parent;
		this.root = root;
		this.title = title;
		this.myrootPane = myrootPane;

		initCommon(do_not_inform_root);
	}

	protected void initCommon(boolean do_not_inform_root) {
		translation_helper = new TranslationHelper(root, parent, this);
		parent.setTitle(MlM(title));

		root.loadMlM4Class(this, "de");

		if (!do_not_inform_root)
			root.informWindowOpened(parent);

		if (logger.isDebugEnabled()) {
			logger.debug(title);
		}

		parent.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (parent.canClose()) {
					parent.close();
				}
			}
		});

		String id = parent.getUniqueDialogIdentifier("SetToLastXYPos");

		int x = Integer.parseInt(root.getSetup().getLocalConfig(
				id.concat(Setup.WindowX), String.valueOf(default_pos_x += 30)));
		int y = Integer.parseInt(root.getSetup().getLocalConfig(
				id.concat(Setup.WindowY), String.valueOf(default_pos_y += 30)));
		int w = Integer.parseInt(root.getSetup().getLocalConfig(
				id.concat(Setup.WindowWidth), "0"));
		int h = Integer.parseInt(root.getSetup().getLocalConfig(
				id.concat(Setup.WindowHeight), "0"));

		// Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dim = getVirtualScreenSize();
		//java.awt.Toolkit.getDefaultToolkit()
		// java.awt.GraphicsDevice

		Point mouse_point = MouseInfo.getPointerInfo().getLocation();

		if (x < mouse_point.x && x + w > mouse_point.x) {
			// alter Wert is super, den lassen wir so
                } else if( Math.abs( x + w -  mouse_point.x ) < w ) {
                        // alter Wert is super, den lassen wir so
		} else {
			x = mouse_point.x - 100;
		}

		if (y < mouse_point.y && y + h > mouse_point.y) {
			// alter Wert is super, den lassen wir so
                } else if( Math.abs( y + h -  mouse_point.y ) < h ) {
                        // alter Wert is super, den lassen wir so
		} else {
			y = mouse_point.y - 100;
		}

		if (dim.getWidth() < x + parent.getWidth())
			x = 100;

		if (dim.getHeight() < y + parent.getHeight())
			y = 100;

		if (x < 0)
			x = 100;

		if (y < 0)
			y = 100;

		logger.info("setting bounds to: " + x + "x" + y);
		parent.setBounds(x, y, 0, 0);
		logger.info("position now: " + parent.getX() + "x" + parent.getY());

		if (w > 0 && h > 0 && parent.openWithLastWidthAndHeight()) {
			logger.info(String.format("x (%d) + w (%d) = %d dim.Width: %d", x,
					w, x + w, (int) dim.getWidth()));

			if (x + w > dim.getWidth()) {
				logger.info("reducing with");
				w = (int) dim.getWidth() - x;
			}

			logger.info(String.format("y (%d) + h (%d) = %d dim.Height: %d", y,
					h, y + h, (int) dim.getHeight()));

			if (y + h > dim.getHeight()) {
				logger.info("reducing height");
				h = (int) dim.getHeight() - y;
			}

			logger.info("set size to: " + w + "x" + h);
			parent.setPreferredSize(new Dimension(w, h));
		}

		registerActionKeyListener(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new Runnable() {

            @Override
					public void run() {
						if (parent.canClose()) {
							parent.close();
						}
					}
				});

		loadStuff();
	}

	/* should be removed later */
	private void loadStuff() {
		StringUtils.set_defaultAutoLineLenght(Integer.valueOf(root.getSetup()
				.getLocalConfig(
						FrameWorkConfigDefinitions.DefaultAutoLineBreakWidth)));
	}

	/**
	 * automatically opens the Help Windows, when F1 is pressed
	 * 
	 * @param runnable
	 *            This runnable should open the Help Window
	 */
	public void registerHelpWin(Runnable runnable) {
		HelpWinRunnable = runnable;

		registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
				runnable);
	}

	/**
	 * returns the virtual screensize in a multimonitor system
	 * 
	 * @return
	 */
	public static Dimension getVirtualScreenSize() {
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				virtualBounds = virtualBounds.union(gc[i].getBounds());
			}
		}
		return virtualBounds.getSize();
	}

	/**
	 * opens the registerd Help win by Hand
	 */
	public void callHelpWin() {
		if (HelpWinRunnable != null) {
			setWaitCursor();
			HelpWinRunnable.run();
			setNormalCursor();
		}
	}

	/**
	 * Setzt den Sanduhr Mauscursor
	 */
	public void setWaitCursor() {
		setWaitCursor(true);
	}

	/**
	 * Setzt den Sanduhr, oder "normale" Mauscursor
	 * 
	 * @param state
	 *            <b>true</b> für die Sanduhr und <b>false</b> für den nurmalen
	 *            Cursor
	 */
	public void setWaitCursor(boolean state) {
		if (state)
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		else
			parent.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Setzt wieder den "normalen" Mauscursor
	 */
	public void setNormalCursor() {
		setWaitCursor(false);
	}

	/**
	 * Konfiguriert das jScrollpanel entsprechen dem im Setup hinterlegten
	 * Geschwindigkeit. Vom User über den Parameter VerticalScrollingSpeed
	 * einstellbar.
	 * 
	 * @param scroll_panel
	 */
	public void adjustScrollingSpeed(JScrollPane scroll_panel) {
		try {
			adjustScrollingSpeed(scroll_panel.getVerticalScrollBar(),
					BaseAppConfigDefinitions.VerticalScrollingSpeed);
			adjustScrollingSpeed(scroll_panel.getVerticalScrollBar(),
					BaseAppConfigDefinitions.HorizontalScrollingSpeed);
		} catch (NumberFormatException ex) {
			logger.error(ex);
			return;
		}
	}

	protected void adjustScrollingSpeed(JScrollBar ScrollBar, DBConfig config) {
		String value = root.getSetup().getLocalConfig(config);

		Integer i = Integer.parseInt(value);

		if (i <= 0) {
			logger.error("invalid scrolling interval: " + i
					+ " using default value: " + config.getConfigValue());
			i = Integer.parseInt(config.getConfigValue());
		}

		ScrollBar.setUnitIncrement(i);
	}

	/**
	 * Little helper function that sets the frame visible and push it to front,
	 * by useing the wait cursor.
	 * 
	 * @param frame
	 */
	public void invokeDialog(JFrame frame) {
		setWaitCursor();
		frame.setVisible(true);
		frame.toFront();
		setNormalCursor();
	}

	/**
	 * Little helper function that sets the frame visible and push it to front,
	 * by useing the wait cursor.
	 * 
	 * @param frame
	 */
	public void invokeDialog(BaseDialogBase dlg) {
		setWaitCursor();
		dlg.setVisible(true);
		dlg.toFront();

		if (close_subdialog_helper == null)
			close_subdialog_helper = new CloseSubDialogHelper(parent);

		if (parent.closeSubdialogsOnClose())
			close_subdialog_helper.closeSubDialog(dlg);

		setNormalCursor();
	}

	void invokeMainDialog(BaseDialogBase dialog) {
		setWaitCursor();
		dialog.setVisible(true);
		dialog.toFront();
		setNormalCursor();
	}

	public void invokeDialogUnique(BaseDialogBase dialog) {
		setWaitCursor();

		if (unique_dialog_helper == null)
			unique_dialog_helper = new UniqueDialogHelper();

		if (close_subdialog_helper == null)
			close_subdialog_helper = new CloseSubDialogHelper(parent);

		BaseDialogBase d_unique = unique_dialog_helper
				.invokeUniqueDialog(dialog);
		d_unique.setVisible(true);
		d_unique.toFront();

		if (parent.closeSubdialogsOnClose())
			close_subdialog_helper.closeSubDialog(dialog);

		setNormalCursor();
	}

	void invokeDialogModal(BaseDialogDialog dlg) {
		setWaitCursor();
		dlg.setModalityType(ModalityType.APPLICATION_MODAL);
		dlg.setVisible(true);
		dlg.toFront();

		if (close_subdialog_helper == null)
			close_subdialog_helper = new CloseSubDialogHelper(parent);

		if (parent.closeSubdialogsOnClose())
			close_subdialog_helper.closeSubDialog(dlg);

		setNormalCursor();
	}

	public void registerOnCloseListener(Runnable runnable) {
		if (runnable == null)
			return;

		if (onCloseListeners == null)
			onCloseListeners = new LinkedList<Runnable>();

		onCloseListeners.add(runnable);
	}

	public void deregisterOnCloseListener(Runnable runnable) {
		if (onCloseListeners == null)
			return;

		int index = onCloseListeners.indexOf(runnable);

		if (index == -1)
			return;

		onCloseListeners.remove(index);
	}

	public void setEdited() {
		setEdited(true);
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean val) {
		edited = val;

		if (edited == false) {
			setBindVarsChanged(false);
		}
	}

	public void clearEdited() {
		setEdited(false);
	}

	/**
	 * Checks, if data within the table have been change, asks the user what
	 * sould be done (save it, don't save it, or cancel current operation
	 * 
	 * @param tm
	 *            TableManipulator object
	 * @return 1 when the data should by saved <br/>
	 *         0 on saving should be done <br/>
	 *         -1 cancel current operation <br/>
	 * 
	 */
	public int checkSave(TableManipulator tm) {
		tm.stopEditing();

		if (tm.getEditedRows().isEmpty() && edited == false) {
			return 0;
		} else {
			int ret = checkSave();

			if (ret == -1) {
				return -1;
			} else if (ret == 0) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	/**
	 * @return 1 on Save Data <br/>
	 *         0 on Don't Save <br/>
	 *         -1 on Cancel <br/>
	 */
	public int checkSave() {
		Object[] options = { MlM("Daten Speichern"),
				MlM("Änderungen verwerfen"), MlM("Abbrechen") };

		int n = JOptionPane
				.showOptionDialog(
						null,
						StringUtils
								.autoLineBreak(MlM("Sie haben Daten verändert. "
										+ "Möchten Sie die Daten vor dem Verlassen des Dialoges speichern?")),
						parent.getTitle(), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		switch (n) {
		case 0:
			return 1;
		case 1:
			return 0;
		default:
			return -1;
		}
	}

	/**
	 * Ermittelt den nächsten Wert für eine gegebene Sequenz
	 * 
	 * @param seqName
	 * @return den nächsten Wert der Sequenz
	 * @throws java.sql.SQLException
	 * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
	 * @throws WrongBindFileFormatException
	 * @throws TableBindingNotRegisteredException
	 * @throws IOException
	 */
	public int getNewSequenceValue(String seqName) throws SQLException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException,
			TableBindingNotRegisteredException, IOException {
		if (getTransaction().getDBMSType() == SupportedDBMSTypes.DB_SQLITE) {
			int value = getTransaction().getNewSequenceValue(seqName, 1234567);
			return value;
		} else {

			if (seq_transaction != null && !seq_transaction.isOpen()) {
				closeTransaction(seq_transaction);
				seq_transaction = null;
			}

			if (seq_transaction == null) {
				seq_transaction = getNewTransaction();
			}

			int value = seq_transaction.getNewSequenceValue(seqName, 1234567);

			seq_transaction.commit();

			return value;
		}
	}

	/**
	 * Ermittelt den nächsten Wert für eine gegebene Sequenz
	 * 
	 * @param seqName
	 * @return den nächsten Wert der Sequenz
	 * @throws java.sql.SQLException
	 * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
	 * @throws WrongBindFileFormatException
	 * @throws TableBindingNotRegisteredException
	 * @throws IOException
	 */
	public int getNewSequenceValues(String seqName, int number)
			throws SQLException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, TableBindingNotRegisteredException,
			IOException {
		if (getTransaction().getDBMSType() == SupportedDBMSTypes.DB_SQLITE) {
			int value = getTransaction().getNewSequenceValues(seqName, number,
					1234567);
			return value;
		} else {

			if (seq_transaction != null && !seq_transaction.isOpen()) {
				closeTransaction(seq_transaction);
				seq_transaction = null;
			}

			if (seq_transaction == null) {
				seq_transaction = getNewTransaction();
			}

			int value = seq_transaction.getNewSequenceValues(seqName, number,
					1234567);

			seq_transaction.commit();

			return value;
		}
	}

	/**
	 * @return The Transaction object for this dialog This Transaction object
	 *         will be automatically closed, on closing this this dialog. The
	 *         Transaction object will be only created once in the lifetime of
	 *         the dialog. So caching the Transaction object is not required.
	 *         <b>Can return null, in case of no database connection.</b>
	 */
	public Transaction getTransaction() {
		if (con == null) {
			con = root.getDBConnection();
		}
		// Here we have to check -> NULL pointer exception if no connection
		// exists, e.g. before inital Setup
		if (con == null) {
			return null;
		}

		if (con.hashCode() != root.getDBConnection().hashCode()) {
			con = root.getDBConnection();
			transaction = null;
		}

		if (transaction != null) {
			try {

				if (!transaction.isOpen()) {
					root.getDBConnection().closeTransaction(transaction);
					transaction = null;
				}
			} catch (SQLException ex) {
				logger.error(StringUtils.exceptionToString(ex));
			}
		}

		if (transaction != null) {
			return transaction;
		}

		if (root.getDBConnection() == null) {
			return null;
		}

		transaction = root.getDBConnection().getNewTransaction();

		return transaction;
	}

	/**
	 * @return A new Transaction object, of the current database connection This
	 *         Transactino won't be closed on dialog closing event automatically
	 *         You have to close each allocated Transaction object yourself by
	 *         calling <b>closeTransaction()</b>
	 * 
	 *         The Transaction object will by destroyed atomatically on
	 *         appliaction shutdown
	 */
	public Transaction getNewTransaction() {
		if (root.getDBConnection() == null) {
			return null;
		}

		Transaction trans = root.getDBConnection().getNewTransaction();

		return trans;
	}

	/**
	 * closes a given Transaction object. Rollback is done automatically.
	 * 
	 * @param tran
	 *            a valid Transaction object
	 * @throws SQLException
	 *             if rollback fails
	 */
	public void closeTransaction(Transaction tran) throws SQLException {
		if (root.getDBConnection() == null) {
			return;
		}

		if (tran == null) {
			return;
		}

		if (tran.isOpen())
			tran.rollback();

		root.getDBConnection().closeTransaction(tran);
	}

	/**
	 * closes the current dialog.
	 */
	public void close() {

		cancelAutoRefreshTimer();

		String id_xy = parent.getUniqueDialogIdentifier("SaveLastXYPos");
		String id_wh = parent.getUniqueDialogIdentifier("SaveWidthHeight");

		logger.info("store size to: " + parent.getWidth() + "x"
				+ parent.getHeight());

		root.getSetup().setLocalConfig(id_xy.concat(Setup.WindowX),
				Integer.toString(parent.getX()));
		root.getSetup().setLocalConfig(id_xy.concat(Setup.WindowY),
				Integer.toString(parent.getY()));
		root.getSetup().setLocalConfig(id_wh.concat(Setup.WindowWidth),
				Integer.toString(parent.getWidth()));
		root.getSetup().setLocalConfig(id_wh.concat(Setup.WindowHeight),
				Integer.toString(parent.getHeight()));

		try {
			if (transaction != null) {
				transaction.rollback();
				root.getDBConnection().closeTransaction(transaction);
			}
			if (seq_transaction != null) {
				seq_transaction.rollback();
				root.getDBConnection().closeTransaction(seq_transaction);
			}
		} catch (SQLException ex) {
			logger.error(ex);
		}

		if (onCloseListeners != null) {
			for (Runnable run : onCloseListeners)
				run.run();

			onCloseListeners.clear();
		}

		root.informWindowClosed(parent);

		parent.dispose();
	}

	void registerListenersOnRootPane(JRootPane myrootPane) {

		this.myrootPane = myrootPane;

		// alle im Container beinhalteten listener anhängen
		if (listen_key_events != null) {
			for (KeyStroke key : listen_key_events.keySet()) {
				myrootPane.registerKeyboardAction(new ActionKeyListener(key),
						key, JComponent.WHEN_IN_FOCUSED_WINDOW);
			}
		}
	}

	public void registerActionKeyListenerOnRootPane(KeyStroke key) {
		if (myrootPane == null)
			return;

		myrootPane.registerKeyboardAction(new ActionKeyListener(key), key,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	/**
	 * Registers a listener for a F1, ESC, or somthing global keypressed Event
	 * 
	 * @param to_listen_Key
	 *            Keyboard Key
	 * @param runnable
	 *            Method to call
	 */
	public void registerActionKeyListener(KeyStroke to_listen_Key,
			Runnable runnable) {
		if (listen_key_events == null)
			listen_key_events = new HashMap<KeyStroke, Vector<Runnable>>();

		Vector<Runnable> listeners = listen_key_events.get(to_listen_Key);

		if (listeners == null) {
			listeners = new Vector<Runnable>();
			listen_key_events.put(to_listen_Key, listeners);

			registerActionKeyListenerOnRootPane(to_listen_Key);
		}

		listeners.add(runnable);
	}

	/**
	 * Kontrolliert, ob in der übergebenen Tabelle nur ein Eintrag selektiert
	 * wurde. Wurde mehr als ein Eintrag selektiert, bekommt der User eine
	 * entsprechende Fehlermeldeung aufgeschalten und der Rückgabewert der
	 * Funktion ist false.
	 * 
	 * @param table
	 *            eine jTable
	 * @return <b>true</b> Wenn nur ein Eintrag selektiert wurde und
	 *         <b>false</b>, wenn kein, oder mehrere Einträge selektiert wurden.
	 *         Eine ensprechende Fehlermeldung ist dabei dem User schon
	 *         aufgeschalten worden.
	 */
	public boolean checkAnyAndSingleSelection(JTable table) {

		if (table.getRowCount() == 1) {
			table.selectAll();
			return true;
		}

		if (table.getSelectedRowCount() <= 0) {
			JOptionPane.showMessageDialog(null, StringUtils
					.autoLineBreak(MlM("Bitte wählen Sie einen Eintrag aus.")),
					MlM("Fehler"), JOptionPane.OK_OPTION);
			return false;
		}

		if (table.getSelectedRowCount() > 1) {
			JOptionPane.showMessageDialog(null,
					MlM("Bitte nur einen Eintrag auswählen."), MlM("Fehler"),
					JOptionPane.OK_OPTION);
			return false;
		}

		return true;
	}

	/**
	 * in jTextField an einen StringBuffer anbinden
	 * 
	 * @param jtext
	 *            das Textfeld
	 * @param var
	 *            der StringBuffer
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
        @Override
	public void bindVar(JTextField jtext, StringBuffer var) {

		checkBindVars();

		bind_vars.bindVar(jtext, var);
	}

	/**
	 * in jTextField an einen StringBuffer anbinden
	 * 
	 * @param jtext
	 *            das Textfeld
	 * @param var
	 *            der StringBuffer
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
	public void bindVar(JPasswordField jtext, StringBuffer var) {

		checkBindVars();

		bind_vars.bindVar(jtext, var);
	}

    /**
     * in jTextArea an eine StringBuffer anbinden
     *
     * @param jtext das Textfeld
     * @param var der StringBuffer
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird dann der
     * demenstprechende Inhalt entweder vom GUI zu Variablen, oder umgekehrt
     * übertragen.
     */
    @Override
    public void bindVar(JTextArea jtext, StringBuffer var) {
        checkBindVars();
        bind_vars.bindVar(jtext, var);
    }

    /**
     * in jTextArea an eine DBValue anbinden
     *
     * @param jtext das Textfeld
     * @param var der DBValue
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird dann der
     * demenstprechende Inhalt entweder vom GUI zu Variablen, oder umgekehrt
     * übertragen.
     */
    @Override
    public void bindVar(JTextArea jtext, DBValue var) {
        checkBindVars();
        bind_vars.bindVar(jtext, var);
    }  
        
	/**
	 * Ein jTextField an eine DBValue anbinden
	 * 
	 * @param jtext
	 *            das Textfeld
	 * @param var
	 *            die Datenbankvariable
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
    @Override
	public void bindVar(JTextField jtext, DBValue var) {

		checkBindVars();

		bind_vars.bindVar(jtext, var);
	}

	/**
	 * Eine {@link JComboBox} an eine {@link DBValue} anbinden
	 * 
	 * @param jtext
	 *            das Textfeld
	 * @param var
	 *            die Datenbankvariable
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
    @Override
	public void bindVar(JComboBox jcombo, DBValue var) {

		checkBindVars();

		bind_vars.bindVar(jcombo, var);
	}

	/**
	 * Eine {@link IDateTimeComponent} an eine {@link DBDateTime} Variable
	 * anbinden
	 * 
	 * @param comp
	 *            die DateTime Komponente
	 * @param dateTime
	 *            die Datenbankvariable
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
	public void bindVar(IDateTimeComponent comp, DBDateTime dateTime) {

		checkBindVars();

		bind_vars.bindVar(comp, dateTime);
	}

	/**
	 * Eine JCheckBox an eine DBFlagInteger Variable anbinden
	 * 
	 * @param jtext
	 *            die Textbox
	 * @param var
	 *            die Datebanvariable
	 * 
	 *            Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
	 *            dann der demenstprechende Inhalt entweder vom GUI zu
	 *            Variablen, oder umgekehrt übertragen.
	 */
	@Override
	public void bindVar(JCheckBox jtext, DBFlagInteger var) {

		checkBindVars();

		bind_vars.bindVar(jtext, var);
	}

	/**
	 * Alle Werte der angebunden Variablen in die entsprechenden GUI Komponenten
	 * übertragen
	 */
	@Override
	public void var_to_gui() {

		if (bind_vars == null)
			return;

		bind_vars.var_to_gui();
	}

	/**
	 * Alle Elemnte des GUIs in die angebundenen Datenbankfelder kopieren
	 */
	@Override
	public void gui_to_var() {

		if (bind_vars == null)
			return;

		bind_vars.gui_to_var();
	}

	public void setBindVarsChanged(boolean state) {

		if (bind_vars == null)
			return;

		for (Pair pair : bind_vars.getBindVarPairs()) {
			if (pair.get_first() instanceof NoticeIfChangedTextField) {
				NoticeIfChangedTextField text_field = (NoticeIfChangedTextField) pair
						.get_first();

				text_field.setChanged(state);
			}
		}
	}

	public void setBindVars(BindVarInterface bind_vars) {
		this.bind_vars = bind_vars;
	}

    @Override
	public Collection<Pair> getBindVarPairs() {

		checkBindVars();

		return bind_vars.getBindVarPairs();
	}

    @Override
	public void addBindVarPair(Pair pair) {
		checkBindVars();

		bind_vars.addBindVarPair(pair);
	}

	protected void checkBindVars() {
		if (bind_vars == null)
			bind_vars = new BindVarBase();
	}

	/**
	 * language the dialog is programmed in if not set, the settings from
	 * Root.getBaseLangague() are used
	 */
	public void setBaseLanguage(String language) {
		base_language = language;
	}

	/**
	 * @return language the dialog is programmed in if not set, the settings
	 *         from Root.getBaseLangague() are used
	 */
	public String getBaseLanguage() {
		if (base_language == null)
			return root.getBaseLanguage();

		return base_language;
	}

	public void autoSwitchToCurrentLocale() {
		translation_helper.autoSwitchToCurrentLocale();
	}

	public void doLayout() {
		if (autoswitch_trans_first_run) {
			autoSwitchToCurrentLocale();
			autoswitch_trans_first_run = false;
		}
	}

	/**
	 * @param message
	 *            native langauge message
	 * @return translated message, if available
	 */
	public String MlM(String message) {
		return translation_helper.MlM(message);
	}

	public Timer getAutoRefreshTimer() {
		if (autoRefreshTimer == null) {
			autoRefreshTimer = new Timer();
		}

		if (autoRefreshTask == null) {
			autoRefreshTask = new TimerTask() {

				@Override
				public void run() {

					parent.doAutoRefresh();
				}
			};
		}

		return autoRefreshTimer;
	}

	public void cancelAutoRefreshTimer() {

		if (autoRefreshTask != null) {
			autoRefreshTask.cancel();
			autoRefreshTask = null;
		}

		if (autoRefreshTimer != null) {
			autoRefreshTimer.cancel();
			autoRefreshTimer = null;
		}
	}
}
