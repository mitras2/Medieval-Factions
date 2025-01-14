package dansplugins.factionsystem.eventhandlers;

import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.utils.Messenger;
import dansplugins.factionsystem.data.EphemeralData;
import dansplugins.factionsystem.data.PersistentData;
import dansplugins.factionsystem.events.FactionJoinEvent;
import dansplugins.factionsystem.objects.domain.ActivityRecord;
import dansplugins.factionsystem.objects.domain.Faction;
import dansplugins.factionsystem.objects.domain.PowerRecord;
import dansplugins.factionsystem.services.LocalActionBarService;
import dansplugins.factionsystem.services.LocalChunkService;
import dansplugins.factionsystem.services.LocalLocaleService;
import dansplugins.factionsystem.utils.Logger;
import dansplugins.factionsystem.utils.TerritoryOwnerNotifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.ZonedDateTime;
import java.util.UUID;

public class JoiningLeavingAndSpawningHandler implements Listener {

	@EventHandler()
    public void handle(PlayerJoinEvent event) {
		Player player = event.getPlayer();

        if (!hasPowerRecord(event.getPlayer().getUniqueId())) {

        	// assign power record
            PowerRecord newRecord = new PowerRecord(player.getUniqueId(), MedievalFactions.getInstance().getConfig().getInt("initialPowerLevel"));
            PersistentData.getInstance().getPlayerPowerRecords().add(newRecord);

            // since player has not logged in before, this is where we will handle random assignment
			if (MedievalFactions.getInstance().getConfig().getBoolean("randomFactionAssignment")) {

				Faction faction = PersistentData.getInstance().getRandomFaction();
				if (faction != null) {
					FactionJoinEvent joinEvent = new FactionJoinEvent(faction, player);
					Bukkit.getPluginManager().callEvent(joinEvent);
					if (joinEvent.isCancelled()) {
						// TODO Locale Message
						return;
					}
					Messenger.getInstance().sendAllPlayersInFactionMessage(faction, String.format(ChatColor.GREEN + "" + LocalLocaleService.getInstance().getText("HasJoined"), player.getName(), faction.getName()));
					faction.addMember(player.getUniqueId());
					player.sendMessage(ChatColor.GREEN + "" + LocalLocaleService.getInstance().getText("AssignedToRandomFaction"));

					Logger.getInstance().log(player.getName() + " has been randomly assigned to " + faction.getName() + "!");
				}
				else {
					// there are no factions to assign this player to
					Logger.getInstance().log("Attempted to assign " + player.getName() + " to a random faction, but no factions are existent.");
				}
			}
        }
        if (!hasActivityRecord(player.getUniqueId())) {
        	ActivityRecord newRecord = new ActivityRecord(player.getUniqueId(), 1);
        	PersistentData.getInstance().getPlayerActivityRecords().add(newRecord);
        }
        else {
        	ActivityRecord record = PersistentData.getInstance().getPlayerActivityRecord(player.getUniqueId());
        	if (record != null) {
        		PowerRecord power = PersistentData.getInstance().getPlayersPowerRecord(player.getUniqueId());
        		record.incrementLogins();

        		int newPower = power.getPowerLevel();
        		if (newPower < 0)
        			newPower = 0;
        		
        		if (record.getLastLogout() != null) {
        			if (record.getMinutesSinceLastLogout() > 1) {
						player.sendMessage(ChatColor.GREEN + String.format(LocalLocaleService.getInstance().getText("WelcomeBackLastLogout"), event.getPlayer().getName(), record.getTimeSinceLastLogout()));
        			}
        		}
        		if (record.getPowerLost() > 0) {
					player.sendMessage(ChatColor.RED + String.format(LocalLocaleService.getInstance().getText("PowerHasDecayed"), record.getPowerLost(), newPower));
        		}
        		record.setPowerLost(0);
        	}
        }

        setPlayerActionBarTerritoryInfo(event.getPlayer());

        LocalChunkService.getInstance().informPlayerIfTheirLandIsInDanger(player, PersistentData.getInstance().getFactions(), PersistentData.getInstance().getClaimedChunks());

        informPlayerIfTheirFactionIsWeakened(player);
    }

    private void setPlayerActionBarTerritoryInfo(Player player) {
		if(MedievalFactions.getInstance().getConfig().getBoolean("territoryIndicatorActionbar")) {
			// if chunk is claimed
			if (LocalChunkService.getInstance().isClaimed(player.getLocation().getChunk(), PersistentData.getInstance().getClaimedChunks())) {
				String factionName = LocalChunkService.getInstance().getClaimedChunk(player.getLocation().getChunk()).getHolder();
				Faction holder = PersistentData.getInstance().getFaction(factionName);
				TerritoryOwnerNotifier.getInstance().sendPlayerTerritoryAlert(player, holder);
				return;
			}

			// Otherwise the chunk ist unclaimed
			TerritoryOwnerNotifier.getInstance().sendPlayerTerritoryAlert(player, null);
		}
	}

    private void informPlayerIfTheirFactionIsWeakened(Player player) {
		Faction playersFaction = PersistentData.getInstance().getPlayersFaction(player.getUniqueId());

		if (playersFaction == null) {
			return;
		}

		if (playersFaction.isLiege()) {
			if (playersFaction.isWeakened()) {
				player.sendMessage(ChatColor.RED + LocalLocaleService.getInstance().getText("AlertFactionIsWeakened"));
			}
		}
	}

	private boolean hasPowerRecord(UUID playerUUID) {
		for (PowerRecord record : PersistentData.getInstance().getPlayerPowerRecords()){
			if (record.getPlayerUUID().equals(playerUUID)){
				return true;
			}
		}
		return false;
	}

	private boolean hasActivityRecord(UUID playerUUID) {
		for (ActivityRecord record : PersistentData.getInstance().getPlayerActivityRecords()){
			if (record.getPlayerUUID().equals(playerUUID)){
				return true;
			}
		}
		return false;
	}

	@EventHandler()
	public void handle(PlayerQuitEvent event)
	{
		if (EphemeralData.getInstance().getLockingPlayers().contains(event.getPlayer().getUniqueId()))
		{
			EphemeralData.getInstance().getLockingPlayers().remove(event.getPlayer().getUniqueId());
		}
		if (EphemeralData.getInstance().getUnlockingPlayers().contains(event.getPlayer().getUniqueId()))
		{
			EphemeralData.getInstance().getUnlockingPlayers().remove(event.getPlayer().getUniqueId());
		}
		if (EphemeralData.getInstance().getPlayersGrantingAccess().containsKey(event.getPlayer().getUniqueId()))
		{
			EphemeralData.getInstance().getPlayersGrantingAccess().remove(event.getPlayer().getUniqueId());
		}
		if (EphemeralData.getInstance().getPlayersCheckingAccess().contains(event.getPlayer().getUniqueId()))
		{
			EphemeralData.getInstance().getPlayersCheckingAccess().remove(event.getPlayer().getUniqueId());
		}
		if (EphemeralData.getInstance().getPlayersRevokingAccess().containsKey(event.getPlayer().getUniqueId()))
		{
			EphemeralData.getInstance().getPlayersRevokingAccess().remove(event.getPlayer().getUniqueId());
		}

		ActivityRecord record = PersistentData.getInstance().getPlayerActivityRecord(event.getPlayer().getUniqueId());
		if (record != null)
		{
			record.setLastLogout(ZonedDateTime.now());
		}

		LocalActionBarService.getInstance(MedievalFactions.getInstance()).clearPlayerActionBar(event.getPlayer());
	}

	@EventHandler()
	public void handle(EntitySpawnEvent event) {

		int x = 0;
		int z = 0;

		x = event.getEntity().getLocation().getChunk().getX();
		z = event.getEntity().getLocation().getChunk().getZ();

		// check if land is claimed
		if (LocalChunkService.getInstance().isClaimed(event.getLocation().getChunk(), PersistentData.getInstance().getClaimedChunks()))
		{
			if (event.getEntity() instanceof Monster && !MedievalFactions.getInstance().getConfig().getBoolean("mobsSpawnInFactionTerritory")) {
				event.setCancelled(true);
			}
		}
	}

}
