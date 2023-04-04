package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildAsyncTask;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class CreateCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.create.name}",
            description = "${user.create.description}",
            aliases = "${user.create.aliases}",
            permission = "funnyguilds.create",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when(!this.config.guildsEnabled, config -> config.admin.commands.guild.status.disabled);
        when(LocationUtils.checkWorld(player), config -> config.guild.commands.create.blockedWorld);
        when(user.hasGuild(), config -> config.commands.validation.hasGuild);

        when(args.length == 0, config -> config.commands.validation.noTagGiven);
        when(args.length == 1, config -> config.commands.validation.noNameGiven);

        String tag = args[0];
        if (!this.config.guildTagKeepCase) {
            tag = this.config.guildTagUppercase ? tag.toUpperCase(Locale.ROOT) : tag.toLowerCase(Locale.ROOT);
        }

        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();
        World world = player.getWorld();

        when(tag.length() < this.config.createTagMinLength,
                config -> config.guild.commands.create.tagMinLength, FunnyFormatter.of("{LENGTH}", this.config.createTagMinLength));
        when(tag.length() > this.config.createTagLength,
                config -> config.guild.commands.create.tagMaxLength, FunnyFormatter.of("{LENGTH}", this.config.createTagLength));
        when(name.length() < this.config.createNameMinLength,
                config -> config.guild.commands.create.nameMinLength, FunnyFormatter.of("{LENGTH}", this.config.createNameMinLength));
        when(name.length() > this.config.createNameLength,
                config -> config.guild.commands.create.nameMaxLength, FunnyFormatter.of("{LENGTH}", this.config.createNameLength));

        when(!this.config.tagRegex.matches(tag), config -> config.guild.commands.create.invalidTag);
        when(!this.config.nameRegex.matches(name), config -> config.guild.commands.create.invalidName);

        when(this.guildManager.tagExists(tag), config -> config.commands.validation.guildWithTagExist);
        when(this.guildManager.nameExists(name), config -> config.commands.validation.guildWithNameExists);

        if (this.config.checkForRestrictedGuildNames) {
            when(!GuildUtils.validateTag(this.config, tag), config -> config.guild.commands.create.restrictedGuildTag);
            when(!GuildUtils.validateName(this.config, name), config -> config.guild.commands.create.restrictedGuildName);
        }

        HeartConfiguration heartConfig = this.config.heart;

        if (this.config.regionsEnabled) {
            heartConfig.center.prepareCenterLocation(guildLocation);

            if (heartConfig.createEntityType != null && guildLocation.getBlockY() < (world.getMaxHeight() - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }

            int distance = this.config.regionSize + this.config.createDistance;
            if (this.config.enlargeItems != null) {
                distance += this.config.enlargeItems.size() * this.config.enlargeSize;
            }

            when(
                    distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation),
                    config -> config.guild.commands.create.nearSpawn, FunnyFormatter.of("{DISTANCE}", distance)
            );

            when(
                    this.regionManager.isInRegion(guildLocation) || this.regionManager.isNearRegion(guildLocation),
                    config -> config.guild.commands.create.nearOtherGuild
            );
        }

        if (this.config.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? this.config.rankCreateVip : this.config.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{REQUIRED-FORMAT}", NumberRange.inRangeToString(requiredRank, this.config.pointsFormat))
                        .register("{POINTS}", requiredRank)
                        .register("{REQUIRED}", requiredRank)
                        .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                        .register("{POINTS}", points);

                this.messageService.getMessage(config -> config.guild.commands.create.missingRankingPoints)
                        .receiver(player)
                        .with(formatter)
                        .send();
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items")
                ? this.config.createItemsVip
                : this.config.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items")
                ? this.config.requiredExperienceVip
                : this.config.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items")
                ? this.config.requiredMoneyVip
                : this.config.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            this.messageService.getMessage(config -> config.guild.commands.create.missingExperience)
                    .receiver(player)
                    .with("{EXP}", requiredExperience)
                    .send();
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            this.messageService.getMessage(config -> config.guild.commands.create.missingMoney)
                    .receiver(player)
                    .with("{MONEY}", requiredMoney)
                    .send();
            return;
        }

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.guild.commands.create.missingItems)) {
            return;
        }

        if (HookManager.WORLD_GUARD.isPresent() && HookManager.WORLD_GUARD.get().isInNonGuildsRegion(guildLocation)) {
            this.messageService.getMessage(config -> config.guild.commands.create.invalidLocation)
                    .receiver(player)
                    .send();
            return;
        }

        Guild guild = new Guild(name, tag);
        guild.setOwner(user);
        guild.setLives(this.config.warLives);
        guild.setValidity(Instant.now().plus(this.config.validityStart));
        guild.setProtection(Instant.now().plus(this.config.warProtection));
        guild.setPvP(this.config.damageGuild);

        Location home = guildLocation.clone()
                .add(0.5D, -2.0D, 0.5D)
                .add(heartConfig.homeOffset);
        heartConfig.homeHeadPosition.setHeadPosition(home);
        guild.setHome(home);

        if (this.config.regionsEnabled) {
            Region region = new Region(guild, guildLocation, this.config.regionSize);
            guild.setRegion(region);

            WorldBorder border = world.getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - this.config.createMinDistanceFromBorder,
                    world.getMaxHeight(), radius - this.config.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                this.messageService.getMessage(config -> config.guild.commands.create.nearBorder)
                        .receiver(player)
                        .with("{BORDER-MIN-DISTANCE}", this.config.createMinDistanceFromBorder)
                        .send();
                return;
            }
        }

        if (!SimpleEventHandler.handle(new GuildPreCreateEvent(EventCause.USER, user, guild))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        player.setTotalExperience(player.getTotalExperience() - requiredExperience);

        if (VaultHook.isEconomyHooked()) {
            EconomyResponse withdrawResult = VaultHook.withdrawFromPlayerBank(player, requiredMoney);

            if (!withdrawResult.transactionSuccess()) {
                this.messageService.getMessage(config -> config.guild.commands.create.withdrawError)
                        .receiver(player)
                        .with("{ERROR}", withdrawResult.errorMessage)
                        .send();
                return;
            }
        }

        if (this.config.regionsEnabled) {
            if (heartConfig.pasteSchematicOnCreation) {
                HookManager.WORLD_EDIT.peek(worldEdit -> {
                    if (!worldEdit.pasteSchematic(heartConfig.guildSchematicFile, guildLocation, heartConfig.pasteSchematicWithAir)) {
                        this.messageService.getMessage(config -> config.guild.commands.create.couldNotPasteSchematic)
                                .receiver(player)
                                .send();
                    }
                });
            }
            else if (heartConfig.createCenterSphere) {
                for (Location locationInSphere : SpaceUtils.sphere(guildLocation, 4, 4, false, true, 0)) {
                    if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                        locationInSphere.getBlock().setType(Material.AIR);
                    }
                }

                for (Location locationInSphere : SpaceUtils.sphere(guildLocation, 4, 4, true, true, 0)) {
                    if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                        locationInSphere.getBlock().setType(Material.OBSIDIAN);
                    }
                }

                if (this.config.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }

            this.guildManager.spawnHeart(this.plugin.getGuildEntityHelper(), guild);
            if (this.config.heart.teleportToHeartOnCreate) {
                guild.teleportHome(player);
            }
        }

        this.guildManager.addGuild(guild);
        user.setGuild(guild);

        guild.getRegion().peek(region -> this.regionManager.addRegion(region));

        this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildAsyncTask(this.plugin.getDataModel(), guild));
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                .peek(this.plugin::scheduleFunnyTasks);

        SimpleEventHandler.handle(new GuildCreateEvent(EventCause.USER, user, guild));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        this.messageService.getMessage(config -> config.guild.commands.create.created)
                .receiver(player)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.guild.commands.create.createdBroadcast)
                .broadcast()
                .with(formatter)
                .send();

        if (!this.config.giveRewardsForFirstGuild || this.guildManager.countGuilds() > 1) {
            return;
        }

        this.config.firstGuildRewards.forEach(item -> {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                return;
            }

            player.getInventory().addItem(item);
        });
    }

}
