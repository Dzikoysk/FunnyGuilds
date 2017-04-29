/*
 *     FunnyGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

public enum Permission {
	NOVAGUILDS_ADMIN_ACCESS,
	NOVAGUILDS_ADMIN_CONFIG_ACCESS,
	NOVAGUILDS_ADMIN_CONFIG_GET,
	NOVAGUILDS_ADMIN_CONFIG_RELOAD,
	NOVAGUILDS_ADMIN_CONFIG_RESET,
	NOVAGUILDS_ADMIN_CONFIG_SAVE,
	NOVAGUILDS_ADMIN_CONFIG_SET,
	NOVAGUILDS_ADMIN_ERROR_ACCESS,
	NOVAGUILDS_ADMIN_ERROR_LIST,
	NOVAGUILDS_ADMIN_PLAYER_ACCESS,
	NOVAGUILDS_ADMIN_PLAYER_SET_POINTS,
	NOVAGUILDS_ADMIN_GUILD_ACCESS,
	NOVAGUILDS_ADMIN_GUILD_ABANDON,
	NOVAGUILDS_ADMIN_GUILD_BANK_PAY,
	NOVAGUILDS_ADMIN_GUILD_BANK_WITHDRAW,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_UPDATE,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_CLEAN,
	NOVAGUILDS_ADMIN_GUILD_INACTIVE_LIST,
	NOVAGUILDS_ADMIN_GUILD_INVITE,
	NOVAGUILDS_ADMIN_GUILD_KICK,
	NOVAGUILDS_ADMIN_GUILD_LIST,
	NOVAGUILDS_ADMIN_GUILD_SET_LEADER,
	NOVAGUILDS_ADMIN_GUILD_SET_LIVEREGENERATIONTIME,
	NOVAGUILDS_ADMIN_GUILD_SET_LIVES,
	NOVAGUILDS_ADMIN_GUILD_SET_NAME,
	NOVAGUILDS_ADMIN_GUILD_SET_POINTS,
	NOVAGUILDS_ADMIN_GUILD_SET_TAG,
	NOVAGUILDS_ADMIN_GUILD_SET_TIMEREST,
	NOVAGUILDS_ADMIN_GUILD_SET_SLOTS,
	NOVAGUILDS_ADMIN_GUILD_PURGE,
	NOVAGUILDS_ADMIN_GUILD_TELEPORT_SELF,
	NOVAGUILDS_ADMIN_GUILD_TELEPORT_OTHER,
	NOVAGUILDS_ADMIN_GUILD_FULLINFO,
	NOVAGUILDS_ADMIN_GUILD_RESET_POINTS,
	NOVAGUILDS_ADMIN_GUILD_RANK_ACCESS,
	NOVAGUILDS_ADMIN_GUILD_RANK_LIST,
	NOVAGUILDS_ADMIN_GUILD_RANK_EDIT,
	NOVAGUILDS_ADMIN_GUILD_RANK_DELETE,
	NOVAGUILDS_ADMIN_GUILD_RANK_SET,
	NOVAGUILDS_ADMIN_HOLOGRAM_ACCESS,
	NOVAGUILDS_ADMIN_HOLOGRAM_LIST,
	NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT,
	NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT_HERE,
	NOVAGUILDS_ADMIN_HOLOGRAM_DELETE,
	NOVAGUILDS_ADMIN_HOLOGRAM_ADD,
	NOVAGUILDS_ADMIN_HOLOGRAM_ADDTOP,
	NOVAGUILDS_ADMIN_REGION_ACCESS,
	NOVAGUILDS_ADMIN_REGION_BYPASS_SELF,
	NOVAGUILDS_ADMIN_REGION_BYPASS_OTHER,
	NOVAGUILDS_ADMIN_REGION_CHANGE_SPECTATE_SELF,
	NOVAGUILDS_ADMIN_REGION_CHANGE_SPECTATE_OTHER,
	NOVAGUILDS_ADMIN_REGION_DELETE,
	NOVAGUILDS_ADMIN_REGION_LIST,
	NOVAGUILDS_ADMIN_REGION_TELEPORT_SELF,
	NOVAGUILDS_ADMIN_REGION_BUY,
	NOVAGUILDS_ADMIN_REGION_TELEPORT_OTHER,
	NOVAGUILDS_ADMIN_REGION_SPECTATE,
	NOVAGUILDS_ADMIN_RELOAD,
	NOVAGUILDS_ADMIN_SAVE,
	NOVAGUILDS_ADMIN_SAVE_NOTIFY,
	NOVAGUILDS_ADMIN_UPDATEAVAILABLE,
	NOVAGUILDS_ADMIN_CHATSPY_SELF,
	NOVAGUILDS_ADMIN_CHATSPY_OTHER,
	NOVAGUILDS_ADMIN_MIGRATE,
	NOVAGUILDS_ADMIN_NOCONFIRM,

	NOVAGUILDS_GUILD_ACCESS,
	NOVAGUILDS_GUILD_ABANDON,
	NOVAGUILDS_GUILD_LEAVE,
	NOVAGUILDS_GUILD_ALLY,
	NOVAGUILDS_GUILD_BANK_PAY,
	NOVAGUILDS_GUILD_BANK_WITHDRAW,
	NOVAGUILDS_GUILD_COMPASS,
	NOVAGUILDS_GUILD_CREATE,
	NOVAGUILDS_GUILD_EFFECT,
	NOVAGUILDS_GUILD_HOME,
	NOVAGUILDS_GUILD_HOME_SET,
	NOVAGUILDS_GUILD_INVITE,
	NOVAGUILDS_GUILD_JOIN,
	NOVAGUILDS_GUILD_KICK,
	NOVAGUILDS_GUILD_PVPTOGGLE,
	NOVAGUILDS_GUILD_REQUIREDITEMS,
	NOVAGUILDS_GUILD_TOP,
	NOVAGUILDS_GUILD_VAULT_RESTORE,
	NOVAGUILDS_GUILD_WAR,
	NOVAGUILDS_GUILD_BUYLIFE,
	NOVAGUILDS_GUILD_BUYSLOT,
	NOVAGUILDS_GUILD_CHATMODE,
	NOVAGUILDS_GUILD_INFO,
	NOVAGUILDS_GUILD_LEADER,
	NOVAGUILDS_GUILD_MENU,
	NOVAGUILDS_GUILD_BOSS,
	NOVAGUILDS_GUILD_OPENINVITATION,
	NOVAGUILDS_GUILD_SET_NAME,
	NOVAGUILDS_GUILD_SET_TAG,
	NOVAGUILDS_GUILD_RANK_ACCESS,
	NOVAGUILDS_GUILD_RANK_LIST,
	NOVAGUILDS_GUILD_RANK_EDIT,
	NOVAGUILDS_GUILD_RANK_DELETE,
	NOVAGUILDS_GUILD_RANK_SET,

	NOVAGUILDS_REGION_ACCESS,
	NOVAGUILDS_REGION_CREATE,
	NOVAGUILDS_REGION_RESIZE,
	NOVAGUILDS_REGION_DELETE,
	NOVAGUILDS_REGION_LIST,

	NOVAGUILDS_CHAT_NOTAG,
	NOVAGUILDS_PLAYERINFO,
	NOVAGUILDS_TOOL_CHECK,
	NOVAGUILDS_TOOL_GET,
	NOVAGUILDS_ERROR,
	NOVAGUILDS_NOVAGUILDS,
	NOVAGUILDS_CONFIRM;

	/**
	 * Checks if a player has required permission
	 *
	 * @param sender command sender (the player)
	 * @return boolean
	 */
	public boolean has(CommandSender sender) {
		return sender.hasPermission(getPath()) || sender.isOp();
	}

	/**
	 * Checks if a player has required permission
	 *
	 * @param nPlayer the player
	 * @return boolean
	 */
	public boolean has(NovaPlayer nPlayer) {
		return has(nPlayer.getPlayer());
	}

	/**
	 * Gets the path
	 *
	 * @return the path string
	 */
	public String getPath() {
		return StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	/**
	 * Gets permission from string
	 *
	 * @param path path
	 * @return the permission
	 */
	public static Permission fromPath(String path) {
		try {
			return Permission.valueOf(StringUtils.replace(path, ".", "_").toUpperCase());
		}
		catch(Exception e) {
			return null;
		}
	}
}
