package net.comorevi.nukkitplugin;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.Command;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;

import java.util.HashMap;

public class SymmetryEdit extends PluginBase implements Listener {
    private HashMap<String, Object[]> keyMap = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            getServer().getLogger().info("SymmetryEditのコマンドはゲーム内からのみ使用できます。");
            return true;
        }
        switch (command.getName()) {
            case "symmetry":
                Entity e = (Entity) sender;
                Object[] array = {e.getDirection(), e.getLocation()};
                if (args.length == 1) {
                    if (args[0].equals("reset")) {
                        keyMap.put(sender.getName(), array);
                        sender.sendMessage("システム>>SymmetryEdit>>中心線を再設定しました。");
                    } else {
                        sender.sendMessage("システム>>SymmetryEdit>>コマンド一覧"+"\n"+"/symmetry: 有効・無効化"+"\n"+"/symmetry reset: 中心線を再設定");
                    }
                } else {
                    if (!keyMap.containsKey(sender.getName())) {
                        keyMap.put(sender.getName(), array);
                        sender.sendMessage("システム>>SymmetryEdit>>有効化しました。");
                    } else {
                        keyMap.remove(sender.getName());
                        sender.sendMessage("システム>>SymmetryEdit>>無効化しました。");
                    }
                }
                break;
        }
        return true;
    }

    @EventHandler
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!keyMap.containsKey(player.getName())) return;
        setBlock(player, event.getBlock());

        player.getLevel().setBlock(getLineSymmetryVector((Location) keyMap.get(player.getName())[1], (BlockFace) keyMap.get(player.getName())[0], event.getBlock()), event.getBlock());
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!keyMap.containsKey(player.getName())) return;

        player.getLevel().setBlock(getLineSymmetryVector((Location) keyMap.get(player.getName())[1], (BlockFace) keyMap.get(player.getName())[0], event.getBlock()), Block.get(Block.AIR));
    }

    public void setBlock(Player player, Block block) {
        player.getLevel().setBlock(block.getLocation(), block);
    }

    public Vector3 getLineSymmetryVector(Location location, BlockFace bf, Block block) {
        int distance;
        if (bf.equals(BlockFace.NORTH) || bf.equals(BlockFace.SOUTH)) {
            //west<x<east
            distance = (location.getFloorX() - block.getFloorX()) * 2;
            return block.getLocation().setComponents(block.getFloorX() + distance, block.getFloorY(), block.getFloorZ());
        } else {
            //north<z<south
            distance = (location.getFloorZ() - block.getFloorZ()) * 2;
            return block.getLocation().setComponents(block.getFloorX(), block.getFloorY(), block.getFloorZ() + distance);
        }
    }
}
