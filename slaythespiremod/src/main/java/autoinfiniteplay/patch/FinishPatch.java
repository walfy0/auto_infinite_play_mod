package autoinfiniteplay.patch;

import autoinfiniteplay.modcore.AutoInfinite.WatcherInfinte;
import autoinfiniteplay.modcore.InfiniteAction;
import autoinfiniteplay.modcore.AutoInfinite.IconcladInfinte;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.AbstractStance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FinishPatch {
    public static final Logger logger = LogManager.getLogger(FinishPatch.class);

    @SpirePatch(clz = DrawCardAction.class, method = "update", paramtypez = {})
    public static class DrawCardAction_Post {

        @SpirePostfixPatch
        public static void Postfix(DrawCardAction __instance) {
            boolean needDraw = false;
            for(AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                if(!action.isDone && action instanceof DrawCardAction) {
                    needDraw = true;
                }
            }
            if (!needDraw && IconcladInfinte.isInfinite() != null) {
//                logger.info("[FinishPatch]start infinite by draw action, card:" + __instance.target.name);
//                logger.info(AbstractDungeon.actionManager.actions);
//                logger.info(AbstractDungeon.player.discardPile);
//                logger.info(AbstractDungeon.player.drawPile);
//                logger.info(AbstractDungeon.player.hand);
//                logger.info(AbstractDungeon.player.cardInUse);
                AbstractDungeon.actionManager.addToTop(new InfiniteAction());
            }
        }
    }

    @SpirePatch(clz = ChangeStanceAction.class, method = "update", paramtypez = {})
    public static class ChangeStanceAction_Pre {

        @SpirePrefixPatch
        public static void Prefix(ChangeStanceAction __instance) {
            String stanceId = ReflectionHacks.getPrivate(__instance, ChangeStanceAction.class, "id");
            AbstractStance oldStance = AbstractDungeon.player.stance;
            if (!oldStance.ID.equals(stanceId) && WatcherInfinte.isInfinite(stanceId) != null) {
                AbstractDungeon.actionManager.addToBottom(new InfiniteAction(stanceId));
            }
        }
    }
}
