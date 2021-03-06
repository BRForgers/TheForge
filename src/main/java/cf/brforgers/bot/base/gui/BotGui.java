/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.brforgers.bot.base.gui;

import cf.brforgers.bot.Bot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class BotGui extends JComponent {
	private static final Logger LOGGER = LogManager.getLogger("BotGUI");
	private static final Font GUI_FONT = new Font("Monospaced", 0, 12);
	public JFrame frame;
	private Consumer<String> out;

	private BotGui() {
		this.setPreferredSize(new Dimension(858, 480));
		this.setLayout(new BorderLayout());

		try {
			this.add(this.getLogComponent(), "Center");
			this.add(this.getStatsComponent(), "West");
		} catch (Exception exception) {
			LOGGER.error("Couldn't build bot GUI", exception);
		}
	}

	/**
	 * Creates the bot GUI and sets it visible for the user.
	 */
	public static BotGui createBotGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		BotGui ui = new BotGui();
		JFrame frame = new JFrame("Bot - GUI");
		Bot.onLoaded.add(() -> {
			frame.setTitle(Bot.SELF.getUsername() + " - GUI");
			frame.repaint();
			frame.revalidate();
		});
		ui.frame = frame;
		frame.add(ui);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return ui;
	}

	/**
	 * Generates new StatsComponent and returns it.
	 */
	private JComponent getStatsComponent() throws Exception {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new StatsComponent(), "North");
		panel.add(this.getUsersListComponent(), "Center");
		TitledBorder b = new TitledBorder(new EtchedBorder(), "Stats");
		panel.setBorder(b);
		return panel;
	}

	/**
	 * Generates new UsersListComponent and returns it.
	 */
	private JComponent getUsersListComponent() throws Exception {
		JList list = new UsersListComponent();
		JScrollPane pane = new JScrollPane(list, 22, 30);
		TitledBorder b = new TitledBorder(new EtchedBorder(), "Users");
		pane.setBorder(b);
		Bot.onLoaded.add(() -> Bot.API.addEventListener(list));
		return pane;
	}

	private JComponent getLogComponent() throws Exception {
		JPanel panel = new JPanel(new BorderLayout());
		final JTextArea textArea = new JTextArea();
		final JScrollPane pane = new JScrollPane(textArea, 22, 30);
		textArea.setEditable(false);
		textArea.setFont(GUI_FONT);
		final JTextField textField = new JTextField();
		textField.setEditable(false);
		Bot.onLoaded.add(() -> textField.setEditable(true));

		out = ConsoleHandler.wrap(in -> appendLine(textArea, pane, in + "\r\n"));

		textField.addActionListener(actionPerformed -> {
			String s = textField.getText().trim();

			if (!s.isEmpty()) {
				out.accept("<Input> " + s);
				ConsoleHandler.handle(s, out);
			}

			textField.setText("");
		});
		textArea.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent focusEvent) {
			}
		});
		panel.add(pane, "Center");
		panel.add(textField, "South");
		TitledBorder b = new TitledBorder(new EtchedBorder(), "Log and chat");
		panel.setBorder(b);
		panel.getBorder();
		Thread thread = new Thread(() -> {
			String s;

			while ((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null)
				appendLine(textArea, pane, s);
		});
		thread.setDaemon(true);
		thread.start();
		return panel;
	}

	public void appendLine(final JTextArea textArea, final JScrollPane scrollPane, final String line) {

		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> this.appendLine(textArea, scrollPane, line));
		} else {
			Document document = textArea.getDocument();
			JScrollBar jscrollbar = scrollPane.getVerticalScrollBar();
			boolean flag = false;

			if (scrollPane.getViewport().getView() == textArea) {
				flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (GUI_FONT.getSize() * 4) > (double) jscrollbar.getMaximum();
			}

			try {
				document.insertString(document.getLength(), line, null);
			} catch (BadLocationException ignored) {
			}

			if (flag) {
				jscrollbar.setValue(Integer.MAX_VALUE);
			}
		}
	}
}