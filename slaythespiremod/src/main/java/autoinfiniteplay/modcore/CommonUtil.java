package autoinfiniteplay.modcore;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtil {
    public static final Logger logger = LogManager.getLogger(CommonUtil.class);

    public static void print() {
        logger.info("[CommonUtil]print hand");
        AbstractDungeon.player.hand.group.forEach(logger::info);
        logger.info("[CommonUtil]print discard");
        AbstractDungeon.player.discardPile.group.forEach(logger::info);
        logger.info("[CommonUtil]print draw");
        AbstractDungeon.player.drawPile.group.forEach(logger::info);
        logger.info("[CommonUtil]print card in use");
        logger.info(AbstractDungeon.player.cardInUse);
        for(AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            if(!action.isDone) {
                logger.info("[CommonUtil]action: " + action);
            }
        }
    }
}
