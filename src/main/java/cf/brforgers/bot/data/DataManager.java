/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/10/16 15:54]
 */

package cf.brforgers.bot.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataManager {
	public static Path getPath(String file, String ext) {
		try {
			return Paths.get(file + "." + ext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
