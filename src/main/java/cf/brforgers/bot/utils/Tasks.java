/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:13]
 */

package cf.brforgers.bot.utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tasks {
	public static double cpuUsage = 0;

	public static void startAsyncTask(Runnable scheduled, int everySeconds) {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(scheduled, 0, everySeconds, TimeUnit.SECONDS);
	}

	public static void startAsyncTasks() {
		final OperatingSystemMXBean os = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
		startAsyncTask(() -> cpuUsage = (Math.floor(os.getProcessCpuLoad() * 10000) / 100), 2);
	}
}
