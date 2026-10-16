package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.entities.mob.lunarians.LunarianMerchantOffers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class LunarianWanderingTrader extends WanderingTrader {

    public LunarianWanderingTrader(EntityType<? extends WanderingTrader> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        LunarianMerchantOffers.ItemListing[] factorys = LunarianMerchantOffers.WANDERING_TRADER_TRADES.get(1);
        LunarianMerchantOffers.ItemListing[] factorys2 = LunarianMerchantOffers.WANDERING_TRADER_TRADES.get(2);
        if (factorys == null || factorys2 == null) return;
        MerchantOffers tradeOfferList = this.getOffers();
        LunarianMerchantOffers.addOffers(this, this.random, tradeOfferList, factorys, 5);
        int i = this.random.nextInt(factorys2.length);
        LunarianMerchantOffers.ItemListing factory = factorys2[i];
        MerchantOffer tradeOffer = factory.getOffer(this, this.random);
        if (tradeOffer != null) {
            tradeOfferList.add(tradeOffer);
        }
    }
}
