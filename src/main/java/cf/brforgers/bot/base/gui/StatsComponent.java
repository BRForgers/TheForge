/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.brforgers.bot.base.gui;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.utils.Statistics;
import cf.brforgers.bot.utils.Tasks;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import static cf.brforgers.bot.utils.Statistics.*;

public class StatsComponent extends JComponent {
	private static final int mb = 1024 * 1024;
	private final int[] graphicValues = new int[229];
	private final String[] msgs = new String[11];
	private int lastValue = 0;

	public StatsComponent() {
		this.setPreferredSize(new Dimension(456, 246));
		this.setMinimumSize(new Dimension(456, 246));
		this.setMaximumSize(new Dimension(456, 246));
		new Timer(1000, actionPerformed -> tick()).start();
		this.setBackground(Color.BLACK);
		Bot.onLoaded.add(this::tick);
	}

	private void addToArray(int value) {
		System.arraycopy(graphicValues, 1, graphicValues, 0, graphicValues.length - 1);
		graphicValues[graphicValues.length - 1] = value;
	}

	private void tick() {
		Runtime instance = Runtime.getRuntime();
		System.gc();
		this.msgs[0] = "Uptime: " + calculate(startDate, new Date());
		this.msgs[1] = Statistics.msgs + " msgs; " + cmds + " cmds; " + crashes + " crashes; " + toofasts + " spam.";
		this.msgs[2] = Thread.activeCount() + " active threads.";
		this.msgs[4] = "RAM(Using/Total/Max): " + ((instance.totalMemory() - instance.freeMemory()) / mb) + " MB/" + (instance.totalMemory() / mb) + " MB/" + (instance.maxMemory() / mb) + " MB";
		this.msgs[5] = "CPU Usage: " + Tasks.cpuUsage + "%";
		addToArray(Statistics.msgs - lastValue);
		lastValue = Statistics.msgs;
		this.repaint();
	}

	public void paint(Graphics g) {
		g.setColor(new Color(16777215));
		g.fillRect(0, 0, 456, 246);

		for (int i = 0; i < graphicValues.length; ++i) {
			int eachValue = graphicValues[i] * 5;
			g.setColor(new Color(eachValue + 28 << 16));
			g.fillRect(i * 2, 1, 2, eachValue);
		}

		g.setColor(Color.BLACK);

		for (int i = 0; i < this.msgs.length; ++i)
			if (this.msgs[i] != null) g.drawString(this.msgs[i], 32, 116 + i * 16);
	}
}