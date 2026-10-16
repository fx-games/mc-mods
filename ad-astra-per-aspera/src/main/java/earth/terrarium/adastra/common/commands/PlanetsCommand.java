package earth.terrarium.adastra.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.adastra.common.menus.base.PlanetsMenuProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PlanetsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("adastra")
            .then(Commands.literal("planets")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    new PlanetsMenuProvider().openMenu(player);
                    return 1;
                })
            )
        );
    }
}
